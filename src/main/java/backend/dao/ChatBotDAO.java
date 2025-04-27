package backend.dao;

import backend.models.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatBotDAO {

    public List<String> getSessionHistoryAsList(String sessionId) {
        List<String> history = new ArrayList<>();
        String sql = "SELECT role, content FROM messages WHERE session_id = ? ORDER BY timestamp ASC";

        try (Connection conn = Database.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String role = rs.getString("role");
                String message = rs.getString("content");
                history.add(role.substring(0, 1).toUpperCase() + role.substring(1) + ": " + message);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return history;
    }

    public void saveMessage(String sessionId, String role, String content) {
        String sql = "INSERT INTO messages (session_id, role, content) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sessionId);
            stmt.setString(2, role);
            stmt.setString(3, content);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getFirstMessage(String sessionId) {
        String firstMessage = null;
        String sql = "SELECT content FROM messages WHERE session_id = ? AND role = 'user' ORDER BY timestamp ASC LIMIT 1";

        try (Connection conn = Database.getConnect();
             PreparedStatement stmt = (conn != null) ? conn.prepareStatement(sql) : null) {
            stmt.setString(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    firstMessage = rs.getString("content");
                    System.out.println("DAO: Found first user message: [" + firstMessage + "]");
                } else {
                    System.out.println("DAO: No user messages found for session: " + sessionId);
                }
            }

        } catch (SQLException e) {
            System.err.println("DAO ERROR getting first user message for session " + sessionId);
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("ChatBotDAO Error: NullPointerException, likely due to null connection in getFirstUserMessage.");
            e.printStackTrace();

        }
        return firstMessage;
    }

    public Map<String, List<String>> loadAllSessionHistories() {
        Map<String, List<String>> sessionHistory = new HashMap<>();
        String sql = "SELECT session_id, role, content FROM messages ORDER BY session_id, id";
        try (Connection conn = Database.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String sessionId = rs.getString("session_id");
                String sender = rs.getString("role");
                String message = rs.getString("content");

                String formatted = sender + ": " + message;
                sessionHistory.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(formatted);
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println("Lỗi khi tải session từ database: " + e.getMessage());
        }

        return sessionHistory;
    }
}
