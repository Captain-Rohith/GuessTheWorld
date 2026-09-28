package com.guesstheworld.repository;

import com.guesstheworld.config.DatabaseConfig;
import com.guesstheworld.model.DailyReportDto;
import com.guesstheworld.model.UserReportDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportRepository {

    /**
     * Report for a specific day:
     * - Number of users who played on that day
     * - Number of words tried (game sessions)
     * - Number of correct guesses (sessions won)
     */
    public DailyReportDto getReportForDate(LocalDate date) {
        String sql = """
            SELECT 
                session_date,
                COUNT(DISTINCT user_id) AS total_users,
                COUNT(id) AS words_tried,
                SUM(CASE WHEN status = 'WON' THEN 1 ELSE 0 END) AS correct_guesses
            FROM game_sessions
            WHERE session_date = ?
            GROUP BY session_date
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, date.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new DailyReportDto(
                            LocalDate.parse(rs.getString("session_date")),
                            rs.getInt("total_users"),
                            rs.getInt("words_tried"),
                            rs.getInt("correct_guesses")
                    );
                }
            }
            return new DailyReportDto(date, 0, 0, 0);
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching daily report for date: " + date, e);
        }
    }

    /**
     * Report across all recorded days ordered chronologically descending.
     */
    public List<DailyReportDto> getAllDailyReports() {
        String sql = """
            SELECT 
                session_date,
                COUNT(DISTINCT user_id) AS total_users,
                COUNT(id) AS words_tried,
                SUM(CASE WHEN status = 'WON' THEN 1 ELSE 0 END) AS correct_guesses
            FROM game_sessions
            GROUP BY session_date
            ORDER BY session_date DESC
        """;
        List<DailyReportDto> reports = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                reports.add(new DailyReportDto(
                        LocalDate.parse(rs.getString("session_date")),
                        rs.getInt("total_users"),
                        rs.getInt("words_tried"),
                        rs.getInt("correct_guesses")
                ));
            }
            return reports;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all daily reports", e);
        }
    }

    /**
     * Report for a specific user:
     * - Date
     * - Number of words tried on that date
     * - Number of correct guesses on that date
     */
    public List<UserReportDto> getReportForUser(String username) {
        String sql = """
            SELECT 
                u.username,
                gs.session_date,
                COUNT(gs.id) AS words_tried,
                SUM(CASE WHEN gs.status = 'WON' THEN 1 ELSE 0 END) AS correct_guesses
            FROM game_sessions gs
            JOIN users u ON gs.user_id = u.id
            WHERE u.username = ? AND u.role = 'PLAYER'
            GROUP BY u.username, gs.session_date
            ORDER BY gs.session_date DESC
        """;
        List<UserReportDto> reports = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(new UserReportDto(
                            rs.getString("username"),
                            LocalDate.parse(rs.getString("session_date")),
                            rs.getInt("words_tried"),
                            rs.getInt("correct_guesses")
                    ));
                }
            }
            return reports;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching user report for username: " + username, e);
        }
    }

    /**
     * Report for all player users across all dates.
     */
    public List<UserReportDto> getAllUserReports() {
        String sql = """
            SELECT 
                u.username,
                gs.session_date,
                COUNT(gs.id) AS words_tried,
                SUM(CASE WHEN gs.status = 'WON' THEN 1 ELSE 0 END) AS correct_guesses
            FROM game_sessions gs
            JOIN users u ON gs.user_id = u.id
            WHERE u.role = 'PLAYER'
            GROUP BY u.username, gs.session_date
            ORDER BY gs.session_date DESC, u.username ASC
        """;
        List<UserReportDto> reports = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                reports.add(new UserReportDto(
                        rs.getString("username"),
                        LocalDate.parse(rs.getString("session_date")),
                        rs.getInt("words_tried"),
                        rs.getInt("correct_guesses")
                ));
            }
            return reports;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all user reports", e);
        }
    }
}
