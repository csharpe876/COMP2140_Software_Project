package com.volunteermanagement.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Secure password hashing and verification using BCrypt.
 * Optimized for Java 21 with input validation and safe defaults.
 */
public final class PasswordUtil {
    private static final int DEFAULT_STRENGTH = 12;
    private static final int MIN_STRENGTH = 4;
    private static final int MAX_STRENGTH = 31;
    private static final int MIN_PASSWORD_LENGTH = 8;

    private PasswordUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Hash a password with default strength (12 rounds).
     */
    public static String hashPassword(String plainPassword) {
        return hashPassword(plainPassword, DEFAULT_STRENGTH);
    }

    /**
     * Hash a password with specified BCrypt strength (work factor).
     * @param plainPassword the plain text password
     * @param strength BCrypt rounds (4-31), higher is more secure but slower
     * @return the hashed password
     */
    public static String hashPassword(String plainPassword, int strength) {
        validatePassword(plainPassword);
        
        int validStrength = Math.clamp(strength, MIN_STRENGTH, MAX_STRENGTH);
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(validStrength));
    }

    /**
     * Verify a plain password against a BCrypt hash.
     * @param plainPassword the plain text password
     * @param hashedPassword the BCrypt hash
     * @return true if password matches
     */
    public static boolean verify(String plainPassword, String hashedPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            return false;
        }
        if (hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Invalid hash format
            return false;
        }
    }

    /**
     * Check if a password meets minimum requirements.
     */
    public static boolean meetsRequirements(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            return false;
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }
        
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    private static void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
        }
    }
}