package backend.dao;

import backend.models.Database;
import backend.models.StudySession;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class StudySessionDAO {
    private final Connection conn;

    public StudySessionDAO() {
        this.conn = Database.getConnect();
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

    public Map<String, Integer> getDailyDurations(int userId, int limit) {
        Map<String, Integer> dailyData = new LinkedHashMap<>();
        String sql = "SELECT strftime('%Y-%m-%d', date) as study_day, SUM(duration_minutes) as total_minutes " +
                "FROM study_sessions " +
                "WHERE user_id = ? " +
                "GROUP BY study_day " +
                "ORDER BY study_day DESC " +
                "LIMIT ?";

        try (Connection conn = Database.getConnect();
             PreparedStatement stmt = (conn != null) ? conn.prepareStatement(sql) : null) {

            if (stmt == null) {
                System.err.println("StudySessionDAO Error: Cannot prepare statement for getDailyDurations.");
                return dailyData;
            }

            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dailyData.put(rs.getString("study_day"), rs.getInt("total_minutes"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching daily study durations for user " + userId);
            e.printStackTrace();
        }
        return dailyData;
    }

    /**
     * Lấy tổng số phút học mỗi tháng cho một user_id.
     * @param userId ID của người dùng.
     * @param limit Số tháng gần nhất muốn lấy.
     * @return Map với Key là tháng (YYYY-MM), Value là tổng số phút. Sắp xếp theo tháng giảm dần.
     */
    public Map<String, Integer> getMonthlyDurations(int userId, int limit) {
        Map<String, Integer> monthlyData = new LinkedHashMap<>();
        String sql = "SELECT strftime('%Y-%m', date) as study_month, SUM(duration_minutes) as total_minutes " +
                "FROM study_sessions " +
                "WHERE user_id = ? " +
                "GROUP BY study_month " +
                "ORDER BY study_month DESC " +
                "LIMIT ?";

        try (Connection conn = Database.getConnect();
             PreparedStatement stmt = (conn != null) ? conn.prepareStatement(sql) : null) {

            if (stmt == null) { /* Lỗi */ return monthlyData; }
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    monthlyData.put(rs.getString("study_month"), rs.getInt("total_minutes"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching monthly study durations for user " + userId);
            e.printStackTrace();
        }
        return monthlyData;
    }
}