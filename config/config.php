<?php
/**
 * Application Configuration
 * 
 * Global configuration settings for the Volunteer Management System
 */

// Start session if not already started
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

// Application settings
define('APP_NAME', 'Volunteer Management System');
define('APP_VERSION', '1.0.0');
define('BASE_URL', 'http://localhost/COMP2140_Software_Project/');

// Timezone
date_default_timezone_set('America/Jamaica');

// Error reporting (set to 0 in production)
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Include database configuration
require_once __DIR__ . '/database.php';

// Autoload helper functions
require_once __DIR__ . '/../helpers/functions.php';
?>
