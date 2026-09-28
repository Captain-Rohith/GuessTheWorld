package com.guesstheworld.service;

import com.guesstheworld.model.Role;
import com.guesstheworld.model.User;
import com.guesstheworld.repository.UserRepository;
import com.guesstheworld.util.PasswordUtil;

import java.util.Optional;

public class AuthService {

    private final UserRepository userRepository;
    private final ValidationService validationService;
    private User currentUser;

    public AuthService(UserRepository userRepository, ValidationService validationService) {
        this.userRepository = userRepository;
        this.validationService = validationService;
    }

    public synchronized User getCurrentUser() {
        return currentUser;
    }

    public synchronized void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public synchronized void logout() {
        this.currentUser = null;
    }

    public synchronized boolean isAuthenticated() {
        return this.currentUser != null;
    }

    public User register(String username, String password) {
        return register(username, password, Role.PLAYER);
    }

    public User register(String username, String password, Role role) {
        ValidationResult usernameVal = validationService.validateUsername(username);
        if (!usernameVal.isValid()) {
            throw new IllegalArgumentException(usernameVal.getFirstError());
        }

        ValidationResult passwordVal = validationService.validatePassword(password);
        if (!passwordVal.isValid()) {
            throw new IllegalArgumentException(passwordVal.getFirstError());
        }

        String trimmedUsername = username.trim();
        if (userRepository.existsByUsername(trimmedUsername)) {
            throw new IllegalArgumentException("Username '" + trimmedUsername + "' is already registered.");
        }

        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(password, salt);

        User newUser = new User(null, trimmedUsername, hash, salt, role != null ? role : Role.PLAYER, null);
        return userRepository.save(newUser);
    }

    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Username and password are required.");
        }

        Optional<User> userOpt = userRepository.findByUsername(username.trim());
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        User user = userOpt.get();
        boolean matches = PasswordUtil.verifyPassword(password, user.getPasswordHash(), user.getSalt());
        if (!matches) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        this.currentUser = user;
        return user;
    }
}
