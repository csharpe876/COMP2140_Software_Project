-- ===================================
-- Volunteer Management System Database
-- ===================================
-- This script creates all necessary tables and sample data
-- for the Volunteer Management System

-- Create database
CREATE DATABASE IF NOT EXISTS volunteer_management;
USE volunteer_management;

-- ===================================
-- Table: users
-- ===================================
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    role ENUM('volunteer', 'admin') DEFAULT 'volunteer',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================================
-- Table: volunteers
-- ===================================
CREATE TABLE IF NOT EXISTS volunteers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    skills TEXT,
    availability TEXT,
    experience TEXT,
    interests TEXT,
    emergency_contact VARCHAR(100),
    emergency_phone VARCHAR(20),
    status ENUM('active', 'inactive') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================================
-- Table: events
-- ===================================
CREATE TABLE IF NOT EXISTS events (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(200) NOT NULL,
    event_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    volunteers_needed INT NOT NULL DEFAULT 1,
    category VARCHAR(50) NOT NULL,
    status ENUM('active', 'completed', 'cancelled') DEFAULT 'active',
    created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_event_date (event_date),
    INDEX idx_status (status),
    INDEX idx_category (category),
    INDEX idx_created_by (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================================
-- Table: event_registrations
-- ===================================
CREATE TABLE IF NOT EXISTS event_registrations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    volunteer_id INT,
    user_id INT NOT NULL,
    status ENUM('confirmed', 'pending', 'cancelled') DEFAULT 'confirmed',
    notes TEXT,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (volunteer_id) REFERENCES volunteers(id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_event_user (event_id, user_id),
    INDEX idx_event_id (event_id),
    INDEX idx_volunteer_id (volunteer_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===================================
-- Insert Sample Data
-- ===================================

-- Insert admin user (password: admin123)
INSERT INTO users (username, email, password, full_name, phone, role) VALUES
('admin', 'admin@volunteermanagement.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'System Administrator', '8761234567', 'admin');

-- Insert sample volunteer users (password: volunteer123)
INSERT INTO users (username, email, password, full_name, phone, role) VALUES
('johndoe', 'john.doe@email.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'John Doe', '8761234568', 'volunteer'),
('janesmit', 'jane.smith@email.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Jane Smith', '8761234569', 'volunteer'),
('mikejohn', 'mike.johnson@email.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Mike Johnson', '8761234570', 'volunteer'),
('sarahwil', 'sarah.williams@email.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Sarah Williams', '8761234571', 'volunteer'),
('davisbro', 'david.brown@email.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'David Brown', '8761234572', 'volunteer');

-- Insert volunteer profiles
INSERT INTO volunteers (user_id, skills, availability, experience, interests, emergency_contact, emergency_phone, status) VALUES
(2, 'Event Planning, Communication, Leadership', 'Weekends, Evenings', '3 years of volunteer experience in community events', 'Community Service, Education', 'Mary Doe', '8769876543', 'active'),
(3, 'Teaching, Mentoring, Public Speaking', 'Weekday afternoons, Weekends', '5 years teaching experience', 'Education, Youth Development', 'Robert Smith', '8769876544', 'active'),
(4, 'Sports Coaching, First Aid, Team Building', 'Saturdays, Sundays', '2 years coaching youth sports', 'Sports & Recreation, Healthcare', 'Lisa Johnson', '8769876545', 'active'),
(5, 'Gardening, Environmental Science, Organization', 'Flexible schedule', '4 years environmental activism', 'Environmental, Community Service', 'Tom Williams', '8769876546', 'active'),
(6, 'Fundraising, Marketing, Social Media', 'Evenings and weekends', '1 year fundraising experience', 'Fundraising, Community Service', 'Emma Brown', '8769876547', 'inactive');

-- Insert sample events
INSERT INTO events (title, description, location, event_date, start_time, end_time, volunteers_needed, category, status, created_by) VALUES
('Beach Cleanup Day', 'Join us for a community beach cleanup event. Help preserve our beautiful coastline and marine life by removing trash and debris from the beach. All supplies will be provided.', 'Hellshire Beach, Portmore', '2025-12-15', '08:00:00', '12:00:00', 20, 'Environmental', 'active', 1),
('Youth Mentorship Program', 'Mentor young students in reading and math. Share your knowledge and help students improve their academic skills. Training will be provided for all volunteers.', 'Kingston Community Center', '2025-12-20', '14:00:00', '17:00:00', 10, 'Education', 'active', 1),
('Food Bank Distribution', 'Assist with sorting and distributing food to families in need. Help make a difference in our community by ensuring everyone has access to nutritious meals.', 'Spanish Town Food Bank', '2025-12-18', '09:00:00', '13:00:00', 15, 'Community Service', 'active', 1),
('Hospital Visit Program', 'Visit patients at the local hospital and provide companionship. Bring joy and comfort to those who are ill or recovering. Orientation required before first visit.', 'Kingston Public Hospital', '2025-12-22', '15:00:00', '18:00:00', 8, 'Healthcare', 'active', 1),
('Community Sports Day', 'Organize and supervise sports activities for children and youth. Help promote health and fitness in our community. Sports equipment will be provided.', 'National Stadium', '2026-01-05', '10:00:00', '16:00:00', 12, 'Sports & Recreation', 'active', 1),
('Charity Fundraising Gala', 'Help organize and staff our annual charity gala. Duties include setup, guest reception, and coordination. Formal attire required.', 'Jamaica Pegasus Hotel', '2026-01-15', '17:00:00', '22:00:00', 10, 'Fundraising', 'active', 1),
('Tree Planting Initiative', 'Plant trees in local parks and public spaces. Help combat climate change and beautify our community. Tools and refreshments provided.', 'Hope Gardens', '2025-11-25', '07:00:00', '11:00:00', 25, 'Environmental', 'completed', 1),
('Back to School Drive', 'Sort and pack school supplies for underprivileged students. Help ensure every child has the tools they need for a successful school year.', 'May Pen Community Center', '2025-08-20', '09:00:00', '15:00:00', 15, 'Education', 'completed', 1);

-- Insert sample event registrations
INSERT INTO event_registrations (event_id, volunteer_id, user_id, status, notes) VALUES
(1, 1, 2, 'confirmed', 'Excited to help with the cleanup!'),
(1, 2, 3, 'confirmed', 'Will bring extra gloves'),
(2, 2, 3, 'confirmed', 'Have teaching experience with young children'),
(3, 3, 4, 'confirmed', NULL),
(4, 4, 5, 'confirmed', 'Looking forward to helping'),
(7, 1, 2, 'confirmed', NULL),
(7, 4, 5, 'confirmed', NULL),
(8, 2, 3, 'confirmed', NULL);

-- ===================================
-- Create Views for Reporting
-- ===================================

-- View: Active volunteers with their event count
CREATE OR REPLACE VIEW active_volunteers_summary AS
SELECT 
    v.id,
    u.full_name,
    u.email,
    u.phone,
    v.skills,
    v.status,
    COUNT(er.id) as total_events,
    v.created_at as joined_date
FROM volunteers v
JOIN users u ON v.user_id = u.id
LEFT JOIN event_registrations er ON v.id = er.volunteer_id
WHERE v.status = 'active'
GROUP BY v.id, u.full_name, u.email, u.phone, v.skills, v.status, v.created_at;

-- View: Upcoming events with registration count
CREATE OR REPLACE VIEW upcoming_events_summary AS
SELECT 
    e.id,
    e.title,
    e.description,
    e.location,
    e.event_date,
    e.start_time,
    e.end_time,
    e.volunteers_needed,
    COUNT(er.id) as volunteers_registered,
    e.category,
    e.status,
    u.full_name as created_by_name
FROM events e
LEFT JOIN event_registrations er ON e.id = er.event_id
JOIN users u ON e.created_by = u.id
WHERE e.event_date >= CURDATE() AND e.status = 'active'
GROUP BY e.id, e.title, e.description, e.location, e.event_date, e.start_time, 
         e.end_time, e.volunteers_needed, e.category, e.status, u.full_name
ORDER BY e.event_date ASC;

-- ===================================
-- Database Setup Complete
-- ===================================

-- Display summary
SELECT 'Database setup complete!' as Status;
SELECT COUNT(*) as total_users FROM users;
SELECT COUNT(*) as total_volunteers FROM volunteers;
SELECT COUNT(*) as total_events FROM events;
SELECT COUNT(*) as total_registrations FROM event_registrations;
