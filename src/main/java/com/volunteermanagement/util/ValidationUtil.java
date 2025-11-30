package com.volunteermanagement.util;

public class ValidationUtil {
    public static boolean isEmpty(String s) { return s == null || s.trim().isEmpty(); }
    public static boolean isValidEmail(String e) { return e != null && e.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"); }
    public static boolean isValidPhone(String p) { return p != null && p.matches("^[0-9+()\\-]{7,20}$"); }
    public static String sanitize(String v) { return v == null ? null : v.replace("<","&lt;").replace(">","&gt;").replace("&","&amp;"); }
}