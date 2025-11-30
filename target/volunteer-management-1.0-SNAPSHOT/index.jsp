<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Volunteer Management System</title>
    <style>
        body { font-family: system-ui, Arial, sans-serif; margin: 2rem; }
        h1 { margin-bottom: 0.5rem; }
        .links { margin-top: 1rem; }
        .links a { display: inline-block; margin-right: 1rem; }
    </style>
</head>
<body>
    <h1>Volunteer Management System</h1>
    <p>Welcome. Choose an action below:</p>
    <div class="links">
        <a href="auth/">Auth</a>
        <a href="events/">Events</a>
        <a href="volunteers/">Volunteers</a>
        <a href="register/">Register</a>
    </div>
    <p>If you reached this page while testing locally, ensure your server context path is correct. For Jetty run-war, use context path `/volunteer-management` or access the app at the root if deployed as ROOT.</p>
    <p><small>Build: 1.0-SNAPSHOT</small></p>
</body>
\</html>