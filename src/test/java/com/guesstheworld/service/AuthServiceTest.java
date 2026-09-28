package com.guesstheworld.service;

import com.guesstheworld.config.DatabaseConfig;
import com.guesstheworld.model.Role;
import com.guesstheworld.model.User;
import com.guesstheworld.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Auth Service Integration Tests")
class AuthServiceTest {

    private static final String TEST_DB = "jdbc:sqlite:test_auth.db";
    private AuthService authService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        DatabaseConfig.setDatabaseUrl(TEST_DB);
        DatabaseConfig.initializeDatabase();
        userRepository = new UserRepository();
        authService = new AuthService(userRepository, new ValidationService());
    }

    @AfterEach
    void tearDown() {
        DatabaseConfig.resetToDefaultUrl();
        new File("test_auth.db").delete();
    }

    @Test
    @DisplayName("Successfully register a new player user and authenticate")
    void testRegisterAndLoginSuccess() {
        User registered = authService.register("ValidUser", "Secr3t$Pass", Role.PLAYER);
        assertNotNull(registered.getId());
        assertEquals("ValidUser", registered.getUsername());
        assertEquals(Role.PLAYER, registered.getRole());

        User loggedIn = authService.login("ValidUser", "Secr3t$Pass");
        assertNotNull(loggedIn);
        assertEquals(registered.getId(), loggedIn.getId());
    }

    @Test
    @DisplayName("Duplicate username registration should be rejected")
    void testDuplicateUsernameRejection() {
        authService.register("AlphaUser", "Pass123%", Role.PLAYER);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                authService.register("AlphaUser", "OtherPass1*", Role.PLAYER)
        );
        assertTrue(ex.getMessage().contains("already registered"));
    }

    @Test
    @DisplayName("Invalid password during login throws exception")
    void testInvalidPasswordLogin() {
        authService.register("TestPlayer", "Pass123*", Role.PLAYER);

        assertThrows(IllegalArgumentException.class, () ->
                authService.login("TestPlayer", "WrongPass1*")
        );
    }
}
