package com.guesstheworld.repository;

import com.guesstheworld.config.DatabaseConfig;
import com.guesstheworld.model.Word;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WordRepository {

    public Word save(Word word) {
        String sql = "INSERT INTO words (word, active, created_at) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            LocalDateTime now = word.getCreatedAt() != null ? word.getCreatedAt() : LocalDateTime.now();
            word.setCreatedAt(now);

            ps.setString(1, word.getWord().toUpperCase());
            ps.setInt(2, word.isActive() ? 1 : 0);
            ps.setString(3, now.toString());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    word.setId(rs.getLong(1));
                }
            }
            return word;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving word: " + word.getWord(), e);
        }
    }

    public Optional<Word> findByWord(String word) {
        String sql = "SELECT id, word, active, created_at FROM words WHERE UPPER(word) = UPPER(?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, word);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToWord(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding word: " + word, e);
        }
    }

    public List<Word> findAll() {
        String sql = "SELECT id, word, active, created_at FROM words ORDER BY word ASC";
        List<Word> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToWord(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all words", e);
        }
    }

    public List<Word> findAllActive() {
        String sql = "SELECT id, word, active, created_at FROM words WHERE active = 1 ORDER BY word ASC";
        List<Word> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToWord(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching active words", e);
        }
    }

    /**
     * Retrieves a random active word from the database for gameplay.
     */
    public Optional<Word> getRandomActiveWord() {
        String sql = "SELECT id, word, active, created_at FROM words WHERE active = 1 ORDER BY RANDOM() LIMIT 1";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return Optional.of(mapResultSetToWord(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving random active word", e);
        }
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM words WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting word id: " + id, e);
        }
    }

    public boolean updateStatus(Long id, boolean active) {
        String sql = "UPDATE words SET active = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, active ? 1 : 0);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating word status for id: " + id, e);
        }
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM words";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting words", e);
        }
    }

    private Word mapResultSetToWord(ResultSet rs) throws SQLException {
        return new Word(
                rs.getLong("id"),
                rs.getString("word"),
                rs.getInt("active") == 1,
                LocalDateTime.parse(rs.getString("created_at"))
        );
    }
}
