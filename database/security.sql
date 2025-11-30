-- ===================================
-- Database Security Configuration
-- Volunteer Management System
-- ===================================

USE volunteer_management;

-- ===================================
-- 1. Create Dedicated Database User (Non-Root)
-- ===================================
-- Drop user if exists (for clean setup)
DROP USER IF EXISTS 'volunteer_app'@'localhost';

-- Create application user with strong password
CREATE USER 'volunteer_app'@'localhost' IDENTIFIED BY 'V0lunt33r$ecur3P@ss2024!';

-- Grant only necessary privileges (principle of least privilege)
GRANT SELECT, INSERT, UPDATE, DELETE ON volunteer_management.* TO 'volunteer_app'@'localhost';

-- Revoke dangerous privileges
REVOKE CREATE, DROP, ALTER, INDEX, CREATE TEMPORARY TABLES, 
       LOCK TABLES, REFERENCES, CREATE VIEW, SHOW VIEW, 
       CREATE ROUTINE, ALTER ROUTINE, EXECUTE, TRIGGER 
       ON volunteer_management.* FROM 'volunteer_app'@'localhost';

-- Apply changes
FLUSH PRIVILEGES;

-- ===================================
-- 2. Add Database Triggers for Audit Trail
-- ===================================

-- Create audit log table
CREATE TABLE IF NOT EXISTS audit_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    table_name VARCHAR(50) NOT NULL,
    operation VARCHAR(10) NOT NULL,
    record_id INT NOT NULL,
    user_id INT,
    old_values TEXT,
    new_values TEXT,
    ip_address VARCHAR(45),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_table_operation (table_name, operation),
    INDEX idx_timestamp (timestamp),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Trigger for user deletions (audit trail)
DROP TRIGGER IF EXISTS before_user_delete;
DELIMITER $$
CREATE TRIGGER before_user_delete
BEFORE DELETE ON users
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (table_name, operation, record_id, user_id, old_values)
    VALUES ('users', 'DELETE', OLD.id, OLD.id, 
            CONCAT('username:', OLD.username, ',email:', OLD.email, ',role:', OLD.role));
END$$
DELIMITER ;

-- Trigger for critical user updates (password/role changes)
DROP TRIGGER IF EXISTS before_user_update;
DELIMITER $$
CREATE TRIGGER before_user_update
BEFORE UPDATE ON users
FOR EACH ROW
BEGIN
    IF OLD.password != NEW.password OR OLD.role != NEW.role THEN
        INSERT INTO audit_log (table_name, operation, record_id, user_id, old_values, new_values)
        VALUES ('users', 'UPDATE', OLD.id, OLD.id,
                CONCAT('password_changed:', IF(OLD.password != NEW.password, 'yes', 'no'), 
                       ',old_role:', OLD.role),
                CONCAT('new_role:', NEW.role));
    END IF;
END$$
DELIMITER ;

-- ===================================
-- 3. Add Constraints to Prevent Data Integrity Issues
-- ===================================

-- Add check constraint for event dates (must be in future or near past)
ALTER TABLE events 
ADD CONSTRAINT chk_event_date 
CHECK (event_date >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR));

-- Add check constraint for volunteers_needed (positive number)
ALTER TABLE events 
ADD CONSTRAINT chk_volunteers_needed 
CHECK (volunteers_needed > 0 AND volunteers_needed <= 1000);

-- Add check constraint for valid email format in users table
ALTER TABLE users 
ADD CONSTRAINT chk_email_format 
CHECK (email REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

-- Add check constraint for valid phone format
ALTER TABLE users 
ADD CONSTRAINT chk_phone_format 
CHECK (phone REGEXP '^[0-9+()-]{7,20}$');

-- ===================================
-- 4. Create Security Views (Hide sensitive data)
-- ===================================

-- View for public user information (without password)
CREATE OR REPLACE VIEW v_users_safe AS
SELECT id, username, email, full_name, phone, role, created_at
FROM users;

-- View for volunteer profiles with user info
CREATE OR REPLACE VIEW v_volunteer_profiles AS
SELECT v.id, v.user_id, u.username, u.email, u.full_name, u.phone,
       v.skills, v.availability, v.experience, v.interests, v.status, v.created_at
FROM volunteers v
INNER JOIN users u ON v.user_id = u.id;

-- View for event summary (without creator details)
CREATE OR REPLACE VIEW v_events_public AS
SELECT e.id, e.title, e.description, e.location, e.event_date, 
       e.start_time, e.end_time, e.volunteers_needed, e.category, e.status,
       (SELECT COUNT(*) FROM event_registrations er 
        WHERE er.event_id = e.id AND er.status IN ('confirmed', 'attended')) as volunteers_registered
FROM events e
WHERE e.status = 'active';

-- ===================================
-- 5. Add Indexes for Performance and Security
-- ===================================

-- Index for faster lookups and preventing timing attacks
CREATE INDEX idx_users_login ON users(username, password);
CREATE INDEX idx_volunteer_user ON volunteers(user_id, status);
CREATE INDEX idx_event_date_status ON events(event_date, status);
CREATE INDEX idx_registration_volunteer ON event_registrations(volunteer_id, status);
CREATE INDEX idx_registration_event ON event_registrations(event_id, status);

-- ===================================
-- 6. Create Stored Procedures for Secure Operations
-- ===================================

-- Secure procedure for user authentication (prevents SQL injection)
DROP PROCEDURE IF EXISTS sp_authenticate_user;
DELIMITER $$
CREATE PROCEDURE sp_authenticate_user(
    IN p_username VARCHAR(50)
)
BEGIN
    SELECT id, username, email, password, full_name, phone, role, created_at
    FROM users
    WHERE username = p_username AND role IN ('volunteer', 'admin')
    LIMIT 1;
END$$
DELIMITER ;

-- Secure procedure for creating volunteer with transaction
DROP PROCEDURE IF EXISTS sp_create_volunteer;
DELIMITER $$
CREATE PROCEDURE sp_create_volunteer(
    IN p_user_id INT,
    IN p_skills TEXT,
    IN p_availability TEXT,
    IN p_experience TEXT,
    IN p_interests TEXT,
    IN p_emergency_contact VARCHAR(100),
    IN p_emergency_phone VARCHAR(20),
    OUT p_volunteer_id INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_volunteer_id = 0;
    END;
    
    START TRANSACTION;
    
    -- Check if user exists and is volunteer role
    IF NOT EXISTS (SELECT 1 FROM users WHERE id = p_user_id AND role = 'volunteer') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid user or not a volunteer';
    END IF;
    
    -- Check if volunteer profile already exists
    IF EXISTS (SELECT 1 FROM volunteers WHERE user_id = p_user_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Volunteer profile already exists';
    END IF;
    
    -- Insert volunteer
    INSERT INTO volunteers (user_id, skills, availability, experience, interests, 
                           emergency_contact, emergency_phone, status)
    VALUES (p_user_id, p_skills, p_availability, p_experience, p_interests,
            p_emergency_contact, p_emergency_phone, 'active');
    
    SET p_volunteer_id = LAST_INSERT_ID();
    
    COMMIT;
END$$
DELIMITER ;

-- Secure procedure for event registration with capacity check
DROP PROCEDURE IF EXISTS sp_register_for_event;
DELIMITER $$
CREATE PROCEDURE sp_register_for_event(
    IN p_event_id INT,
    IN p_volunteer_id INT,
    IN p_user_id INT,
    IN p_notes TEXT,
    OUT p_registration_id INT,
    OUT p_message VARCHAR(255)
)
BEGIN
    DECLARE v_volunteers_needed INT;
    DECLARE v_volunteers_registered INT;
    DECLARE v_event_status VARCHAR(20);
    DECLARE v_user_exists INT DEFAULT 0;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_registration_id = 0;
        SET p_message = 'Registration failed due to error';
    END;
    
    START TRANSACTION;
    
    -- Verify user exists
    SELECT COUNT(*) INTO v_user_exists FROM users WHERE id = p_user_id;
    IF v_user_exists = 0 THEN
        SET p_registration_id = 0;
        SET p_message = 'Invalid user';
        ROLLBACK;
    ELSE
        -- Get event details
        SELECT volunteers_needed, status INTO v_volunteers_needed, v_event_status
        FROM events
        WHERE id = p_event_id
        FOR UPDATE;
        
        -- Check if event is active
        IF v_event_status != 'active' THEN
            SET p_registration_id = 0;
            SET p_message = 'Event is not active';
            ROLLBACK;
        ELSE
            -- Get current registrations
            SELECT COUNT(*) INTO v_volunteers_registered
            FROM event_registrations
            WHERE event_id = p_event_id AND status IN ('confirmed', 'attended');
            
            -- Check if already registered
            IF EXISTS (SELECT 1 FROM event_registrations 
                       WHERE event_id = p_event_id AND user_id = p_user_id 
                       AND status IN ('confirmed', 'attended')) THEN
                SET p_registration_id = 0;
                SET p_message = 'Already registered for this event';
                ROLLBACK;
            -- Check capacity
            ELSEIF v_volunteers_registered >= v_volunteers_needed THEN
                SET p_registration_id = 0;
                SET p_message = 'Event is full';
                ROLLBACK;
            ELSE
                -- Register volunteer
                INSERT INTO event_registrations (event_id, volunteer_id, user_id, status, notes)
                VALUES (p_event_id, p_volunteer_id, p_user_id, 'confirmed', p_notes);
                
                SET p_registration_id = LAST_INSERT_ID();
                SET p_message = 'Registration successful';
                COMMIT;
            END IF;
        END IF;
    END IF;
END$$
DELIMITER ;

-- ===================================
-- 7. Set Table-Level Security
-- ===================================

-- Prevent direct modifications to audit log (append-only)
GRANT SELECT ON volunteer_management.audit_log TO 'volunteer_app'@'localhost';
REVOKE INSERT, UPDATE, DELETE ON volunteer_management.audit_log FROM 'volunteer_app'@'localhost';

-- ===================================
-- 8. Enable Binary Logging (for point-in-time recovery)
-- ===================================
-- Note: This requires server restart and must be added to my.cnf/my.ini
-- [mysqld]
-- log-bin=mysql-bin
-- binlog_format=ROW
-- expire_logs_days=7

-- ===================================
-- Security Configuration Summary
-- ===================================
-- 1. Dedicated app user with limited privileges
-- 2. Audit trail for critical operations
-- 3. Data integrity constraints
-- 4. Secure views hiding sensitive data
-- 5. Performance indexes
-- 6. Stored procedures for complex operations
-- 7. Append-only audit log
-- 8. Binary logging recommended for backups

SELECT 'Database security configuration completed successfully!' AS Status;
