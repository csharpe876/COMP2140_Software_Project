# Volunteer Management System - Java Backend Implementation

## Overview

This is a Java-based web application for managing volunteers and events, built with:
- **Java Servlets** for MVC Controllers
- **JSP** for Views
- **JDBC** with HikariCP for Database
- **Maven** for dependency management
- **MySQL** for data storage

## Project Structure (Java)

```
COMP2140_Software_Project/
├── pom.xml                          # Maven configuration
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/volunteermanagement/
│   │   │       ├── controller/      # Servlets (Controllers)
│   │   │       │   ├── AuthServlet.java
│   │   │       │   ├── EventServlet.java
│   │   │       │   └── VolunteerServlet.java
│   │   │       ├── dao/             # Data Access Objects
│   │   │       │   ├── UserDAO.java
│   │   │       │   ├── VolunteerDAO.java
│   │   │       │   ├── EventDAO.java
│   │   │       │   └── EventRegistrationDAO.java
│   │   │       ├── model/           # Entity classes (POJOs)
│   │   │       │   ├── User.java
│   │   │       │   ├── Volunteer.java
│   │   │       │   ├── Event.java
│   │   │       │   └── EventRegistration.java
│   │   │       ├── filter/          # Request filters
│   │   │       │   ├── AuthenticationFilter.java
│   │   │       │   ├── AuthorizationFilter.java
│   │   │       │   └── CharacterEncodingFilter.java
│   │   │       ├── listener/        # Context listeners
│   │   │       │   └── ApplicationListener.java
│   │   │       └── util/            # Utility classes
│   │   │           ├── DatabaseUtil.java
│   │   │           ├── PasswordUtil.java
│   │   │           └── ValidationUtil.java
│   │   ├── resources/
│   │   │   └── application.properties
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── web.xml          # Deployment descriptor
│   │       │   └── views/           # JSP files
│   │       │       ├── auth/
│   │       │       │   ├── login.jsp
│   │       │       │   └── register.jsp
│   │       │       ├── events/
│   │       │       │   ├── list.jsp
│   │       │       │   ├── details.jsp
│   │       │       │   ├── create.jsp
│   │       │       │   └── my-events.jsp
│   │       │       ├── volunteers/
│   │       │       │   ├── profile.jsp
│   │       │       │   └── list.jsp
│   │       │       ├── includes/
│   │       │       │   ├── header.jsp
│   │       │       │   └── footer.jsp
│   │       │       ├── error/
│   │       │       │   ├── 404.jsp
│   │       │       │   ├── 500.jsp
│   │       │       │   └── error.jsp
│   │       │       └── dashboard.jsp
│   │       ├── css/                 # Stylesheets
│   │       │   └── style.css
│   │       ├── js/                  # JavaScript files
│   │       │   ├── main.js
│   │       │   ├── events.js
│   │       │   └── volunteers.js
│   │       ├── images/              # Image assets
│   │       └── index.jsp            # Entry point
│   └── test/                        # Unit tests
│       └── java/
└── database/
    └── schema.sql                   # Database schema (unchanged)
```

## System Requirements

- **JDK**: Java Development Kit 11 or higher
- **Maven**: 3.6 or higher
- **Tomcat**: Apache Tomcat 9.0 or higher
- **MySQL**: 5.7 or higher
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions

## Installation Instructions

### 1. Install Prerequisites

#### Install Java JDK 11+
1. Download from [Oracle](https://www.oracle.com/java/technologies/javase-downloads.html) or use OpenJDK
2. Set JAVA_HOME environment variable
3. Verify: `java -version`

#### Install Apache Maven
1. Download from [Maven](https://maven.apache.org/download.cgi)
2. Extract to a directory (e.g., `C:\Program Files\Apache\maven`)
3. Add Maven bin directory to PATH
4. Verify: `mvn -version`

#### Install Apache Tomcat 9
1. Download from [Tomcat](https://tomcat.apache.org/download-90.cgi)
2. Extract to a directory (e.g., `C:\Program Files\Apache\Tomcat 9.0`)
3. Note the installation directory

### 2. Set Up MySQL Database

```bash
# Same as PHP version - run schema.sql
mysql -u root -p < database/schema.sql
```

Or use phpMyAdmin as described in the original INSTALLATION.md

### 3. Configure Database Connection

Edit `src/main/resources/application.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/volunteer_management?useSSL=false&serverTimezone=UTC
db.username=root
db.password=
```

### 4. Build the Project

```bash
# Navigate to project directory
cd C:\xampp\htdocs\COMP2140_Software_Project

# Clean and build
mvn clean package

# This creates: target/volunteer-management.war
```

### 5. Deploy to Tomcat

**Option A: Copy WAR file**
1. Copy `target/volunteer-management.war` to Tomcat's `webapps` directory
2. Start Tomcat
3. Access: http://localhost:8080/volunteer-management

**Option B: Use Maven Tomcat Plugin**
```bash
mvn tomcat7:run
```
Access: http://localhost:8080/volunteer-management

**Option C: IDE Deployment**
- Configure Tomcat server in your IDE
- Deploy the project
- Run/Debug from IDE

### 6. Access the Application

- **URL**: http://localhost:8080/volunteer-management
- **Admin**: username=`admin`, password=`admin123`
- **Volunteer**: username=`johndoe`, password=`volunteer123`

## Key Differences from PHP Version

### Architecture

| Component | PHP | Java |
|-----------|-----|------|
| Controller | PHP files | Servlet classes |
| View | PHP files | JSP files |
| Model | PHP classes | POJO + DAO |
| Routing | .php URLs | Servlet mappings |
| Session | $_SESSION | HttpSession |
| Template | PHP echo | JSTL tags |

### Code Patterns

**PHP:**
```php
<?php echo $user->getName(); ?>
```

**Java (JSP):**
```jsp
${user.name}
```

### Dependency Management

- **PHP**: Manual includes, composer (optional)
- **Java**: Maven with pom.xml

### Deployment

- **PHP**: Copy files to web server
- **Java**: Build WAR, deploy to Tomcat

## Development Workflow

### 1. Make Code Changes

Edit Java files in `src/main/java/` or JSP files in `src/main/webapp/WEB-INF/views/`

### 2. Rebuild

```bash
mvn clean compile
```

### 3. Test

```bash
mvn test
```

### 4. Package

```bash
mvn package
```

### 5. Deploy

Redeploy WAR to Tomcat or use hot-swap in IDE

## Maven Commands

```bash
# Compile only
mvn compile

# Run tests
mvn test

# Package as WAR
mvn package

# Clean build artifacts
mvn clean

# Install to local repository
mvn install

# Skip tests
mvn package -DskipTests

# Run with embedded Tomcat
mvn tomcat7:run

# Generate project documentation
mvn site
```

## Database Connection Pooling

The application uses HikariCP for connection pooling:

- **Max Pool Size**: 10 connections
- **Min Idle**: 5 connections
- **Connection Timeout**: 30 seconds

Configure in `application.properties`.

## Security Features

1. **Password Hashing**: BCrypt with cost factor 10
2. **SQL Injection Prevention**: Prepared statements
3. **XSS Prevention**: Input sanitization
4. **Session Management**: HttpSession with timeout
5. **Authentication Filter**: Protects restricted pages
6. **Authorization Filter**: Role-based access control

## Logging

The application uses SLF4J with simple logging:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

private static final Logger logger = LoggerFactory.getLogger(YourClass.class);
logger.info("Message");
logger.error("Error message", exception);
```

## Troubleshooting

### Port Already in Use
```bash
# Change Tomcat port in conf/server.xml
<Connector port="8080" ... />
```

### Maven Build Fails
```bash
# Update dependencies
mvn clean install -U

# Verify Java version
java -version
mvn -version
```

### Database Connection Error
- Check MySQL is running
- Verify connection string in application.properties
- Test connection with MySQL client

### ClassNotFoundException
- Ensure all dependencies in pom.xml
- Run `mvn clean install`
- Check Tomcat lib folder for MySQL connector

### JSP Not Found (404)
- Verify JSP files are in WEB-INF/views/
- Check servlet mappings in web.xml
- Ensure WAR is properly deployed

## IDE Setup

### IntelliJ IDEA
1. Import as Maven project
2. Configure Tomcat: Run → Edit Configurations → + → Tomcat Server
3. Add deployment: target/volunteer-management.war
4. Run/Debug

### Eclipse
1. Import as Existing Maven Project
2. Right-click → Run As → Run on Server
3. Select Tomcat server

### VS Code
1. Install "Extension Pack for Java"
2. Install "Tomcat for Java"
3. Open folder, build with Maven
4. Deploy to Tomcat

## Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserDAOTest

# Run with coverage
mvn test jacoco:report
```

## Production Deployment

1. Update application.properties for production
2. Set strong database password
3. Enable HTTPS in Tomcat
4. Set error-page handlers
5. Configure logging properly
6. Remove test accounts
7. Build production WAR:
   ```bash
   mvn clean package -Pprod
   ```

## Performance Optimization

1. **Connection Pooling**: Configured via HikariCP
2. **Prepared Statements**: Reused for efficiency
3. **Caching**: Consider adding Redis/Ehcache
4. **Compression**: Enable in Tomcat
5. **Static Resources**: Serve via Apache/Nginx

## Migration Status

### ✅ Completed
- Maven project structure
- Database utilities (HikariCP)
- Entity models (User, Volunteer, Event)
- User DAO with authentication
- Configuration files

### 🚧 In Progress
- Remaining DAOs (Volunteer, Event, EventRegistration)
- Servlet controllers
- JSP views conversion
- Filters and listeners

### 📋 To Do
- Complete all servlets
- Convert all PHP views to JSP
- Update JavaScript paths
- Integration testing
- Documentation updates

## Additional Resources

- [Servlet API Documentation](https://javaee.github.io/servlet-spec/)
- [JSP Documentation](https://javaee.github.io/javaee-spec/javadocs/)
- [Maven Guide](https://maven.apache.org/guides/)
- [Tomcat Documentation](https://tomcat.apache.org/tomcat-9.0-doc/)
- [HikariCP](https://github.com/brettwooldridge/HikariCP)

## Support

For issues or questions:
- Check troubleshooting section
- Review logs in Tomcat logs directory
- Contact: info@volunteermanagement.com

---

**Note**: This is a partial implementation. Additional servlets, DAOs, and JSP files need to be created to complete the full conversion from PHP to Java.
