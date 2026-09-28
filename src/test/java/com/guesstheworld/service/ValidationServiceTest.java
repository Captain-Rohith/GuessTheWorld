package com.guesstheworld.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceTest {

    private ValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new ValidationService();
    }

    @Test
    void testValidUsername() {
        ValidationResult result = validationService.validateUsername("Alice");
        assertTrue(result.isValid());

        ValidationResult result2 = validationService.validateUsername("PlayerOne");
        assertTrue(result2.isValid());

        ValidationResult result3 = validationService.validateUsername("AdminUser123");
        assertTrue(result3.isValid());
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "Abc", "ABCD", "aBcD"})
    void testUsernameTooShort(String shortUser) {
        ValidationResult result = validationService.validateUsername(shortUser);
        assertFalse(result.isValid());
        assertTrue(result.getFirstError().contains("at least 5 letters"));
    }

    @Test
    void testUsernameMissingCase() {
        ValidationResult allLower = validationService.validateUsername("player");
        assertFalse(allLower.isValid());
        assertTrue(allLower.getErrors().stream().anyMatch(e -> e.contains("uppercase")));

        ValidationResult allUpper = validationService.validateUsername("PLAYER");
        assertFalse(allUpper.isValid());
        assertTrue(allUpper.getErrors().stream().anyMatch(e -> e.contains("lowercase")));
    }

    @Test
    void testValidPasswords() {
        assertTrue(validationService.validatePassword("Pass1$").isValid());
        assertTrue(validationService.validatePassword("Secret9%").isValid());
        assertTrue(validationService.validatePassword("Game4*").isValid());
        assertTrue(validationService.validatePassword("Code2&").isValid());
    }

    @Test
    void testPasswordTooShort() {
        ValidationResult result = validationService.validatePassword("P1$");
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("at least 5 characters")));
    }

    @Test
    void testPasswordMissingComponents() {
        ValidationResult noAlpha = validationService.validatePassword("12345$");
        assertFalse(noAlpha.isValid());

        ValidationResult noNum = validationService.validatePassword("Password$");
        assertFalse(noNum.isValid());

        ValidationResult noSpecial = validationService.validatePassword("Password123");
        assertFalse(noSpecial.isValid());
    }

    @Test
    void testWordValidation() {
        assertTrue(validationService.validateWord("APPLE").isValid());
        assertTrue(validationService.validateWord("world").isValid());

        assertFalse(validationService.validateWord("FOUR").isValid());
        assertFalse(validationService.validateWord("ELEVEN").isValid());
        assertFalse(validationService.validateWord("AB12C").isValid());
    }
}
