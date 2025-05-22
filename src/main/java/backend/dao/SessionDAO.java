package backend.dao;

import backend.models.Database;
import backend.models.Session;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SessionDAO {
    private Connection conn = Database.getConnect();

    public boolean createSession(Session session) {
        String sql = "INSERT INTO sessions (id, user_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, session.getSessionId());
            stmt.setString(2, session.getUserId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error creating session: " + e.getMessage());
            return false;
        }
    }

    public List<Session> getAllSessionsByUser(String userId) {
        List<Session> sessions = new ArrayList<>();
        String sql = "SELECT * FROM sessions WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                sessions.add(new Session(rs.getString("id"), rs.getString("user_id")));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching sessions: " + e.getMessage());
        }
        return sessions;
    }

    public String getUserIdBySessionId(String sessionId) {
        String sql = "SELECT user_id FROM sessions WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean sessionExists(String sessionId) {
        String sql = "SELECT 1 FROM sessions WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error checking session: " + e.getMessage());
            return false;
        }
    }


}