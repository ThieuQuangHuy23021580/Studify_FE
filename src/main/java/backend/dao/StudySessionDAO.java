package backend.dao;

import backend.models.Database;
import backend.models.StudySession;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class StudySessionDAO {
    private Connection getConnection() throws SQLException {
        return Database.getConnect();
    }

    public void insertSession(StudySession session) {
        String sql = "INSERT INTO study_sessions (user_id, date, duration_minutes) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, session.getUserId());
            stmt.setString(2, session.getDate().toString());
            stmt.setInt(3, session.getDurationMinutes());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<StudySession> getSessionsByUser(int userId) {
        List<StudySession> list = new ArrayList<>();
        String sql = "SELECT * FROM study_sessions WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                StudySession session = new StudySession(
                        rs.getInt("user_id"),
                        LocalDate.parse(rs.getString("date")),
                        rs.getInt("duration_minutes")
                );
                session.setId(rs.getInt("id"));
                list.add(session);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
