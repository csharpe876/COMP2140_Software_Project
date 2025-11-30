# Volunteer Management System

A comprehensive web application for managing volunteers, events, and registrations. Built with Java, Servlets, JSP, MySQL, HTML, CSS, and JavaScript following the MVC (Model-View-Controller) architecture pattern.

## 📋 Table of Contents

- [Features](#features)
- [System Requirements](#system-requirements)
- [Installation](#installation)
- [Security](#security)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [Usage](#usage)
- [User Roles](#user-roles)
- [Screenshots](#screenshots)
- [Technologies Used](#technologies-used)
- [MVC Architecture](#mvc-architecture)
- [Contributing](#contributing)
- [License](#license)

## ✨ Features

### User Management
- User registration and authentication
- Secure password hashing
- Role-based access control (Admin & Volunteer)
- User profile management

### Volunteer Management
- Volunteer profile creation and editing
- Skills and availability tracking
- Experience and interests documentation
- Emergency contact information
- Status management (Active/Inactive)
- Volunteer search and filtering

### Event Management
- Create, edit, and delete events
- Event categorization (Community Service, Environmental, Education, Healthcare, etc.)
- Date, time, and location tracking
- Volunteer capacity management
- Event status tracking (Active, Completed, Cancelled)
- Event registration system

### Registration System
- Event registration for volunteers
- Registration status tracking
- Volunteer limit enforcement
- Registration cancellation
- View personal event registrations

### Dashboard & Analytics
- Overview statistics
- Upcoming events display
- Recent registrations
- Quick navigation

### Additional Features
- Responsive design for all devices
- Form validation (client and server-side)
- Flash messages for user feedback
- Search and filter functionality
- Modern, clean UI/UX

## 💻 System Requirements

- **JDK**: Java Development Kit 11 or higher
- **Application Server**: Apache Tomcat 9.0 or higher
- **Build Tool**: Apache Maven 3.6 or higher
- **MySQL**: Version 5.7 or higher
- **Web Browser**: Modern browser (Chrome, Firefox, Edge, Safari)

## 🚀 Installation

### Step 1: Install Prerequisites

1. **Install JDK 11 or higher**
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or use OpenJDK
   - Set JAVA_HOME environment variable
   - Verify: `java -version`

2. **Install Apache Maven**
   - Download from [Maven](https://maven.apache.org/download.cgi)
   - Add Maven bin directory to PATH
   - Verify: `mvn -version`

3. **Install Apache Tomcat 9**
   - Download from [Tomcat](https://tomcat.apache.org/download-90.cgi)
   - Extract to a directory (e.g., `C:\Program Files\Apache\Tomcat 9.0`)

4. **Install MySQL**
   - Download from [MySQL](https://dev.mysql.com/downloads/installer/) or use XAMPP
   - Start MySQL service

### Step 2: Clone the Project

```bash
git clone https://github.com/csharpe876/COMP2140_Software_Project.git
cd COMP2140_Software_Project
```

### Step 3: Create the Database

1. Open phpMyAdmin: [http://localhost/phpmyadmin](http://localhost/phpmyadmin)
2. Click on "Import" tab
3. Click "Choose File" and select `database/schema.sql`
4. Click "Go" to import the database

**OR** use MySQL command line:

```bash
mysql -u root -p < database/schema.sql
```

### Step 4: Configure Database Security (Recommended)

**For production or secure development**:

```bash
# Run security configuration
mysql -u root -p < database/security.sql
```

This creates a dedicated database user and implements security measures. See [SECURITY_SETUP.md](SECURITY_SETUP.md) for details.

### Step 5: Configure Database Connection

Edit `src/main/resources/application.properties`:

**Development (using root - not recommended for production)**:
```properties
db.url=jdbc:mysql://localhost:3306/volunteer_management?useSSL=false&serverTimezone=UTC
db.username=root
db.password=
```

**Production (using secure user - recommended)**:
```properties
db.url=jdbc:mysql://localhost:3306/volunteer_management?useSSL=true&requireSSL=true&serverTimezone=UTC
db.username=volunteer_app
db.password=V0lunt33r$ecur3P@ss2024!
```

### Step 6: Build the Application

```bash
mvn clean package
```

This creates `target/volunteer-management.war`

### Step 7: Deploy to Tomcat

**Option A: Copy WAR file**
1. Copy `target/volunteer-management.war` to Tomcat's `webapps` directory
2. Start Tomcat
3. Access: http://localhost:8080/volunteer-management

**Option B: Use Maven Tomcat Plugin**
```bash
mvn tomcat7:run
```

### Step 8: Login

#### Admin Account:
- **Username**: admin
- **Password**: admin123

#### Sample Volunteer Accounts:
- **Username**: johndoe | **Password**: volunteer123
- **Username**: janesmit | **Password**: volunteer123
- **Username**: mikejohn | **Password**: volunteer123

## 📁 Project Structure

```
COMP2140_Software_Project/
├── pom.xml                          # Maven configuration
├── src/
│   ├── main/
│   │   ├── java/com/volunteermanagement/
│   │   │   ├── controller/          # Servlet Controllers
│   │   │   │   ├── AuthServlet.java
│   │   │   │   ├── EventServlet.java
│   │   │   │   └── VolunteerServlet.java
│   │   │   ├── dao/                 # Data Access Objects
│   │   │   │   ├── UserDAO.java
│   │   │   │   ├── VolunteerDAO.java
│   │   │   │   ├── EventDAO.java
│   │   │   │   └── EventRegistrationDAO.java
│   │   │   ├── model/               # Entity POJOs
│   │   │   │   ├── User.java
│   │   │   │   ├── Volunteer.java
│   │   │   │   ├── Event.java
│   │   │   │   └── EventRegistration.java
│   │   │   ├── filter/              # Request Filters
│   │   │   │   ├── AuthenticationFilter.java
│   │   │   │   ├── AuthorizationFilter.java
│   │   │   │   └── CharacterEncodingFilter.java
│   │   │   ├── listener/            # Application Listeners
│   │   │   │   └── ApplicationListener.java
│   │   │   └── util/                # Utility Classes
│   │   │       ├── DatabaseUtil.java
│   │   │       ├── PasswordUtil.java
│   │   │       └── ValidationUtil.java
│   │   ├── resources/
│   │   │   └── application.properties # Configuration
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── web.xml          # Deployment descriptor
│   │       │   └── views/           # JSP Views
│   │       │       ├── auth/
│   │       │       ├── events/
│   │       │       ├── volunteers/
│   │       │       ├── includes/
│   │       │       └── error/
│   │       ├── css/                 # Stylesheets
│   │       │   └── style.css
│   │       ├── js/                  # JavaScript files
│   │       │   ├── main.js
│   │       │   ├── events.js
│   │       │   └── volunteers.js
│   │       └── images/              # Image assets
│   └── test/                        # Unit tests
├── database/
│   ├── schema.sql                   # Database schema
│   └── security.sql                 # Security configuration
├── JAVA_README.md                   # Detailed Java documentation
├── SECURITY.md                      # Security implementation guide
├── SECURITY_SETUP.md                # Quick security setup
└── README.md                        # This file
```

## 🔒 Security

This application implements comprehensive security measures:

### Database Security
- **Dedicated Database User**: Application uses limited-privilege user (not root)
- **Audit Trail**: All critical operations logged for compliance
- **Data Constraints**: Invalid data prevented at database level
- **SQL Injection Prevention**: All queries use prepared statements
- **Secure Views**: Sensitive data hidden from queries

### Application Security
- **BCrypt Password Hashing**: Industry-standard with cost factor 10
- **XSS Prevention**: Input sanitization on all user input
- **Session Management**: Secure session handling with timeout
- **Connection Pooling**: Resource exhaustion prevention
- **Input Validation**: Email, phone, and data format validation

### Quick Security Setup
```bash
# Run security configuration script
mysql -u root -p < database/security.sql
```

**For detailed security documentation**, see:
- [SECURITY.md](SECURITY.md) - Complete security guide
- [SECURITY_SETUP.md](SECURITY_SETUP.md) - Quick setup instructions

## 🗄️ Database Schema

### Tables:

1. **users** - User accounts and authentication
2. **volunteers** - Volunteer profile information
3. **events** - Event details and information
4. **event_registrations** - Event registration records
5. **audit_log** - Security audit trail (created by security.sql)

### Relationships:

- Users → Volunteers (One-to-One)
- Users → Events (One-to-Many, as creator)
- Events → Event Registrations (One-to-Many)
- Volunteers → Event Registrations (One-to-Many)

## 📖 Usage

### For Volunteers:

1. **Register an Account**
   - Click "Register" on the login page
   - Fill in your personal information
   - Create a username and password

2. **Complete Your Profile**
   - Navigate to "My Profile"
   - Add your skills, availability, and experience
   - Add emergency contact information

3. **Browse Events**
   - Go to "Events" to see all available events
   - Filter by category or status
   - Search for specific events

4. **Register for Events**
   - Click on an event to view details
   - Click "Register" if you want to volunteer
   - Add optional notes

5. **View Your Events**
   - Go to "My Events" to see your registrations
   - Cancel registrations if needed

### For Administrators:

1. **Login as Admin**
   - Use admin credentials

2. **Manage Volunteers**
   - View all volunteers
   - Activate/deactivate volunteer accounts
   - View volunteer details

3. **Create Events**
   - Click "Create New Event"
   - Fill in event details
   - Set volunteer capacity

4. **Manage Events**
   - Edit event information
   - View registered volunteers
   - Update event status
   - Delete events if needed

5. **View Dashboard**
   - Monitor system statistics
   - View upcoming events
   - Track registrations

## 👥 User Roles

### Volunteer (Default)
- Register and manage personal profile
- Browse and search events
- Register for events
- View personal event history
- Cancel registrations

### Administrator
- All volunteer permissions
- Create, edit, and delete events
- View all volunteers
- Manage volunteer status
- View event registrations
- Access system statistics

## 🛠️ Technologies Used

### Backend:
- **Java 11** - Server-side programming
- **Servlet API 4.0** - HTTP request handling
- **JSP 2.3** - Dynamic page generation
- **JDBC** - Database connectivity
- **MySQL** - Database management
- **HikariCP** - Connection pooling
- **Maven** - Build automation and dependency management

### Frontend:
- **HTML5** - Structure and markup
- **CSS3** - Styling and layout
- **JavaScript (ES6)** - Client-side interactivity
- **JSTL** - JSP Standard Tag Library
- **Responsive Design** - Mobile-friendly interface

### Architecture:
- **MVC Pattern** - Separation of concerns
- **DAO Pattern** - Data access abstraction
- **Object-Oriented Programming** - Code organization
- **Servlet/JSP architecture** - Java EE web patterns

### Security:
- **BCrypt** - Password hashing (at.favre.lib)
- **Input Sanitization** - XSS prevention
- **Prepared Statements** - SQL injection prevention
- **Session Management** - Secure authentication
- **Filter Chain** - Request/response filtering

### Libraries & Frameworks:
- **HikariCP 5.0.1** - High-performance connection pooling
- **BCrypt 0.10.2** - Password hashing
- **Apache Commons Lang3** - Utility functions
- **Gson** - JSON processing
- **SLF4J** - Logging framework

## 🏗️ MVC Architecture

This application follows the MVC (Model-View-Controller) pattern with DAO layer:

### Models (`src/main/java/com/volunteermanagement/model/`)
- Plain Old Java Objects (POJOs)
- Define data structures and entities
- Represent database tables
- Include helper methods

### Views (`src/main/webapp/WEB-INF/views/`)
- JSP pages for user interface
- JSTL tags for dynamic content
- Present data to users
- Collect user input via forms

### Controllers (`src/main/java/com/volunteermanagement/controller/`)
- Servlet classes handling HTTP requests
- Process form submissions
- Coordinate between DAOs and views
- Implement business logic

### Data Access Layer (`src/main/java/com/volunteermanagement/dao/`)
- DAO (Data Access Object) classes
- Interact with database via JDBC
- Implement CRUD operations
- Execute SQL queries with prepared statements

### Flow:
```
User Request → Servlet (Controller) → DAO → Database
                      ↓
                  JSP (View)
                      ↓
              User Response
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License. See the LICENSE file for details.

## 📧 Contact

For questions or support, please contact:
- **Email**: info@volunteermanagement.com
- **GitHub**: [csharpe876](https://github.com/csharpe876)

## 🙏 Acknowledgments

- COMP2140 Software Engineering Course
- University of Technology, Jamaica
- Open source community

---

**Version**: 1.0.0  
**Last Updated**: November 29, 2025  
**Status**: Production Ready
