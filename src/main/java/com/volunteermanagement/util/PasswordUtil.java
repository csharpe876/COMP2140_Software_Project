package com.volunteermanagement.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Password utility for hashing and verifying passwords
 */
public class PasswordUtil {
    private static final int BCRYPT_COST = 10;

    /**
     * Hash a plain text password
     *
     * @param plainPassword The plain text password
     * @return Hashed password
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, plainPassword.toCharArray());
    }

    /**
     * Verify a plain text password against a hashed password
     *
     * @param plainPassword  The plain text password
     * @param hashedPassword The hashed password
     * @return true if passwords match, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword);
        return result.verified;
    }

    /**
     * Check if password meets strength requirements
     *
     * @param password Password to check
     * @return true if valid, false otherwise
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
}
