-- Volunteer Management System Database Schema
-- MySQL 8.0+

CREATE DATABASE IF NOT EXISTS volunteer_management
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE volunteer_management;

-- Users table (base authentication and user info)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role ENUM('volunteer', 'admin') DEFAULT 'volunteer',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volunteers table (extended profile for volunteers)
CREATE TABLE IF NOT EXISTS volunteers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    skills TEXT,
    availability TEXT,
    experience TEXT,
    interests TEXT,
    emergency_contact VARCHAR(100),
    emergency_phone VARCHAR(20),
    status ENUM('active', 'inactive', 'suspended') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Events table
CREATE TABLE IF NOT EXISTS events (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    location VARCHAR(255),
    event_date DATE NOT NULL,
    start_time TIME,
    end_time TIME,
    volunteers_needed INT DEFAULT 0,
    volunteers_registered INT DEFAULT 0,
    category VARCHAR(50),
    status ENUM('active', 'completed', 'cancelled') DEFAULT 'active',
    created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id),
    INDEX idx_event_date (event_date),
    INDEX idx_status (status),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Event registrations table
CREATE TABLE IF NOT EXISTS event_registrations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    volunteer_id INT,
    user_id INT NOT NULL,
    status ENUM('confirmed', 'pending', 'cancelled', 'attended', 'no_show') DEFAULT 'pending',
    notes TEXT,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (volunteer_id) REFERENCES volunteers(id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_registration (event_id, user_id),
    INDEX idx_status (status),
    INDEX idx_event (event_id),
    INDEX idx_volunteer (volunteer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Sample data for testing
INSERT INTO users (username, email, password_hash, full_name, phone, role) VALUES
('admin', 'admin@volunteer.org', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYVar5zzHuW', 'System Admin', '555-0001', 'admin'),
('john_doe', 'john@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYVar5zzHuW', 'John Doe', '555-0002', 'volunteer'),
('jane_smith', 'jane@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYVar5zzHuW', 'Jane Smith', '555-0003', 'volunteer')
ON DUPLICATE KEY UPDATE username=username;
-- Password for all test users: password123

INSERT INTO volunteers (user_id, skills, availability, experience, interests, emergency_contact, emergency_phone) VALUES
((SELECT id FROM users WHERE username = 'john_doe'), 'First Aid, Event Planning', 'Weekends, Evenings', '2 years volunteer experience', 'Community service, Education', 'Mary Doe', '555-1001'),
((SELECT id FROM users WHERE username = 'jane_smith'), 'Teaching, Fundraising', 'Weekdays', '5 years volunteer experience', 'Youth programs, Environment', 'Bob Smith', '555-1002')
ON DUPLICATE KEY UPDATE user_id=user_id;

INSERT INTO events (title, description, location, event_date, start_time, end_time, volunteers_needed, category, created_by) VALUES
('Community Food Drive', 'Help collect and distribute food to families in need', 'Community Center', DATE_ADD(CURDATE(), INTERVAL 7 DAY), '09:00:00', '17:00:00', 20, 'Community Service', (SELECT id FROM users WHERE username = 'admin')),
('Park Cleanup Day', 'Join us to clean up the local park and plant trees', 'Central Park', DATE_ADD(CURDATE(), INTERVAL 14 DAY), '08:00:00', '14:00:00', 15, 'Environment', (SELECT id FROM users WHERE username = 'admin')),
('Youth Tutoring Program', 'Tutor elementary school students in reading and math', 'Local Library', DATE_ADD(CURDATE(), INTERVAL 21 DAY), '15:00:00', '18:00:00', 10, 'Education', (SELECT id FROM users WHERE username = 'admin'))
ON DUPLICATE KEY UPDATE title=title;
