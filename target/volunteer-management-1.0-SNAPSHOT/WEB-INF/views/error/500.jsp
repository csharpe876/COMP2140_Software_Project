<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>500 Internal Server Error</title>
    <style>
        body { font-family: system-ui, Arial, sans-serif; margin: 3rem; }
        h1 { color: #b00020; }
        pre { background: #f6f8fa; padding: 1rem; overflow: auto; }
        a { color: #0366d6; }
    </style>
</head>
<body>
<h1>500 - Internal Server Error</h1>
<p>Something went wrong while processing your request.</p>
<p><a href="<%= request.getContextPath() %>/">Return to Home</a></p>
<%-- Optionally show stack trace in dev; keep minimal for production --%>
</body>
</html>