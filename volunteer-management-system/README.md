Volunteer Management System
Complete Backend Implementation for FST Guild Committee
📁 Project Structure
volunteer-management-system/
├── src/
│   └── main/
│       ├── java/com/fstgc/vms/
│       │   ├── model/
│       │   │   ├── Volunteer.java
│       │   │   ├── Event.java
│       │   │   ├── Attendance.java
│       │   │   ├── Timesheet.java
│       │   │   ├── Announcement.java
│       │   │   ├── Award.java
│       │   │   ├── SystemAdmin.java
│       │   │   └── AwardCriteria.java
│       │   ├── repository/
│       │   │   ├── VolunteerRepository.java
│       │   │   ├── EventRepository.java
│       │   │   ├── AttendanceRepository.java
│       │   │   ├── TimesheetRepository.java
│       │   │   ├── AnnouncementRepository.java
│       │   │   └── AwardRepository.java
│       │   ├── service/
│       │   │   ├── VolunteerService.java
│       │   │   ├── EventService.java
│       │   │   ├── AttendanceService.java
│       │   │   ├── TimesheetService.java
│       │   │   ├── AnnouncementService.java
│       │   │   └── AwardService.java
│       │   ├── controller/
│       │   │   ├── VolunteerController.java
│       │   │   ├── EventController.java
│       │   │   ├── AttendanceController.java
│       │   │   ├── TimesheetController.java
│       │   │   ├── AnnouncementController.java
│       │   │   └── AwardController.java
│       │   └── util/
│       │       ├── DatabaseConnection.java
│       │       └── CorsFilter.java
│       └── webapp/
│           ├── WEB-INF/
│           │   └── web.xml
│           ├── index.html
│           ├── css/
│           │   └── styles.css
│           └── js/
│               └── main.js
├── database/
│   └── schema.sql
├── pom.xml
└── README.md
🚀 Quick Start Guide
Prerequisites
Java Development Kit (JDK) - Version 11 or higher
Download from: https://www.oracle.com/java/technologies/downloads/
Verify installation: java -version
Apache Tomcat - Version 9 or higher
Download from: https://tomcat.apache.org/download-90.cgi
Extract to a directory (e.g., C:\apache-tomcat-9.0.xx)
MySQL Server - Version 5.7 or higher
Download from: https://dev.mysql.com/downloads/mysql/
Install and remember your root password
Maven - For dependency management
Download from: https://maven.apache.org/download.cgi
Verify installation: mvn -version
IDE - VS Code, IntelliJ IDEA, or Eclipse
For VS Code, install "Java Extension Pack"
📦 Installation Steps
Step 1: Setup MySQL Database
Start MySQL Server
bash
   # On Windows
   net start MySQL80

   # On Mac/Linux
   sudo service mysql start
Login to MySQL
bash
   mysql -u root -p
Create Database and Run Schema
sql
   SOURCE /path/to/database/schema.sql;
Or manually copy the contents of schema.sql and execute in MySQL Workbench.

Verify Database Creation
sql
   SHOW DATABASES;
   USE volunteer_management_system;
   SHOW TABLES;
Step 2: Configure Database Connection
Open src/main/java/com/fstgc/vms/util/DatabaseConnection.java
Update the database credentials:
java
   private static final String URL = "jdbc:mysql://localhost:3306/volunteer_management_system?useSSL=false&serverTimezone=UTC";
   private static final String USERNAME = "root";
   private static final String PASSWORD = "YOUR_MYSQL_PASSWORD";
Step 3: Build the Project
Navigate to project directory
bash
   cd volunteer-management-system
Clean and build with Maven
bash
   mvn clean install
This will create a WAR file in the target/ directory:
volunteer-management-system.war
Step 4: Deploy to Tomcat
Option A: Manual Deployment

Copy the WAR file to Tomcat's webapps directory:
bash
   cp target/volunteer-management-system.war /path/to/tomcat/webapps/
Start Tomcat:
bash
   # On Windows
   C:\apache-tomcat-9.0.xx\bin\startup.bat

   # On Mac/Linux
   /path/to/tomcat/bin/startup.sh
Option B: VS Code Deployment

Install "Tomcat for Java" extension in VS Code
Right-click on the WAR file → Deploy to Tomcat
Step 5: Verify Deployment
Check Tomcat is running
Open browser: http://localhost:8080/
You should see the Tomcat homepage
Test the API
Open browser or Postman
Test endpoint: http://localhost:8080/volunteer-management-system/api/volunteers
You should get an empty JSON array: []
🔧 Configuration Files
pom.xml (Already provided)
Contains all required dependencies:

Servlet API
MySQL Connector
Gson (JSON parsing)
BCrypt (password hashing)
web.xml (Already provided)
Maps all controller servlets to their URLs
Configures CORS filter
Sets session timeout
Defines error pages
🧪 Testing the API
Using Postman or cURL
1. Create a Volunteer

bash
POST http://localhost:8080/volunteer-management-system/api/volunteers
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "1234567890",
  "dateOfBirth": "2000-01-15",
  "address": "123 Main St, Kingston"
}
2. Get All Volunteers

bash
GET http://localhost:8080/volunteer-management-system/api/volunteers
3. Create an Event

bash
POST http://localhost:8080/volunteer-management-system/api/events
Content-Type: application/json

{
  "title": "Beach Cleanup",
  "description": "Community beach cleanup event",
  "eventDate": "2025-12-10",
  "startTime": "09:00:00",
  "endTime": "13:00:00",
  "location": "Hellshire Beach",
  "capacity": 30,
  "eventType": "community_service",
  "status": "published"
}
4. Record Attendance

bash
POST http://localhost:8080/volunteer-management-system/api/attendance
Content-Type: application/json

{
  "volunteerId": 1,
  "eventId": 1,
  "checkInTime": "2025-12-10T09:00:00",
  "checkOutTime": "2025-12-10T13:00:00",
  "status": "present"
}
📊 API Endpoints Reference
Volunteers
GET /api/volunteers - Get all volunteers
GET /api/volunteers/{id} - Get volunteer by ID
GET /api/volunteers?q={search} - Search volunteers
POST /api/volunteers - Create volunteer
PUT /api/volunteers/{id} - Update volunteer
DELETE /api/volunteers/{id} - Deactivate volunteer
Events
GET /api/events - Get all events
GET /api/events/{id} - Get event by ID
GET /api/events/upcoming - Get upcoming events
GET /api/events?status={status} - Filter by status
POST /api/events - Create event
POST /api/events/register/{id} - Register for event
PUT /api/events/{id} - Update event
PUT /api/events/{id}/publish - Publish event
DELETE /api/events/{id} - Cancel event
Attendance
GET /api/attendance - Get all attendance
GET /api/attendance/{id} - Get attendance by ID
GET /api/attendance?volunteerId={id} - Get by volunteer
GET /api/attendance?eventId={id} - Get by event
POST /api/attendance - Record attendance
PUT /api/attendance/{id} - Update attendance
DELETE /api/attendance/{id} - Delete attendance
Timesheets
GET /api/timesheets - Get all timesheets
GET /api/timesheets/{id} - Get timesheet by ID
GET /api/timesheets/pending - Get pending approvals
GET /api/timesheets?volunteerId={id} - Get by volunteer
PUT /api/timesheets/{id}/approve - Approve timesheet
PUT /api/timesheets/{id}/reject - Reject timesheet
DELETE /api/timesheets/{id} - Delete timesheet
Announcements
GET /api/announcements - Get all announcements
GET /api/announcements/{id} - Get announcement by ID
GET /api/announcements/active - Get active only
GET /api/announcements?priority={priority} - Filter by priority
POST /api/announcements - Create announcement
PUT /api/announcements/{id} - Update announcement
DELETE /api/announcements/{id} - Delete announcement
Awards
GET /api/awards?volunteerId={id} - Get volunteer's awards
GET /api/awards?tier={tier} - Get by tier
GET /api/awards/featured - Get featured awards
POST /api/awards - Assign award
POST /api/awards/check/{volunteerId} - Check automatic awards
DELETE /api/awards/{id} - Delete award
🐛 Troubleshooting
Issue: Cannot connect to database
Solution:

Check MySQL is running
Verify credentials in DatabaseConnection.java
Check if database exists: SHOW DATABASES;
Ensure MySQL user has proper permissions
Issue: Port 8080 already in use
Solution:

Stop other services using port 8080
Or change Tomcat port in server.xml
Located in: tomcat/conf/server.xml
Find <Connector port="8080" and change to 8081
Issue: 404 Not Found
Solution:

Check WAR file is deployed: Look in tomcat/webapps/
Verify Tomcat started successfully: Check tomcat/logs/catalina.out
Ensure correct URL format: http://localhost:8080/volunteer-management-system/api/volunteers
Issue: CORS errors in browser
Solution:

The CorsFilter.java should handle this
For production, update allowed origins in CorsFilter
Issue: Maven build fails
Solution:

Check internet connection (Maven needs to download dependencies)
Clear Maven cache: mvn clean
Update Maven: mvn -version should be 3.6+
📝 Development Workflow
1. Making Code Changes
bash
# Edit Java files in VS Code
# After changes, rebuild:
mvn clean package

# Redeploy to Tomcat (stop → copy WAR → start)
2. Database Schema Changes
sql
# Make changes in MySQL
# Update schema.sql file for version control
3. Adding New Features
Create Model class in model/
Create Repository in repository/
Create Service in service/
Create Controller in controller/
Update web.xml with new servlet mapping
Rebuild and redeploy
🔐 Security Considerations
Password Hashing: Passwords are hashed using BCrypt
SQL Injection: Using PreparedStatements
CORS: Configured for development (restrict in production)
Session Management: 30-minute timeout configured
For Production:
Use HTTPS
Restrict CORS origins
Use environment variables for credentials
Enable authentication/authorization
Add rate limiting
📚 Additional Resources
Java Servlets Tutorial
MySQL Documentation
Maven Guide
Tomcat Documentation
👥 Team
Carl Sharpe - 05017725
Jaedon Beckford - 621696555
Ti-Carla Thompson - 620147445
Ashani Falconer - 620155746
Oneil Marshall - 620143423
Johnathan Jackson - 620169527
Course: COMP2140 - Introduction to Software Engineering
Instructor: Dr. R. Anderson
Studio Facilitator: Mr. E. Ferguson

📄 License
This project is created for educational purposes for the Faculty of Science and Technology Guild Committee at UWI Mona.

✅ Checklist for Setup
 JDK 11+ installed
 MySQL installed and running
 Maven installed
 Tomcat installed
 Database schema executed
 Database credentials updated in code
 Project built with Maven
 WAR file deployed to Tomcat
 Tomcat started successfully
 API endpoints tested and working
Happy Coding! 🎉

