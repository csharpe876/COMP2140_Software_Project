package com.volunteermanagement.util;

import java.util.regex.Pattern;

/**
 * Input validation and sanitization utilities.
 * Optimized for Java 21 with compiled patterns and comprehensive checks.
 */
public final class ValidationUtil {
    // Compiled regex patterns for better performance
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[0-9+()\\-\\s]{7,20}$"
    );
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_]{3,30}$"
    );
    
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i).*(union|select|insert|update|delete|drop|create|alter|exec|script).*"
    );

    private ValidationUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Check if a string is null, empty, or contains only whitespace.
     */
    public static boolean isEmpty(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Check if a string is NOT empty.
     */
    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    /**
     * Validate email format using compiled regex pattern.
     */
    public static boolean isValidEmail(String email) {
        return email != null && !email.isBlank() && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validate phone number format (allows digits, +, -, (), and spaces).
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && !phone.isBlank() && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Validate username format (alphanumeric and underscore, 3-30 chars).
     */
    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * Check for potential SQL injection patterns.
     */
    public static boolean containsSqlInjection(String input) {
        return input != null && SQL_INJECTION_PATTERN.matcher(input).matches();
    }

    /**
     * Sanitize HTML special characters to prevent XSS.
     * Order matters: & must be replaced first.
     */
    public static String sanitizeHtml(String value) {
        if (value == null) return null;
        
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")
            .replace("/", "&#x2F;");
    }

    /**
     * Trim and sanitize input, returning null for empty strings.
     */
    public static String cleanInput(String value) {
        if (isEmpty(value)) return null;
        return sanitizeHtml(value.trim());
    }

    /**
     * Validate that a number is within a specified range (inclusive).
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * Validate string length is within range.
     */
    public static boolean isLengthValid(String value, int minLength, int maxLength) {
        if (value == null) return false;
        int length = value.length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Check if a string contains only alphanumeric characters.
     */
    public static boolean isAlphanumeric(String value) {
        if (isEmpty(value)) return false;
        return value.chars().allMatch(Character::isLetterOrDigit);
    }
}