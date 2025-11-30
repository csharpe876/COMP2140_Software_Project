<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Register" scope="request"/>
<c:set var="containerClass" value="container-sm" scope="request"/>
<jsp:include page="../common/header.jsp"/>

<!-- Page Header -->
<div class="page-header">
    <h1 class="app-title">The Faculty of Science & Technology</h1>
    <h2 class="app-subtitle">Volunteer Management System</h2>
    <h3 class="page-title">📝 Create Account</h3>
</div>

<!-- Error/Success Messages -->
<c:if test="${not empty param.error}">
    <div class="alert alert-danger">
        ${param.error}
    </div>
</c:if>

<!-- Registration Form -->
<form id="registerForm" method="post" action="${pageContext.request.contextPath}/auth/register" novalidate>
    <div class="form-group">
        <label for="username" class="form-label">Username *</label>
        <input 
            type="text" 
            id="username" 
            name="username" 
            class="form-control" 
            placeholder="Choose a username"
            data-validate='{"required": true, "username": true, "minLength": 3, "maxLength": 20}'
            autocomplete="username"
            required
            autofocus>
        <small class="form-text">3-20 characters, alphanumeric and underscores only</small>
    </div>

    <div class="form-group">
        <label for="email" class="form-label">Email Address *</label>
        <input 
            type="email" 
            id="email" 
            name="email" 
            class="form-control" 
            placeholder="your.email@example.com"
            data-validate='{"required": true, "email": true}'
            autocomplete="email"
            required>
        <small class="form-text">We'll never share your email with anyone else</small>
    </div>

    <div class="form-group">
        <label for="password" class="form-label">Password *</label>
        <div class="input-group">
            <input 
                type="password" 
                id="password" 
                name="password" 
                class="form-control" 
                placeholder="Create a strong password"
                data-validate='{"required": true, "password": true, "minLength": 8}'
                autocomplete="new-password"
                required>
            <div class="input-group-append">
                <button type="button" class="btn-link input-group-text" id="togglePassword" title="Show/Hide Password">
                    👁️
                </button>
            </div>
        </div>
        <small class="form-text">Minimum 8 characters with uppercase, lowercase, and number</small>
        
        <!-- Password Strength Indicator -->
        <div class="password-strength mt-2">
            <div class="password-strength-bar"></div>
        </div>
        <small class="password-strength-text mt-1"></small>
    </div>

    <div class="form-group">
        <label for="confirmPassword" class="form-label">Confirm Password *</label>
        <input 
            type="password" 
            id="confirmPassword" 
            name="confirmPassword" 
            class="form-control" 
            placeholder="Re-enter your password"
            data-validate='{"required": true, "matches": "password"}'
            autocomplete="new-password"
            required>
    </div>

    <div class="form-group">
        <label for="fullName" class="form-label">Full Name *</label>
        <input 
            type="text" 
            id="fullName" 
            name="fullName" 
            class="form-control" 
            placeholder="Your full name"
            data-validate='{"required": true, "minLength": 2}'
            autocomplete="name"
            required>
    </div>

    <div class="form-group">
        <label for="phone" class="form-label">Phone Number (Optional)</label>
        <input 
            type="tel" 
            id="phone" 
            name="phone" 
            class="form-control" 
            placeholder="(876) 123-4567"
            data-validate='{"phone": true}'
            autocomplete="tel">
        <small class="form-text">Used for event notifications</small>
    </div>

    <button type="submit" class="btn btn-primary btn-block mt-4" id="registerBtn">
        <span id="registerBtnText">Create Account</span>
        <span id="registerBtnSpinner" class="spinner d-none"></span>
    </button>
</form>

<div class="text-center mt-3">
    <p>Already have an account? <a href="${pageContext.request.contextPath}/auth/login">Login here</a></p>
</div>

<!-- Page-specific JavaScript -->
<script>
    // Setup password toggle
    PasswordToggle.setup(
        document.getElementById('password'),
        document.getElementById('togglePassword')
    );

    // Setup password strength indicator
    PasswordStrength.setup(
        document.getElementById('password'),
        document.querySelector('.password-strength').parentElement
    );

    // Form validation on submit
    document.getElementById('registerForm').addEventListener('submit', function(e) {
        const isValid = Validator.validateForm(this);
        
        if (!isValid) {
            e.preventDefault();
            Toast.error('Please fix the errors in the form');
            return false;
        }

        // Show loading state
        const btn = document.getElementById('registerBtn');
        const btnText = document.getElementById('registerBtnText');
        const btnSpinner = document.getElementById('registerBtnSpinner');
        
        btn.disabled = true;
        btnText.classList.add('d-none');
        btnSpinner.classList.remove('d-none');
    });

    // Real-time validation
    const inputs = document.querySelectorAll('[data-validate]');
    inputs.forEach(input => {
        // Validate on blur
        input.addEventListener('blur', function() {
            const rules = JSON.parse(this.dataset.validate || '{}');
            const result = Validator.validateField(this, rules);
            Validator.showFeedback(this, result.isValid, result.message);
        });

        // Clear error on input
        input.addEventListener('input', function() {
            this.classList.remove('is-invalid');
            const feedback = this.parentElement.querySelector('.invalid-feedback');
            if (feedback) feedback.remove();
        });
    });

    // Optional: Check username availability (uncomment to enable)
    /*
    let usernameTimeout;
    document.getElementById('username').addEventListener('input', function() {
        clearTimeout(usernameTimeout);
        const username = this.value.trim();
        
        if (username.length < 3) return;

        usernameTimeout = setTimeout(() => {
            Ajax.get('${pageContext.request.contextPath}/api/check-username?username=' + encodeURIComponent(username))
                .then(response => {
                    if (response.exists) {
                        Validator.showFeedback(this, false, 'Username already taken');
                    } else {
                        Validator.showFeedback(this, true, 'Username available');
                    }
                })
                .catch(error => {
                    console.error('Error checking username:', error);
                });
        }, 500);
    });
    */

    // Confirm password matching
    document.getElementById('confirmPassword').addEventListener('input', function() {
        const password = document.getElementById('password').value;
        const confirmPassword = this.value;
        
        if (confirmPassword && password !== confirmPassword) {
            Validator.showFeedback(this, false, 'Passwords do not match');
        } else if (confirmPassword) {
            Validator.showFeedback(this, true, 'Passwords match');
        }
    });
</script>

<jsp:include page="../common/footer.jsp"/>
