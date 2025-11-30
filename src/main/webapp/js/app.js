/**
 * The Faculty of Science & Technology Volunteer Management System
 * JavaScript Application Library
 * Provides form validation, AJAX helpers, notifications, and UI utilities
 */

// ==================== Utility Functions ====================
const App = {
    // Configuration
    config: {
        contextPath: '', // Will be set dynamically
        toastDuration: 5000,
        ajaxTimeout: 30000
    },

    // Initialize the application
    init: function(contextPath) {
        this.config.contextPath = contextPath || '';
        this.setupEventListeners();
        console.log('App initialized with context path:', this.config.contextPath);
    },

    // Setup global event listeners
    setupEventListeners: function() {
        // Close modals when clicking outside
        document.addEventListener('click', function(e) {
            if (e.target.classList.contains('modal')) {
                App.closeModal(e.target);
            }
        });

        // Handle Escape key to close modals
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape') {
                const openModals = document.querySelectorAll('.modal.show');
                openModals.forEach(modal => App.closeModal(modal));
            }
        });
    }
};

// ==================== Form Validation ====================
const Validator = {
    // Validate email format
    isValidEmail: function(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    },

    // Validate username (alphanumeric, underscore, 3-20 chars)
    isValidUsername: function(username) {
        const re = /^[a-zA-Z0-9_]{3,20}$/;
        return re.test(username);
    },

    // Validate phone number (flexible format)
    isValidPhone: function(phone) {
        const re = /^[\d\s\-\+\(\)]{10,}$/;
        return re.test(phone);
    },

    // Check password strength
    getPasswordStrength: function(password) {
        if (!password) return { strength: 'none', score: 0 };
        
        let score = 0;
        
        // Length check
        if (password.length >= 8) score++;
        if (password.length >= 12) score++;
        
        // Character variety
        if (/[a-z]/.test(password)) score++;
        if (/[A-Z]/.test(password)) score++;
        if (/\d/.test(password)) score++;
        if (/[^a-zA-Z0-9]/.test(password)) score++;
        
        if (score <= 2) return { strength: 'weak', score: score };
        if (score <= 4) return { strength: 'medium', score: score };
        return { strength: 'strong', score: score };
    },

    // Validate password meets requirements
    isValidPassword: function(password) {
        if (password.length < 8) return false;
        if (!/[a-z]/.test(password)) return false;
        if (!/[A-Z]/.test(password)) return false;
        if (!/\d/.test(password)) return false;
        return true;
    },

    // Validate form field
    validateField: function(input, rules) {
        const value = input.value.trim();
        let isValid = true;
        let message = '';

        // Required check
        if (rules.required && !value) {
            isValid = false;
            message = 'This field is required';
        }

        // Email validation
        if (isValid && rules.email && value) {
            isValid = this.isValidEmail(value);
            message = isValid ? '' : 'Please enter a valid email address';
        }

        // Username validation
        if (isValid && rules.username && value) {
            isValid = this.isValidUsername(value);
            message = isValid ? '' : 'Username must be 3-20 alphanumeric characters or underscores';
        }

        // Password validation
        if (isValid && rules.password && value) {
            isValid = this.isValidPassword(value);
            message = isValid ? '' : 'Password must be at least 8 characters with uppercase, lowercase, and number';
        }

        // Phone validation
        if (isValid && rules.phone && value) {
            isValid = this.isValidPhone(value);
            message = isValid ? '' : 'Please enter a valid phone number';
        }

        // Min length
        if (isValid && rules.minLength && value.length < rules.minLength) {
            isValid = false;
            message = `Must be at least ${rules.minLength} characters`;
        }

        // Max length
        if (isValid && rules.maxLength && value.length > rules.maxLength) {
            isValid = false;
            message = `Must not exceed ${rules.maxLength} characters`;
        }

        // Match another field
        if (isValid && rules.matches) {
            const otherField = document.getElementById(rules.matches);
            if (otherField && value !== otherField.value) {
                isValid = false;
                message = 'Fields do not match';
            }
        }

        return { isValid, message };
    },

    // Show validation feedback
    showFeedback: function(input, isValid, message) {
        input.classList.remove('is-valid', 'is-invalid');
        
        // Remove existing feedback
        const existingFeedback = input.parentElement.querySelector('.invalid-feedback, .valid-feedback');
        if (existingFeedback) {
            existingFeedback.remove();
        }

        if (message) {
            input.classList.add(isValid ? 'is-valid' : 'is-invalid');
            
            const feedback = document.createElement('div');
            feedback.className = isValid ? 'valid-feedback' : 'invalid-feedback';
            feedback.textContent = message;
            input.parentElement.appendChild(feedback);
        }
    },

    // Validate entire form
    validateForm: function(form) {
        let isValid = true;
        const inputs = form.querySelectorAll('[data-validate]');
        
        inputs.forEach(input => {
            const rules = JSON.parse(input.dataset.validate || '{}');
            const result = this.validateField(input, rules);
            
            this.showFeedback(input, result.isValid, result.message);
            
            if (!result.isValid) {
                isValid = false;
            }
        });

        return isValid;
    }
};

// ==================== Password Strength Indicator ====================
const PasswordStrength = {
    // Setup password strength indicator for an input
    setup: function(passwordInput, strengthContainer) {
        if (!passwordInput || !strengthContainer) return;

        passwordInput.addEventListener('input', function() {
            const result = Validator.getPasswordStrength(this.value);
            PasswordStrength.updateIndicator(strengthContainer, result);
        });
    },

    // Update the strength indicator
    updateIndicator: function(container, result) {
        const bar = container.querySelector('.password-strength-bar');
        if (!bar) return;

        bar.className = 'password-strength-bar ' + result.strength;
        
        const text = container.querySelector('.password-strength-text');
        if (text) {
            const labels = {
                'none': '',
                'weak': 'Weak password',
                'medium': 'Medium password',
                'strong': 'Strong password'
            };
            text.textContent = labels[result.strength] || '';
            text.className = 'password-strength-text text-' + 
                (result.strength === 'weak' ? 'danger' : 
                 result.strength === 'medium' ? 'warning' : 'success');
        }
    }
};

// ==================== Password Visibility Toggle ====================
const PasswordToggle = {
    // Setup password visibility toggle
    setup: function(passwordInput, toggleButton) {
        if (!passwordInput || !toggleButton) return;

        toggleButton.addEventListener('click', function(e) {
            e.preventDefault();
            const type = passwordInput.getAttribute('type');
            passwordInput.setAttribute('type', type === 'password' ? 'text' : 'password');
            
            const icon = this.querySelector('i') || this;
            icon.textContent = type === 'password' ? '👁️' : '👁️‍🗨️';
        });
    }
};

// ==================== Toast Notifications ====================
const Toast = {
    container: null,

    // Initialize toast container
    init: function() {
        if (!this.container) {
            this.container = document.createElement('div');
            this.container.className = 'toast-container';
            document.body.appendChild(this.container);
        }
    },

    // Show toast notification
    show: function(message, type = 'info', duration = App.config.toastDuration) {
        this.init();

        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        
        const icons = {
            success: '✓',
            error: '✗',
            warning: '⚠',
            info: 'ℹ'
        };

        toast.innerHTML = `
            <span class="toast-icon">${icons[type] || icons.info}</span>
            <div class="toast-content">${message}</div>
            <button class="toast-close" onclick="this.parentElement.remove()">×</button>
        `;

        this.container.appendChild(toast);

        // Auto-remove after duration
        setTimeout(() => {
            toast.style.animation = 'slideOutRight 0.3s ease-out';
            setTimeout(() => toast.remove(), 300);
        }, duration);
    },

    success: function(message, duration) {
        this.show(message, 'success', duration);
    },

    error: function(message, duration) {
        this.show(message, 'error', duration);
    },

    warning: function(message, duration) {
        this.show(message, 'warning', duration);
    },

    info: function(message, duration) {
        this.show(message, 'info', duration);
    }
};

// ==================== AJAX Utilities ====================
const Ajax = {
    // Make AJAX request
    request: function(url, options = {}) {
        const defaults = {
            method: 'GET',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-Requested-With': 'XMLHttpRequest'
            },
            timeout: App.config.ajaxTimeout
        };

        const config = { ...defaults, ...options };

        return new Promise((resolve, reject) => {
            const xhr = new XMLHttpRequest();
            xhr.open(config.method, url, true);

            // Set headers
            for (const [key, value] of Object.entries(config.headers)) {
                xhr.setRequestHeader(key, value);
            }

            // Setup timeout
            xhr.timeout = config.timeout;
            xhr.ontimeout = () => reject(new Error('Request timeout'));

            // Handle response
            xhr.onload = function() {
                if (xhr.status >= 200 && xhr.status < 300) {
                    try {
                        const response = xhr.responseText ? JSON.parse(xhr.responseText) : {};
                        resolve(response);
                    } catch (e) {
                        resolve({ success: true, data: xhr.responseText });
                    }
                } else {
                    reject(new Error(`Request failed with status ${xhr.status}`));
                }
            };

            xhr.onerror = () => reject(new Error('Network error'));

            // Send request
            xhr.send(config.body || null);
        });
    },

    // GET request
    get: function(url) {
        return this.request(url, { method: 'GET' });
    },

    // POST request
    post: function(url, data) {
        const body = new URLSearchParams(data).toString();
        return this.request(url, { method: 'POST', body });
    },

    // Submit form via AJAX
    submitForm: function(form, onSuccess, onError) {
        const formData = new FormData(form);
        const data = {};
        formData.forEach((value, key) => data[key] = value);

        return this.post(form.action, data)
            .then(response => {
                if (onSuccess) onSuccess(response);
                return response;
            })
            .catch(error => {
                if (onError) onError(error);
                throw error;
            });
    }
};

// ==================== Modal Utilities ====================
const Modal = {
    // Open modal
    open: function(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.add('show');
            document.body.style.overflow = 'hidden';
        }
    },

    // Close modal
    close: function(modalId) {
        const modal = typeof modalId === 'string' ? 
            document.getElementById(modalId) : modalId;
        
        if (modal) {
            modal.classList.remove('show');
            document.body.style.overflow = '';
        }
    },

    // Create dynamic modal
    create: function(title, content, buttons = []) {
        const modalId = 'modal-' + Date.now();
        
        const modal = document.createElement('div');
        modal.id = modalId;
        modal.className = 'modal';
        
        let buttonsHtml = '';
        buttons.forEach(btn => {
            buttonsHtml += `<button class="btn btn-${btn.type || 'secondary'}" 
                onclick="${btn.onclick}">${btn.text}</button>`;
        });

        modal.innerHTML = `
            <div class="modal-content">
                <div class="modal-header">
                    <h3 class="modal-title">${title}</h3>
                    <button class="modal-close" onclick="Modal.close('${modalId}')">&times;</button>
                </div>
                <div class="modal-body">${content}</div>
                ${buttonsHtml ? `<div class="modal-footer">${buttonsHtml}</div>` : ''}
            </div>
        `;

        document.body.appendChild(modal);
        this.open(modalId);

        return modalId;
    }
};

// Attach to App object
App.closeModal = Modal.close;

// ==================== Loading Spinner ====================
const Loading = {
    overlay: null,

    // Show loading overlay
    show: function(message = 'Loading...') {
        if (!this.overlay) {
            this.overlay = document.createElement('div');
            this.overlay.className = 'loading-overlay';
            this.overlay.innerHTML = `
                <div style="text-align: center; color: white;">
                    <div class="spinner spinner-lg"></div>
                    <p style="margin-top: 1rem;">${message}</p>
                </div>
            `;
            document.body.appendChild(this.overlay);
        }
        this.overlay.style.display = 'flex';
    },

    // Hide loading overlay
    hide: function() {
        if (this.overlay) {
            this.overlay.style.display = 'none';
        }
    }
};

// ==================== Search & Filter ====================
const Search = {
    // Filter items based on search query
    filter: function(items, query, searchFields) {
        query = query.toLowerCase().trim();
        
        if (!query) {
            items.forEach(item => item.style.display = '');
            return;
        }

        items.forEach(item => {
            let matches = false;
            
            searchFields.forEach(field => {
                const element = item.querySelector(field);
                if (element) {
                    const text = element.textContent.toLowerCase();
                    if (text.includes(query)) {
                        matches = true;
                    }
                }
            });

            item.style.display = matches ? '' : 'none';
        });
    },

    // Setup search input
    setupSearch: function(searchInput, items, searchFields) {
        if (!searchInput) return;

        searchInput.addEventListener('input', function() {
            Search.filter(items, this.value, searchFields);
        });
    }
};

// ==================== Date & Time Utilities ====================
const DateTime = {
    // Format date for display
    formatDate: function(date) {
        if (!(date instanceof Date)) {
            date = new Date(date);
        }
        return date.toLocaleDateString('en-US', { 
            year: 'numeric', 
            month: 'long', 
            day: 'numeric' 
        });
    },

    // Format time for display
    formatTime: function(time) {
        if (typeof time === 'string') {
            const [hours, minutes] = time.split(':');
            const date = new Date();
            date.setHours(parseInt(hours), parseInt(minutes));
            time = date;
        }
        return time.toLocaleTimeString('en-US', { 
            hour: 'numeric', 
            minute: '2-digit',
            hour12: true 
        });
    },

    // Check if date is past
    isPast: function(date) {
        return new Date(date) < new Date();
    },

    // Check if date is today
    isToday: function(date) {
        const today = new Date();
        date = new Date(date);
        return date.getDate() === today.getDate() &&
               date.getMonth() === today.getMonth() &&
               date.getFullYear() === today.getFullYear();
    }
};

// ==================== Export to global scope ====================
window.App = App;
window.Validator = Validator;
window.PasswordStrength = PasswordStrength;
window.PasswordToggle = PasswordToggle;
window.Toast = Toast;
window.Ajax = Ajax;
window.Modal = Modal;
window.Loading = Loading;
window.Search = Search;
window.DateTime = DateTime;
