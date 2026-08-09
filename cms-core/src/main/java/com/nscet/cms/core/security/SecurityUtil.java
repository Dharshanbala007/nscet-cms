package com.nscet.cms.core.security;

import java.util.regex.Pattern;

public final class SecurityUtil {

    private SecurityUtil() {}

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern AADHAR_PATTERN = Pattern.compile("^\\d{12}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z\\s.'-]{1,150}$");

    public static String maskAadhar(String aadhar) {
        if (aadhar == null || aadhar.length() < 4) return "****-****-****";
        return "****-****-" + aadhar.substring(aadhar.length() - 4);
    }

    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) return "****";
        return "****" + phone.substring(phone.length() - 4);
    }

    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "****";
        String[] parts = email.split("@");
        String name = parts[0];
        String domain = parts[1];
        if (name.length() <= 2) return "*@" + domain;
        return name.charAt(0) + "*".repeat(name.length() - 2) + name.charAt(name.length() - 1) + "@" + domain;
    }

    public static String sanitize(String input) {
        if (input == null) return null;
        return input.replaceAll("[<>\"';&]", "").trim();
    }

    public static boolean isValidPhone(String phone) {
        return phone == null || PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidAadhar(String aadhar) {
        return aadhar == null || aadhar.isEmpty() || AADHAR_PATTERN.matcher(aadhar).matches();
    }

    public static boolean isValidEmail(String email) {
        return email == null || email.isEmpty() || EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    public static String getPasswordPolicyMessage() {
        return "Password must be at least 8 characters with: 1 uppercase, 1 lowercase, 1 digit, 1 special character";
    }
}
