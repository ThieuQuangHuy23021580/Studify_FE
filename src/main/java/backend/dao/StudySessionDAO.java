package backend.dao;

import backend.models.StudySession;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class StudySessionDAO {
    private final Connection conn;

    public StudySessionDAO(Connection conn) {
        this.conn = conn;
    }

    public void createTableIfNotExists() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS study_sessions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                date TEXT,
                duration_minutes INTEGER
            );
        """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void saveSession(StudySession session) throws SQLException {
        String sql = """
            INSERT INTO study_sessions (user_id, date, duration_minutes)
            VALUES (?, ?, ?);
        """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, session.getUserId());
            stmt.setString(2, session.getDate().toString());
            stmt.setInt(3, session.getDurationMinutes());
            stmt.executeUpdate();
        }
    }

    public int getTotalMinutes(int userId) throws SQLException {
        String sql = "SELECT SUM(duration_minutes) FROM study_sessions WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public double getAverageMinutesPerDay(int userId) throws SQLException {
        String countSql = "SELECT COUNT(DISTINCT date) FROM study_sessions WHERE user_id = ?";
        String totalSql = "SELECT SUM(duration_minutes) FROM study_sessions WHERE user_id = ?";
        try (
                PreparedStatement countStmt = conn.prepareStatement(countSql);
                PreparedStatement totalStmt = conn.prepareStatement(totalSql)
        ) {
            countStmt.setInt(1, userId);
            totalStmt.setInt(1, userId);
            ResultSet countRs = countStmt.executeQuery();
            ResultSet totalRs = totalStmt.executeQuery();
            int days = countRs.next() ? countRs.getInt(1) : 0;
            int total = totalRs.next() ? totalRs.getInt(1) : 0;
            return (days > 0) ? (double) total / days : 0;
        }
    }

    public int getCurrentStreak(int userId) throws SQLException {
        String sql = """
            SELECT date, SUM(duration_minutes) AS minutes
            FROM study_sessions
            WHERE user_id = ?
            GROUP BY date
            ORDER BY date DESC
        """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            int streak = 0;
            LocalDate today = LocalDate.now();

            while (rs.next()) {
                LocalDate date = LocalDate.parse(rs.getString("date"));
                int minutes = rs.getInt("minutes");
                long daysDiff = java.time.temporal.ChronoUnit.DAYS.between(date, today.minusDays(streak));
                if (daysDiff == 0 && minutes >= 10) {
                    streak++;
                } else {
                    break;
                }
            }
            return streak;
        }
    }

    public int getMaxStreak(int userId) throws SQLException {
        String sql = """
            SELECT date, SUM(duration_minutes) AS minutes
            FROM study_sessions
            WHERE user_id = ?
            GROUP BY date
            ORDER BY date ASC
        """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            List<LocalDate> validDates = new ArrayList<>();
            while (rs.next()) {
                if (rs.getInt("minutes") >= 10) {
                    validDates.add(LocalDate.parse(rs.getString("date")));
                }
            }

            int maxStreak = 0;
            int currentStreak = 1;

            for (int i = 1; i < validDates.size(); i++) {
                long diff = java.time.temporal.ChronoUnit.DAYS.between(validDates.get(i - 1), validDates.get(i));
                if (diff == 1) {
                    currentStreak++;
                } else {
                    maxStreak = Math.max(maxStreak, currentStreak);
                    currentStreak = 1;
                }
            }
            return Math.max(maxStreak, currentStreak);
        }
    }
}