<?php
/**
 * Database Configuration
 * 
 * This file contains all database connection settings.
 * Modify these values according to your local environment.
 */

// Database configuration
define('DB_HOST', 'localhost');
define('DB_USER', 'root');
define('DB_PASS', '');
define('DB_NAME', 'volunteer_management');

// Database connection class
class Database {
    private $host = DB_HOST;
    private $user = DB_USER;
    private $pass = DB_PASS;
    private $dbname = DB_NAME;
    private $conn;
    private $error;

    /**
     * Establish database connection
     * 
     * @return mysqli Database connection object
     */
    public function connect() {
        $this->conn = null;

        try {
            $this->conn = new mysqli($this->host, $this->user, $this->pass, $this->dbname);
            
            if ($this->conn->connect_error) {
                throw new Exception("Connection failed: " . $this->conn->connect_error);
            }
            
            // Set charset to utf8mb4 for full Unicode support
            $this->conn->set_charset("utf8mb4");
            
        } catch(Exception $e) {
            $this->error = $e->getMessage();
            return false;
        }

        return $this->conn;
    }

    /**
     * Get connection error
     * 
     * @return string Error message
     */
    public function getError() {
        return $this->error;
    }

    /**
     * Close database connection
     */
    public function close() {
        if ($this->conn) {
            $this->conn->close();
        }
    }
}
?>
