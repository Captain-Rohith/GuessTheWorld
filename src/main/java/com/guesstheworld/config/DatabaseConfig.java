package com.guesstheworld.config;

import com.guesstheworld.model.Role;
import com.guesstheworld.util.PasswordUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class DatabaseConfig {

    private static final String DEFAULT_DB_URL = "jdbc:sqlite:guesstheworld.db";
    private static String currentDbUrl = DEFAULT_DB_URL;

    public static final List<String> INITIAL_WORDS = Arrays.asList(
            "APPLE", "BRAVE", "CRANE", "DRIVE", "EAGLE",
            "FLAME", "GRACE", "HOUSE", "LIGHT", "MANGO",
            "NOBLE", "OCEAN", "PIANO", "QUEEN", "RIVER",
            "SHINE", "TIGER", "UNITY", "VIPER", "WORLD"
    );

    private DatabaseConfig() {
    }

    public static synchronized void setDatabaseUrl(String dbUrl) {
        currentDbUrl = dbUrl;
    }

    public static synchronized void resetToDefaultUrl() {
        currentDbUrl = DEFAULT_DB_URL;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(currentDbUrl);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON;");

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password_hash TEXT NOT NULL,
                    salt TEXT NOT NULL,
                    role TEXT NOT NULL,
                    created_at TEXT NOT NULL
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS words (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    word TEXT UNIQUE NOT NULL,
                    active INTEGER NOT NULL DEFAULT 1,
                    created_at TEXT NOT NULL
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS game_sessions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    target_word TEXT NOT NULL,
                    max_attempts INTEGER NOT NULL DEFAULT 5,
                    attempts_used INTEGER NOT NULL DEFAULT 0,
                    status TEXT NOT NULL,
                    session_date TEXT NOT NULL,
                    created_at TEXT NOT NULL,
                    completed_at TEXT,
                    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS guess_attempts (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    session_id INTEGER NOT NULL,
                    attempt_number INTEGER NOT NULL,
                    guess_word TEXT NOT NULL,
                    guessed_at TEXT NOT NULL,
                    FOREIGN KEY(session_id) REFERENCES game_sessions(id) ON DELETE CASCADE
                );
            """);

            seedInitialWords(conn);
            seedDefaultUsers(conn);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private static void seedInitialWords(Connection conn) throws SQLException {
        try (Statement checkStmt = conn.createStatement();
             ResultSet rs = checkStmt.executeQuery("SELECT COUNT(*) FROM words")) {
            if (rs.next() && rs.getInt(1) == 0) {
                String insertSql = "INSERT INTO words (word, active, created_at) VALUES (?, 1, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    String now = LocalDateTime.now().toString();
                    for (String word : INITIAL_WORDS) {
                        ps.setString(1, word.toUpperCase());
                        ps.setString(2, now);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
        }
    }

    private static void seedDefaultUsers(Connection conn) throws SQLException {
        try (Statement checkStmt = conn.createStatement();
             ResultSet rs = checkStmt.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.next() && rs.getInt(1) == 0) {
                String adminSalt = PasswordUtil.generateSalt();
                String adminHash = PasswordUtil.hashPassword("AdminPassword1$", adminSalt);

                String playerSalt = PasswordUtil.generateSalt();
                String playerHash = PasswordUtil.hashPassword("PlayerPassword1%", playerSalt);

                String insertSql = "INSERT INTO users (username, password_hash, salt, role, created_at) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    String now = LocalDateTime.now().toString();

                    ps.setString(1, "AdminUser");
                    ps.setString(2, adminHash);
                    ps.setString(3, adminSalt);
                    ps.setString(4, Role.ADMIN.name());
                    ps.setString(5, now);
                    ps.executeUpdate();

                    ps.setString(1, "PlayerOne");
                    ps.setString(2, playerHash);
                    ps.setString(3, playerSalt);
                    ps.setString(4, Role.PLAYER.name());
                    ps.setString(5, now);
                    ps.executeUpdate();
                }
            }
        }
    }
}
