<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    </div>
    
    <!-- Footer -->
    <footer class="text-center mt-4" style="color: white; padding: 20px;">
        <p>&copy; 2025 The Faculty of Science & Technology. All rights reserved.</p>
        <p style="font-size: 0.875rem;">Volunteer Management System</p>
    </footer>
    
    <!-- External JavaScript -->
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
    
    <!-- Initialize App -->
    <script>
        App.init('${pageContext.request.contextPath}');
    </script>
    
    <!-- Page-specific scripts -->
    <c:if test="${not empty pageScript}">
        <script>${pageScript}</script>
    </c:if>
</body>
</html>
