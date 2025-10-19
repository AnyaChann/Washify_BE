package com.washify.apis.util;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * Utility class cho validation
 */
public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(\\+84|0)[3|5|7|8|9][0-9]{8}$"
    );

    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._-]{3,20}$"
    );

    private ValidationUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Validate email
     */
    public static boolean isValidEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate phone number (Vietnam format)
     */
    public static boolean isValidPhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Validate username
     */
    public static boolean isValidUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * Validate password strength
     * At least 8 characters, must contain uppercase, lowercase, number
     */
    public static boolean isStrongPassword(String password) {
        if (!StringUtils.hasText(password) || password.length() < 8) {
            return false;
        }
        
        boolean hasUppercase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowercase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        
        return hasUppercase && hasLowercase && hasDigit;
    }

    /**
     * Validate positive number
     */
    public static boolean isPositive(Number number) {
        if (number == null) {
            return false;
        }
        return number.doubleValue() > 0;
    }

    /**
     * Validate non-negative number
     */
    public static boolean isNonNegative(Number number) {
        if (number == null) {
            return false;
        }
        return number.doubleValue() >= 0;
    }

    /**
     * Validate string không rỗng và không chỉ chứa whitespace
     */
    public static boolean isNotBlank(String str) {
        return StringUtils.hasText(str);
    }
}
