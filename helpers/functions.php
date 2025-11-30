<?php
/**
 * Helper Functions
 * 
 * Common utility functions used throughout the application
 */

/**
 * Sanitize input data
 * 
 * @param string $data Input data to sanitize
 * @return string Sanitized data
 */
function sanitize($data) {
    $data = trim($data);
    $data = stripslashes($data);
    $data = htmlspecialchars($data);
    return $data;
}

/**
 * Redirect to a specific page
 * 
 * @param string $page Page to redirect to
 */
function redirect($page) {
    header("Location: " . BASE_URL . $page);
    exit();
}

/**
 * Check if user is logged in
 * 
 * @return bool True if logged in, false otherwise
 */
function isLoggedIn() {
    return isset($_SESSION['user_id']) && !empty($_SESSION['user_id']);
}

/**
 * Check if user is admin
 * 
 * @return bool True if admin, false otherwise
 */
function isAdmin() {
    return isset($_SESSION['role']) && $_SESSION['role'] === 'admin';
}

/**
 * Require login to access page
 */
function requireLogin() {
    if (!isLoggedIn()) {
        $_SESSION['error'] = "Please login to access this page";
        redirect('views/auth/login.php');
    }
}

/**
 * Require admin role to access page
 */
function requireAdmin() {
    requireLogin();
    if (!isAdmin()) {
        $_SESSION['error'] = "Access denied. Admin privileges required.";
        redirect('views/dashboard.php');
    }
}

/**
 * Format date for display
 * 
 * @param string $date Date string
 * @return string Formatted date
 */
function formatDate($date) {
    return date('F j, Y', strtotime($date));
}

/**
 * Format datetime for display
 * 
 * @param string $datetime Datetime string
 * @return string Formatted datetime
 */
function formatDateTime($datetime) {
    return date('F j, Y g:i A', strtotime($datetime));
}

/**
 * Get flash message and clear it
 * 
 * @param string $type Message type (success, error, info)
 * @return string|null Message or null
 */
function getFlashMessage($type) {
    if (isset($_SESSION[$type])) {
        $message = $_SESSION[$type];
        unset($_SESSION[$type]);
        return $message;
    }
    return null;
}

/**
 * Set flash message
 * 
 * @param string $type Message type (success, error, info)
 * @param string $message Message content
 */
function setFlashMessage($type, $message) {
    $_SESSION[$type] = $message;
}

/**
 * Validate email address
 * 
 * @param string $email Email to validate
 * @return bool True if valid, false otherwise
 */
function validateEmail($email) {
    return filter_var($email, FILTER_VALIDATE_EMAIL);
}

/**
 * Validate phone number (Jamaican format)
 * 
 * @param string $phone Phone number to validate
 * @return bool True if valid, false otherwise
 */
function validatePhone($phone) {
    // Remove spaces and dashes
    $phone = preg_replace('/[\s\-]/', '', $phone);
    // Check if it matches Jamaican phone format (10 digits)
    return preg_match('/^[0-9]{10}$/', $phone);
}

/**
 * Generate random string
 * 
 * @param int $length Length of string
 * @return string Random string
 */
function generateRandomString($length = 10) {
    return bin2hex(random_bytes($length / 2));
}
?>
