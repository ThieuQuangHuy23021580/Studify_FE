package backend.dao;

import backend.models.Database;
import backend.models.StudySession;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StudySessionDAO {
    public void insert(StudySession s) {
        String sql = """
            INSERT INTO study_sessions 
                   (user_id, started_at, ended_at, duration_minutes)
            VALUES (?, ?, ?, ?)""";

        try (Connection conn = Database.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt   (1, s.getUserId());
            ps.setString(2, s.getStartedAt().toString());
            ps.setString(3, s.getEndedAt().toString());
            ps.setInt   (4, s.getDurationMinutes());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<StudySession> findByUser(int userId) {
        List<StudySession> list = new ArrayList<>();
        String sql = "SELECT * FROM study_sessions WHERE user_id = ? ORDER BY started_at DESC";

        try (Connection conn = Database.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                StudySession s = new StudySession();
                s.setId            (rs.getInt("id"));
                s.setUserId        (rs.getInt("user_id"));
                s.setStartedAt     (LocalDateTime.parse(rs.getString("started_at")));
                s.setEndedAt       (LocalDateTime.parse(rs.getString("ended_at")));
                s.setDurationMinutes(rs.getInt("duration_minutes"));
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int totalMinutesByDate(int userId, String date) {
        String sql = """
            SELECT COALESCE(SUM(duration_minutes),0) 
            FROM study_sessions
            WHERE user_id = ? AND DATE(started_at) = ?""";

        try (Connection conn = Database.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, date);
            ResultSet rs = ps.executeQuery();
            return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
