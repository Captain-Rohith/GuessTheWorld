package com.guesstheworld.repository;

import com.guesstheworld.config.DatabaseConfig;
import com.guesstheworld.model.GameSession;
import com.guesstheworld.model.GameStatus;
import com.guesstheworld.model.GuessAttempt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameSessionRepository {

    public GameSession createSession(GameSession session) {
        String sql = """
            INSERT INTO game_sessions (user_id, target_word, max_attempts, attempts_used, status, session_date, created_at, completed_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            LocalDateTime now = session.getCreatedAt() != null ? session.getCreatedAt() : LocalDateTime.now();
            LocalDate sessionDate = session.getSessionDate() != null ? session.getSessionDate() : now.toLocalDate();
            session.setCreatedAt(now);
            session.setSessionDate(sessionDate);

            ps.setLong(1, session.getUserId());
            ps.setString(2, session.getTargetWord().toUpperCase());
            ps.setInt(3, session.getMaxAttempts());
            ps.setInt(4, session.getAttemptsUsed());
            ps.setString(5, session.getStatus().name());
            ps.setString(6, sessionDate.toString());
            ps.setString(7, now.toString());
            ps.setString(8, session.getCompletedAt() != null ? session.getCompletedAt().toString() : null);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    session.setId(rs.getLong(1));
                }
            }
            return session;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating game session", e);
        }
    }

    public void updateSessionStatus(Long sessionId, GameStatus status, LocalDateTime completedAt, int attemptsUsed) {
        String sql = "UPDATE game_sessions SET status = ?, completed_at = ?, attempts_used = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setString(2, completedAt != null ? completedAt.toString() : null);
            ps.setInt(3, attemptsUsed);
            ps.setLong(4, sessionId);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating game session status for id: " + sessionId, e);
        }
    }

    public Optional<GameSession> findById(Long id) {
        String sql = "SELECT id, user_id, target_word, max_attempts, attempts_used, status, session_date, created_at, completed_at FROM game_sessions WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    GameSession session = mapResultSetToGameSession(rs);
                    session.setAttempts(findAttemptsBySessionId(session.getId()));
                    return Optional.of(session);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding game session by id: " + id, e);
        }
    }

    public int countSessionsForUserOnDate(Long userId, LocalDate date) {
        String sql = "SELECT COUNT(*) FROM game_sessions WHERE user_id = ? AND session_date = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setString(2, date.toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting sessions for user on date", e);
        }
    }

    public List<GameSession> findSessionsForUserOnDate(Long userId, LocalDate date) {
        String sql = "SELECT id, user_id, target_word, max_attempts, attempts_used, status, session_date, created_at, completed_at FROM game_sessions WHERE user_id = ? AND session_date = ? ORDER BY created_at ASC";
        List<GameSession> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setString(2, date.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GameSession session = mapResultSetToGameSession(rs);
                    session.setAttempts(findAttemptsBySessionId(session.getId()));
                    list.add(session);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding sessions for user on date", e);
        }
    }

    public Optional<GameSession> findActiveSessionForUser(Long userId) {
        String sql = "SELECT id, user_id, target_word, max_attempts, attempts_used, status, session_date, created_at, completed_at FROM game_sessions WHERE user_id = ? AND status = 'IN_PROGRESS' ORDER BY id DESC LIMIT 1";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    GameSession session = mapResultSetToGameSession(rs);
                    session.setAttempts(findAttemptsBySessionId(session.getId()));
                    return Optional.of(session);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding active session for user", e);
        }
    }

    public GuessAttempt saveGuessAttempt(GuessAttempt attempt) {
        String sql = "INSERT INTO guess_attempts (session_id, attempt_number, guess_word, guessed_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            LocalDateTime now = attempt.getGuessedAt() != null ? attempt.getGuessedAt() : LocalDateTime.now();
            attempt.setGuessedAt(now);

            ps.setLong(1, attempt.getSessionId());
            ps.setInt(2, attempt.getAttemptNumber());
            ps.setString(3, attempt.getGuessWord().toUpperCase());
            ps.setString(4, now.toString());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    attempt.setId(rs.getLong(1));
                }
            }
            return attempt;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving guess attempt", e);
        }
    }

    public List<GuessAttempt> findAttemptsBySessionId(Long sessionId) {
        String sql = "SELECT id, session_id, attempt_number, guess_word, guessed_at FROM guess_attempts WHERE session_id = ? ORDER BY attempt_number ASC";
        List<GuessAttempt> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new GuessAttempt(
                            rs.getLong("id"),
                            rs.getLong("session_id"),
                            rs.getInt("attempt_number"),
                            rs.getString("guess_word"),
                            LocalDateTime.parse(rs.getString("guessed_at"))
                    ));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding attempts by session id: " + sessionId, e);
        }
    }

    private GameSession mapResultSetToGameSession(ResultSet rs) throws SQLException {
        String completedAtStr = rs.getString("completed_at");
        return new GameSession(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getString("target_word"),
                rs.getInt("max_attempts"),
                rs.getInt("attempts_used"),
                GameStatus.valueOf(rs.getString("status")),
                LocalDate.parse(rs.getString("session_date")),
                LocalDateTime.parse(rs.getString("created_at")),
                completedAtStr != null ? LocalDateTime.parse(completedAtStr) : null
        );
    }
}
