package com.volunteermanagement.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    public static String hashPassword(String plain, int strength) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(strength));
    }
    public static boolean verify(String plain, String hashed) {
        return BCrypt.checkpw(plain, hashed);
    }
}