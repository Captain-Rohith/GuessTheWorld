package com.guesstheworld.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ValidationService {

    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[$%*&]");
    private static final Pattern ALPHA_PATTERN = Pattern.compile("[a-zA-Z]");
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern FIVE_LETTER_WORD_PATTERN = Pattern.compile("^[A-Za-z]{5}$");

    public ValidationResult validateUsername(String username) {
        List<String> errors = new ArrayList<>();

        if (username == null || username.trim().isEmpty()) {
            errors.add("Username cannot be empty.");
            return ValidationResult.failure(errors);
        }

        String trimmed = username.trim();
        long letterCount = trimmed.chars().filter(Character::isLetter).count();
        if (letterCount < 5) {
            errors.add("Username must contain at least 5 letters (found " + letterCount + ").");
        }

        if (!UPPERCASE_PATTERN.matcher(trimmed).find()) {
            errors.add("Username must contain at least one uppercase letter (A-Z).");
        }

        if (!LOWERCASE_PATTERN.matcher(trimmed).find()) {
            errors.add("Username must contain at least one lowercase letter (a-z).");
        }

        if (errors.isEmpty()) {
            return ValidationResult.success();
        }
        return ValidationResult.failure(errors);
    }

    public ValidationResult validatePassword(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.isEmpty()) {
            errors.add("Password cannot be empty.");
            return ValidationResult.failure(errors);
        }

        if (password.length() < 5) {
            errors.add("Password must be at least 5 characters long (current length: " + password.length() + ").");
        }

        if (!ALPHA_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one alphabetic character (a-z, A-Z).");
        }

        if (!NUMERIC_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one numeric digit (0-9).");
        }

        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one special character from: $, %, *, &");
        }

        if (errors.isEmpty()) {
            return ValidationResult.success();
        }
        return ValidationResult.failure(errors);
    }

    public ValidationResult validateWord(String word) {
        List<String> errors = new ArrayList<>();

        if (word == null || word.trim().isEmpty()) {
            errors.add("Word cannot be empty.");
            return ValidationResult.failure(errors);
        }

        String cleaned = word.trim();
        if (cleaned.length() != 5) {
            errors.add("Word must be exactly 5 letters (current length: " + cleaned.length() + ").");
        }

        if (!FIVE_LETTER_WORD_PATTERN.matcher(cleaned).matches()) {
            errors.add("Word must consist only of alphabetic letters (A-Z).");
        }

        if (errors.isEmpty()) {
            return ValidationResult.success();
        }
        return ValidationResult.failure(errors);
    }

    public ValidationResult validateGuess(String guess) {
        return validateWord(guess);
    }
}
