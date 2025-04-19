package backend.dao;

import backend.models.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatBotDAO {
    private Connection connection;

    public ChatBotDAO() {
        connection = Database.getConnect();
    }

    public List<String> getSessionHistoryAsList(String sessionId) {
        List<String> history = new ArrayList<>();
        String sql = "SELECT role, content FROM messages WHERE session_id = ? ORDER BY timestamp ASC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        try {
            String sql = "INSERT INTO messages (session_id, role, content) VALUES (?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setString(1, sessionId);
            stmt.setString(2, role);
            stmt.setString(3, content);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, List<String>> loadAllSessionHistories() {
        Map<String, List<String>> sessionHistory = new HashMap<>();

        try {
            String query = "SELECT session_id, role, content FROM messages ORDER BY session_id, id";
            PreparedStatement stmt = connection.prepareStatement(query);
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
