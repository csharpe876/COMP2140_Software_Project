# Database Security Implementation Guide

## Overview

This document describes the comprehensive security measures implemented for the Volunteer Management System database to prevent unauthorized modifications and ensure data integrity.

## Security Implementation

### 1. Dedicated Database User (Principle of Least Privilege)

**Purpose**: Prevent the application from using root credentials, limiting potential damage from SQL injection attacks.

**Implementation**:
```sql
-- Create dedicated user
CREATE USER 'volunteer_app'@'localhost' IDENTIFIED BY 'V0lunt33r$ecur3P@ss2024!';

-- Grant only necessary privileges
GRANT SELECT, INSERT, UPDATE, DELETE ON volunteer_management.* TO 'volunteer_app'@'localhost';

-- Explicitly revoke dangerous privileges
REVOKE CREATE, DROP, ALTER, INDEX ON volunteer_management.* FROM 'volunteer_app'@'localhost';
```

**Application Configuration**:
Update `src/main/resources/application.properties`:
```properties
db.username=volunteer_app
db.password=V0lunt33r$ecur3P@ss2024!
```

### 2. Audit Trail System

**Purpose**: Track all critical database operations for security monitoring and compliance.

**Implementation**:
- Audit log table stores all sensitive operations
- Triggers automatically log user deletions and role/password changes
- Append-only access (no updates/deletes allowed)

**Tables**:
- `audit_log` - Stores operation history with timestamps

**Triggers**:
- `before_user_delete` - Logs user account deletions
- `before_user_update` - Logs password and role changes

**Usage**:
```sql
-- View recent audit events
SELECT * FROM audit_log ORDER BY timestamp DESC LIMIT 50;

-- View suspicious activities
SELECT * FROM audit_log 
WHERE operation = 'DELETE' OR new_values LIKE '%admin%'
ORDER BY timestamp DESC;
```

### 3. Data Integrity Constraints

**Purpose**: Prevent invalid data entry at the database level.

**Constraints Implemented**:

```sql
-- Event dates must be reasonable (not too far in past)
ALTER TABLE events 
ADD CONSTRAINT chk_event_date 
CHECK (event_date >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR));

-- Volunteers needed must be positive and reasonable
ALTER TABLE events 
ADD CONSTRAINT chk_volunteers_needed 
CHECK (volunteers_needed > 0 AND volunteers_needed <= 1000);

-- Email format validation
ALTER TABLE users 
ADD CONSTRAINT chk_email_format 
CHECK (email REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

-- Phone format validation
ALTER TABLE users 
ADD CONSTRAINT chk_phone_format 
CHECK (phone REGEXP '^[0-9+()-]{7,20}$');
```

### 4. Secure Views (Data Hiding)

**Purpose**: Hide sensitive information like passwords from queries.

**Views Created**:

```sql
-- Safe user view (no passwords)
CREATE VIEW v_users_safe AS
SELECT id, username, email, full_name, phone, role, created_at
FROM users;

-- Volunteer profiles with user info
CREATE VIEW v_volunteer_profiles AS
SELECT v.id, v.user_id, u.username, u.email, u.full_name, 
       v.skills, v.availability, v.status
FROM volunteers v
INNER JOIN users u ON v.user_id = u.id;

-- Public events view
CREATE VIEW v_events_public AS
SELECT e.id, e.title, e.description, e.location, 
       e.event_date, e.volunteers_needed, e.category
FROM events e
WHERE e.status = 'active';
```

### 5. Performance & Security Indexes

**Purpose**: Improve query performance and prevent timing attacks.

**Indexes Created**:
```sql
-- Login performance and timing attack prevention
CREATE INDEX idx_users_login ON users(username, password);

-- Faster lookups
CREATE INDEX idx_volunteer_user ON volunteers(user_id, status);
CREATE INDEX idx_event_date_status ON events(event_date, status);
CREATE INDEX idx_registration_volunteer ON event_registrations(volunteer_id, status);
CREATE INDEX idx_registration_event ON event_registrations(event_id, status);
```

### 6. Stored Procedures for Complex Operations

**Purpose**: Encapsulate business logic and prevent SQL injection.

**Procedures Available**:

#### User Authentication
```sql
CALL sp_authenticate_user('username');
-- Returns user details without password in plain text
-- Use in Java: UserDAO.authenticate() with BCrypt verification
```

#### Create Volunteer Profile
```sql
CALL sp_create_volunteer(
    user_id, skills, availability, experience, 
    interests, emergency_contact, emergency_phone, 
    @volunteer_id
);
-- Returns volunteer_id or 0 on failure
-- Includes validation and transaction management
```

#### Event Registration with Capacity Check
```sql
CALL sp_register_for_event(
    event_id, volunteer_id, notes,
    @registration_id, @message
);
-- Atomic operation with:
-- - Event capacity checking
-- - Duplicate registration prevention
-- - Status validation
```

## Application-Level Security

### 1. Prepared Statements (SQL Injection Prevention)

**All DAO classes use prepared statements**:

```java
// CORRECT - Prevents SQL injection
String sql = "SELECT * FROM users WHERE username = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setString(1, username);

// WRONG - Vulnerable to SQL injection
String sql = "SELECT * FROM users WHERE username = '" + username + "'";
```

### 2. Password Hashing (BCrypt)

**Implementation in PasswordUtil.java**:

```java
// Hash password before storing
String hashedPassword = PasswordUtil.hashPassword(plainPassword);

// Verify during login
boolean valid = PasswordUtil.verifyPassword(plainPassword, hashedPassword);
```

**Strength**: BCrypt cost factor = 10 (2^10 iterations)

### 3. Input Validation & Sanitization

**Implementation in ValidationUtil.java**:

```java
// Sanitize all user input
String clean = ValidationUtil.sanitize(userInput);

// Validate email
if (!ValidationUtil.isValidEmail(email)) {
    throw new ValidationException("Invalid email format");
}

// Validate phone
if (!ValidationUtil.isValidPhone(phone)) {
    throw new ValidationException("Invalid phone format");
}
```

### 4. Connection Pooling Security

**HikariCP Configuration**:

```properties
# Limit maximum connections
db.pool.maximumPoolSize=10

# Connection timeout prevents resource exhaustion
db.pool.connectionTimeout=30000

# Detect and remove broken connections
db.pool.idleTimeout=600000
db.pool.maxLifetime=1800000
```

## Deployment Security Checklist

### Production Deployment Steps:

1. **Change Default Passwords**
   ```sql
   -- Generate new BCrypt hash for admin password
   UPDATE users SET password = '$2a$10$NEW_HASH_HERE' WHERE username = 'admin';
   
   -- Remove test accounts
   DELETE FROM users WHERE username IN ('johndoe', 'janesmit', 'mikejohn');
   ```

2. **Update Database Credentials**
   ```properties
   # application.properties
   db.username=volunteer_app
   db.password=STRONG_RANDOM_PASSWORD_HERE
   ```

3. **Run Security Script**
   ```bash
   mysql -u root -p < database/security.sql
   ```

4. **Enable SSL for Database Connection**
   ```properties
   db.url=jdbc:mysql://localhost:3306/volunteer_management?useSSL=true&requireSSL=true
   ```

5. **Restrict Database Network Access**
   ```sql
   -- Allow connections only from application server
   CREATE USER 'volunteer_app'@'192.168.1.100' IDENTIFIED BY 'password';
   
   -- Remove localhost user if not needed
   DROP USER 'volunteer_app'@'localhost';
   ```

6. **Enable Binary Logging** (in my.cnf/my.ini)
   ```ini
   [mysqld]
   log-bin=mysql-bin
   binlog_format=ROW
   expire_logs_days=7
   ```

7. **Regular Backups**
   ```bash
   # Daily backup script
   mysqldump -u root -p volunteer_management > backup_$(date +%Y%m%d).sql
   ```

8. **Monitor Audit Log**
   ```sql
   -- Check for suspicious activities daily
   SELECT * FROM audit_log 
   WHERE timestamp >= DATE_SUB(NOW(), INTERVAL 24 HOUR)
   ORDER BY timestamp DESC;
   ```

## Security Monitoring

### Key Metrics to Monitor:

1. **Failed Login Attempts**
   - Track in application logs
   - Implement rate limiting after 5 failed attempts

2. **Audit Log Review**
   - Daily review of DELETE operations
   - Weekly review of role changes
   - Monitor for unexpected UPDATE patterns

3. **Database Performance**
   - Slow query log analysis
   - Connection pool statistics
   - Index usage monitoring

4. **Access Patterns**
   - Unusual query volumes
   - Off-hours database access
   - Geographic anomalies (if tracked)

## Incident Response

### If Security Breach Suspected:

1. **Immediate Actions**:
   ```sql
   -- Disable application user
   ALTER USER 'volunteer_app'@'localhost' ACCOUNT LOCK;
   
   -- Review audit log
   SELECT * FROM audit_log ORDER BY timestamp DESC LIMIT 1000;
   ```

2. **Investigation**:
   - Check application logs for suspicious activities
   - Review database access logs
   - Analyze audit trail for unauthorized changes

3. **Recovery**:
   - Restore from backup if data corrupted
   - Change all passwords
   - Update security configurations
   - Patch vulnerabilities

4. **Prevention**:
   - Implement additional monitoring
   - Update security policies
   - Train development team

## Testing Security

### Security Test Checklist:

- [ ] SQL injection attempts fail
- [ ] XSS attempts are sanitized
- [ ] Weak passwords are rejected
- [ ] Rate limiting works on login
- [ ] Audit log captures all critical operations
- [ ] Database constraints prevent invalid data
- [ ] Prepared statements used everywhere
- [ ] Connection pool limits respected
- [ ] Unauthorized access attempts blocked
- [ ] Sensitive data not exposed in logs

### Testing SQL Injection Prevention:

```java
// Test cases that should NOT work:
String[] maliciousInputs = {
    "admin' OR '1'='1",
    "admin'; DROP TABLE users--",
    "admin' UNION SELECT * FROM users--",
    "' OR 1=1#"
};

// All should fail safely without compromising database
```

## Additional Resources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [MySQL Security Best Practices](https://dev.mysql.com/doc/refman/8.0/en/security-guidelines.html)
- [Java Security Guidelines](https://www.oracle.com/java/technologies/javase/seccodeguide.html)
- [BCrypt Information](https://en.wikipedia.org/wiki/Bcrypt)

## Support

For security concerns or questions:
- Email: security@volunteermanagement.com
- Review audit logs regularly
- Keep all software updated

---

**Last Updated**: November 29, 2025  
**Security Level**: Production Ready  
**Compliance**: OWASP Recommended Practices
