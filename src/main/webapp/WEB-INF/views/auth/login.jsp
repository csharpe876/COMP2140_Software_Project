<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Login" scope="request"/>
<c:set var="containerClass" value="container-sm" scope="request"/>
<jsp:include page="../common/header.jsp"/>

<!-- Page Header -->
<div class="page-header">
    <h1 class="app-title">The Faculty of Science & Technology</h1>
    <h2 class="app-subtitle">Volunteer Management System</h2>
    <h3 class="page-title">🔐 Login</h3>
</div>

<!-- Error/Success Messages -->
<c:if test="${not empty param.error}">
    <div class="alert alert-danger">
        Invalid username or password. Please try again.
    </div>
</c:if>

<c:if test="${not empty param.registered}">
    <div class="alert alert-success">
        Registration successful! Please login with your credentials.
    </div>
</c:if>

<c:if test="${not empty param.logout}">
    <div class="alert alert-info">
        You have been logged out successfully.
    </div>
</c:if>

<!-- Login Form -->
<form id="loginForm" method="post" action="${pageContext.request.contextPath}/auth/login">
    <div class="form-group">
        <label for="username" class="form-label">Username</label>
        <input 
            type="text" 
            id="username" 
            name="username" 
            class="form-control" 
            placeholder="Enter your username"
            data-validate='{"required": true, "minLength": 3}'
            autocomplete="username"
            required
            autofocus>
    </div>

    <div class="form-group">
        <label for="password" class="form-label">Password</label>
        <div class="input-group">
            <input 
                type="password" 
                id="password" 
                name="password" 
                class="form-control" 
                placeholder="Enter your password"
                data-validate='{"required": true, "minLength": 8}'
                autocomplete="current-password"
                required>
            <div class="input-group-append">
                <button type="button" class="btn-link input-group-text" id="togglePassword" title="Show/Hide Password">
                    👁️
                </button>
            </div>
        </div>
        <small class="form-text">Minimum 8 characters</small>
    </div>

    <button type="submit" class="btn btn-primary btn-block" id="loginBtn">
        <span id="loginBtnText">Login</span>
        <span id="loginBtnSpinner" class="spinner d-none"></span>
    </button>
</form>

<div class="text-center mt-3">
    <p>Don't have an account? <a href="${pageContext.request.contextPath}/auth/register">Create new account</a></p>
</div>

<!-- Page-specific JavaScript -->
<script>
    // Setup password toggle
    PasswordToggle.setup(
        document.getElementById('password'),
        document.getElementById('togglePassword')
    );

    // Form validation on submit
    document.getElementById('loginForm').addEventListener('submit', function(e) {
        const isValid = Validator.validateForm(this);
        
        if (!isValid) {
            e.preventDefault();
            Toast.error('Please fix the errors in the form');
            return false;
        }

        // Show loading state
        const btn = document.getElementById('loginBtn');
        const btnText = document.getElementById('loginBtnText');
        const btnSpinner = document.getElementById('loginBtnSpinner');
        
        btn.disabled = true;
        btnText.classList.add('d-none');
        btnSpinner.classList.remove('d-none');
    });

    // Real-time validation
    const inputs = document.querySelectorAll('[data-validate]');
    inputs.forEach(input => {
        input.addEventListener('blur', function() {
            const rules = JSON.parse(this.dataset.validate || '{}');
            const result = Validator.validateField(this, rules);
            Validator.showFeedback(this, result.isValid, result.message);
        });
    });

    // Optional: AJAX login (uncomment to enable)
    /*
    document.getElementById('loginForm').addEventListener('submit', function(e) {
        e.preventDefault();
        
        if (!Validator.validateForm(this)) {
            Toast.error('Please fix the errors in the form');
            return;
        }

        Loading.show('Logging in...');

        Ajax.submitForm(this)
            .then(response => {
                Loading.hide();
                if (response.success) {
                    Toast.success('Login successful! Redirecting...');
                    setTimeout(() => {
                        window.location.href = response.redirectUrl || '${pageContext.request.contextPath}/events';
                    }, 1000);
                } else {
                    Toast.error(response.message || 'Login failed. Please try again.');
                }
            })
            .catch(error => {
                Loading.hide();
                Toast.error('An error occurred. Please try again.');
                console.error('Login error:', error);
            });
    });
    */
</script>

<jsp:include page="../common/footer.jsp"/>
