<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>404 Not Found</title>
    <style>
        body { font-family: system-ui, Arial, sans-serif; margin: 3rem; }
        h1 { color: #b00020; }
        a { color: #0366d6; }
    </style>
</head>
<body>
<h1>404 - Page Not Found</h1>
<p>The requested resource could not be found.</p>
<p><a href="<%= request.getContextPath() %>/">Return to Home</a></p>
</body>
</html>