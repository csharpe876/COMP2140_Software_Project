# Volunteer Management System - Installation Guide (Java)

## Quick Start Guide

Follow these steps to get your Java-based Volunteer Management System up and running.

---

## Prerequisites

Before you begin, ensure you have:
- **JDK 11 or higher** installed
- **Apache Maven 3.6+** installed
- **Apache Tomcat 9.0+** installed
- **MySQL 5.7+** installed (or XAMPP for MySQL)
- **Windows PowerShell** or Command Prompt
- **Git** (optional, for cloning)

---

## Step-by-Step Installation

### 1. Install Java Development Kit (JDK)

1. **Download JDK 11 or higher:**
   - Oracle: https://www.oracle.com/java/technologies/downloads/
   - Or OpenJDK: https://adoptium.net/

2. **Install JDK:**
   - Run the installer
   - Use default installation path or note custom path

3. **Set JAVA_HOME environment variable:**
   - Right-click "This PC" → Properties → Advanced system settings
   - Click "Environment Variables"
   - Under "System Variables", click "New"
   - Variable name: `JAVA_HOME`
   - Variable value: Path to JDK (e.g., `C:\Program Files\Java\jdk-11.0.12`)
   - Click OK

4. **Add Java to PATH:**
   - In Environment Variables, find "Path" under System Variables
   - Click "Edit"
   - Add: `%JAVA_HOME%\bin`
   - Click OK

5. **Verify installation:**
   ```powershell
   java -version
   ```
   Should show Java version 11 or higher

### 2. Install Apache Maven

1. **Download Maven:**
   - Go to: https://maven.apache.org/download.cgi
   - Download the binary zip archive (e.g., apache-maven-3.9.5-bin.zip)

2. **Extract Maven:**
   - Extract to: `C:\Program Files\Apache\maven`

3. **Set M2_HOME environment variable:**
   - Right-click "This PC" → Properties → Advanced system settings
   - Click "Environment Variables"
   - Under "System Variables", click "New"
   - Variable name: `M2_HOME`
   - Variable value: `C:\Program Files\Apache\maven`
   - Click OK

4. **Add Maven to PATH:**
   - In Environment Variables, find "Path"
   - Click "Edit"
   - Add: `%M2_HOME%\bin`
   - Click OK

5. **Verify installation:**
   ```powershell
   mvn -version
   ```

### 3. Install Apache Tomcat

1. **Download Tomcat 9:**
   - Go to: https://tomcat.apache.org/download-90.cgi
   - Download the Windows zip file (64-bit Windows zip)

2. **Extract Tomcat:**
   - Extract to: `C:\Program Files\Apache\Tomcat 9.0`

3. **Set CATALINA_HOME (optional but recommended):**
   - Environment Variables → System Variables → New
   - Variable name: `CATALINA_HOME`
   - Variable value: `C:\Program Files\Apache\Tomcat 9.0`

4. **Configure Tomcat users (optional):**
   - Edit: `C:\Program Files\Apache\Tomcat 9.0\conf\tomcat-users.xml`
   - Add inside `<tomcat-users>` tag:
   ```xml
   <role rolename="manager-gui"/>
   <user username="admin" password="admin" roles="manager-gui"/>
   ```

### 4. Install MySQL

**Option A: Install MySQL Server**
1. Download MySQL Installer from: https://dev.mysql.com/downloads/installer/
2. Run installer and select "Developer Default"
3. Set root password during installation
4. Complete installation

**Option B: Use XAMPP for MySQL only**
1. Download XAMPP from: https://www.apachefriends.org/
2. Install to: `C:\xampp`
3. Start only MySQL from XAMPP Control Panel

### 5. Get the Project Files

**Option A: Clone with Git**
```powershell
cd C:\
git clone https://github.com/csharpe876/COMP2140_Software_Project.git
```

**Option B: Download ZIP**
1. Download the project ZIP file
2. Extract to: `C:\COMP2140_Software_Project`

### 6. Create the Database

**Method 1: Using phpMyAdmin (if using XAMPP)**

1. Start MySQL from XAMPP Control Panel
2. Open browser: http://localhost/phpmyadmin
3. Click **"Import"** tab
4. Click **"Choose File"**
5. Navigate to: `C:\COMP2140_Software_Project\database\schema.sql`
6. Click **"Go"**
7. Wait for success message

**Method 2: Using MySQL Command Line**

1. Open PowerShell or Command Prompt
2. Navigate to project directory:
   ```powershell
   cd C:\COMP2140_Software_Project
   ```
3. Run the import command:
   ```powershell
   mysql -u root -p < database\schema.sql
   ```
4. Enter your MySQL root password when prompted

### 7. Configure Database Connection

1. **Open the configuration file:**
   - Navigate to: `C:\COMP2140_Software_Project\src\main\resources\`
   - Open: `application.properties`

2. **Update database credentials:**
   ```properties
   # Database Configuration
   db.url=jdbc:mysql://localhost:3306/volunteer_management?useSSL=false&serverTimezone=UTC
   db.username=root
   db.password=YOUR_MYSQL_PASSWORD
   
   # Connection Pool Settings
   db.pool.maximumPoolSize=10
   db.pool.minimumIdle=5
   db.pool.connectionTimeout=30000
   db.pool.idleTimeout=600000
   db.pool.maxLifetime=1800000
   ```

3. **Save the file**

### 8. Build the Application

1. **Open PowerShell or Command Prompt**

2. **Navigate to project directory:**
   ```powershell
   cd C:\COMP2140_Software_Project
   ```

3. **Clean and build:**
   ```powershell
   mvn clean package
   ```

4. **Wait for build to complete:**
   - You should see: `BUILD SUCCESS`
   - The WAR file will be created at: `target\volunteer-management.war`

### 9. Deploy to Tomcat

**Method A: Manual WAR Deployment (Recommended)**

1. **Copy the WAR file:**
   ```powershell
   Copy-Item target\volunteer-management.war "C:\Program Files\Apache\Tomcat 9.0\webapps\"
   ```

2. **Start Tomcat:**
   ```powershell
   cd "C:\Program Files\Apache\Tomcat 9.0\bin"
   .\startup.bat
   ```

3. **Wait for deployment:**
   - Tomcat will automatically extract the WAR file
   - Watch the console for "Deployment of web application archive"

**Method B: Use Maven Tomcat Plugin**

1. **Run directly with Maven:**
   ```powershell
   mvn tomcat7:run
   ```

2. **Application will start on port 8080**

### 10. Access the Application

1. **Open your web browser**

2. **Navigate to:**
   ```
   http://localhost:8080/volunteer-management
   ```

3. **You should see the login page**

### 11. Login to the System

**Admin Account:**
- Username: `admin`
- Password: `admin123`

**Test Volunteer Account:**
- Username: `johndoe`
- Password: `volunteer123`

---

## Troubleshooting

### Problem: "JAVA_HOME not set" or "java not recognized"

**Solution:**
1. Verify JDK is installed:
   ```powershell
   "C:\Program Files\Java\jdk-11.0.12\bin\java.exe" -version
   ```
2. Set JAVA_HOME environment variable (see Step 1)
3. Add `%JAVA_HOME%\bin` to PATH
4. Close and reopen PowerShell
5. Test: `java -version`

### Problem: "mvn not recognized"

**Solution:**
1. Verify Maven is extracted correctly
2. Set M2_HOME environment variable (see Step 2)
3. Add `%M2_HOME%\bin` to PATH
4. Close and reopen PowerShell
5. Test: `mvn -version`

### Problem: "Port 8080 already in use"

**Solution:**
1. Find what's using port 8080:
   ```powershell
   netstat -ano | findstr :8080
   ```
2. Stop the process or change Tomcat port
3. Edit: `C:\Program Files\Apache\Tomcat 9.0\conf\server.xml`
4. Find: `<Connector port="8080"`
5. Change to: `<Connector port="8081"`
6. Restart Tomcat
7. Access: http://localhost:8081/volunteer-management

### Problem: "Database connection error"

**Solution:**
1. Verify MySQL is running:
   ```powershell
   Get-Service | Where-Object {$_.Name -like '*mysql*'}
   ```
2. Check database exists:
   - Open phpMyAdmin or MySQL Workbench
   - Verify "volunteer_management" database exists
3. Verify connection settings in `application.properties`:
   ```properties
   db.url=jdbc:mysql://localhost:3306/volunteer_management?useSSL=false&serverTimezone=UTC
   db.username=root
   db.password=YOUR_PASSWORD
   ```
4. Test connection manually:
   ```powershell
   mysql -u root -p -e "USE volunteer_management; SHOW TABLES;"
   ```

### Problem: "BUILD FAILURE" when running mvn package

**Solution:**
1. Check Java version:
   ```powershell
   java -version
   ```
   Must be 11 or higher
2. Clean Maven cache:
   ```powershell
   mvn clean
   ```
3. Delete target folder and rebuild:
   ```powershell
   Remove-Item target -Recurse -Force
   mvn package
   ```
4. Check for compilation errors in output

### Problem: "ClassNotFoundException" or "NoClassDefFoundError"

**Solution:**
1. Verify all dependencies in pom.xml
2. Clean and rebuild:
   ```powershell
   mvn clean install
   ```
3. Check target/volunteer-management/WEB-INF/lib/ has all JAR files
4. If using IDE, do Maven → Reload Project

### Problem: "404 - Application not found"

**Solution:**
1. Verify WAR is deployed:
   - Check: `C:\Program Files\Apache\Tomcat 9.0\webapps\volunteer-management\`
   - Folder should exist and contain WEB-INF
2. Check Tomcat logs:
   ```powershell
   Get-Content "C:\Program Files\Apache\Tomcat 9.0\logs\catalina.*.log" -Tail 50
   ```
3. Verify URL is correct:
   - Should be: http://localhost:8080/volunteer-management
   - NOT: http://localhost:8080/volunteer-management.war
4. Restart Tomcat:
   ```powershell
   cd "C:\Program Files\Apache\Tomcat 9.0\bin"
   .\shutdown.bat
   .\startup.bat
   ```

### Problem: "Cannot login" or "Login credentials incorrect"

**Solution:**
1. Verify database was imported:
   ```powershell
   mysql -u root -p -e "USE volunteer_management; SELECT username FROM users;"
   ```
2. Should see: admin, johndoe, janesmit, etc.
3. Try default credentials:
   - Username: `admin`
   - Password: `admin123`
4. Check BCrypt is working (password hashing)

### Problem: "500 Internal Server Error"

**Solution:**
1. Check Tomcat logs:
   ```powershell
   Get-Content "C:\Program Files\Apache\Tomcat 9.0\logs\localhost.*.log" -Tail 100
   ```
2. Look for stack traces showing the actual error
3. Common causes:
   - Database connection failure
   - Missing servlet mapping in web.xml
   - Class instantiation errors
4. Enable debug logging in application

---

## Testing Your Installation

### Quick Test Checklist:

- [ ] Java version 11+ verified
- [ ] Maven installed and working
- [ ] Tomcat running on port 8080
- [ ] MySQL running with volunteer_management database
- [ ] Can access: http://localhost:8080/volunteer-management
- [ ] Can see the login page
- [ ] Can login with admin credentials
- [ ] Dashboard loads with statistics
- [ ] Can view events list
- [ ] Can view volunteers list (admin)
- [ ] Can logout successfully

### Test User Accounts:

All test accounts use password: `volunteer123`

1. johndoe - Active volunteer
2. janesmit - Active volunteer
3. mikejohn - Active volunteer
4. sarahwil - Active volunteer
5. davisbro - Inactive volunteer

### Test as Regular Volunteer:

1. Login as: `johndoe` / `volunteer123`
2. View Dashboard
3. Click "Events" → Browse available events
4. Click "My Profile" → View/edit volunteer info
5. Click "My Events" → See registered events
6. Click an event → Register for it

### Test as Administrator:

1. Login as: `admin` / `admin123`
2. View Dashboard with all statistics
3. Click "Admin" → "Manage Volunteers"
4. View all volunteers
5. Toggle volunteer status (active/inactive)
6. Click "Admin" → "Create Event"
7. Fill form and create a test event
8. View the event and see registered volunteers

### Verify Build:

```powershell
# Check WAR file was created
Test-Path target\volunteer-management.war

# Check WAR contents
cd target\volunteer-management
Get-ChildItem -Recurse

# Should see:
# - WEB-INF/classes/com/volunteermanagement/
# - WEB-INF/lib/ (with JAR files)
# - WEB-INF/web.xml
# - css/, js/, images/
```

---

## Default Configuration

### Database Configuration
- **Host**: localhost
- **Port**: 3306
- **Database**: volunteer_management
- **Username**: root
- **Password**: (set during MySQL installation)

### Application Configuration
- **Base URL**: http://localhost:8080/volunteer-management
- **Application Server**: Apache Tomcat 9.0
- **Servlet API**: 4.0
- **JSP Version**: 2.3
- **Connection Pool**: HikariCP (Max 10 connections)
- **Session Timeout**: 30 minutes
- **Password Hash**: BCrypt (Cost factor 10)

### Maven Configuration
- **Group ID**: com.volunteermanagement
- **Artifact ID**: volunteer-management
- **Version**: 1.0-SNAPSHOT
- **Packaging**: WAR

---

## Development Mode

### Running in Development:

1. **Use Maven Tomcat Plugin:**
   ```powershell
   mvn tomcat7:run
   ```

2. **Or use your IDE:**
   - IntelliJ IDEA: Configure Tomcat server, Run/Debug
   - Eclipse: Add to Server, Run on Server
   - VS Code: Install Tomcat extension, deploy

3. **Hot Reload (IntelliJ/Eclipse):**
   - Enable "Update classes and resources" on frame deactivation
   - Changes to JSP are reflected immediately
   - Java class changes require redeploy

### Useful Maven Commands:

```powershell
# Compile without packaging
mvn compile

# Run tests
mvn test

# Package as WAR
mvn package

# Clean build artifacts
mvn clean

# Install to local repository
mvn install

# Skip tests during build
mvn package -DskipTests

# View dependency tree
mvn dependency:tree

# Generate project documentation
mvn site
```

---

## Security Notes

### For Production Use:

1. **Change Default Passwords:**
   - Change admin password immediately:
     ```sql
     UPDATE users SET password = '$2a$10$NEW_BCRYPT_HASH' WHERE username = 'admin';
     ```
   - Remove or change test volunteer accounts

2. **Update Configuration:**
   - Set strong database password in `application.properties`
   - Change connection pool settings for production load
   - Enable SSL/TLS for database connection:
     ```properties
     db.url=jdbc:mysql://localhost:3306/volunteer_management?useSSL=true&requireSSL=true
     ```

3. **Secure Tomcat:**
   - Remove default webapps (ROOT, examples, docs, manager)
   - Change default Tomcat admin password
   - Enable HTTPS in Tomcat (server.xml)
   - Restrict manager application access
   - Set secure session cookies

4. **Application Security:**
   - Enable HTTPS redirect in web.xml
   - Set secure and httpOnly flags on cookies
   - Implement CSRF protection
   - Add Content Security Policy headers
   - Keep dependencies updated: `mvn versions:display-dependency-updates`

5. **Database Security:**
   - Create dedicated database user (not root)
   - Grant only necessary permissions
   - Enable MySQL SSL
   - Regular backups
   - Monitor slow query log

---

## Additional Resources

### Official Documentation:
- **Java Documentation**: https://docs.oracle.com/en/java/
- **Servlet API**: https://javaee.github.io/servlet-spec/
- **JSP Documentation**: https://javaee.github.io/javaee-spec/javadocs/
- **Maven Guide**: https://maven.apache.org/guides/
- **Tomcat Documentation**: https://tomcat.apache.org/tomcat-9.0-doc/
- **MySQL Documentation**: https://dev.mysql.com/doc/
- **HikariCP**: https://github.com/brettwooldridge/HikariCP

### Project Documentation:
- **README.md** - Project overview and features
- **JAVA_README.md** - Detailed Java implementation guide
- **Database Schema** - database/schema.sql

### Getting Help:
- Review troubleshooting section above
- Check Tomcat logs for error details
- Review project README files
- Contact: info@volunteermanagement.com

### Learning Resources:
- Java Servlets Tutorial: https://www.oracle.com/java/technologies/servlet-technology.html
- JSP Tutorial: https://www.tutorialspoint.com/jsp/
- Maven Tutorial: https://maven.apache.org/guides/getting-started/
- JDBC Tutorial: https://docs.oracle.com/javase/tutorial/jdbc/

---

## Uninstallation

To completely remove the system:

1. **Stop Tomcat:**
   ```powershell
   cd "C:\Program Files\Apache\Tomcat 9.0\bin"
   .\shutdown.bat
   ```

2. **Remove deployed application:**
   ```powershell
   Remove-Item "C:\Program Files\Apache\Tomcat 9.0\webapps\volunteer-management*" -Recurse -Force
   ```

3. **Delete project folder:**
   ```powershell
   Remove-Item C:\COMP2140_Software_Project -Recurse -Force
   ```

4. **Drop database:**
   ```powershell
   mysql -u root -p -e "DROP DATABASE volunteer_management;"
   ```

5. **Optional - Uninstall software:**
   - Uninstall JDK from Control Panel
   - Delete Maven folder
   - Delete Tomcat folder
   - Uninstall MySQL or XAMPP

---

**Installation Complete!**

You now have a fully functional Volunteer Management System ready to use.

For more information, see the main README.md file.
