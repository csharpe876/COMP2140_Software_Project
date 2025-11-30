<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>${pageTitle} - The Faculty of Science & Technology Volunteer Management System</title>
    
    <!-- External CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    
    <!-- Favicon -->
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
</head>
<body>
    <!-- Navigation Header -->
    <c:if test="${not empty sessionScope.userId}">
        <nav class="navbar">
            <div class="container">
                <div class="navbar-brand">
                    <a href="${pageContext.request.contextPath}/">FST VMS</a>
                </div>
                <div class="navbar-menu">
                    <a href="${pageContext.request.contextPath}/events" class="nav-link">Events</a>
                    <a href="${pageContext.request.contextPath}/volunteers" class="nav-link">Volunteers</a>
                    <c:if test="${sessionScope.role == 'ADMIN'}">
                        <a href="${pageContext.request.contextPath}/admin" class="nav-link">Admin</a>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/auth/logout" class="nav-link">Logout (${sessionScope.username})</a>
                </div>
            </div>
        </nav>
    </c:if>
    
    <!-- Main Content Container -->
    <div class="container ${containerClass}">
