package backend.dao;

import backend.models.Database;
import backend.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private Connection connection;

    public UserDAO() {
        connection = Database.getConnect();
    }

    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("id"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    user.setBackgroundId(rs.getInt("background_id"));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể tìm người dùng theo email", e);
        }
        return null;
    }

    public boolean insert(User user) {
        String sql = "INSERT INTO users (password, email) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getPassword());
            stmt.setString(2, user.getEmail());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể thêm người dùng", e);
        }
    }

    public boolean setUserBackground(int userId, int backgroundId) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "UPDATE users SET background_id = ? WHERE id = ?"
        )) {
            stmt.setInt(1, backgroundId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể cập nhật background cho người dùng", e);
        }
    }

    public int getUserBackgroundId(int userId) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT background_id FROM users WHERE id = ?"
        )) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("background_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể lấy background_id của người dùng", e);
        }
        return -1;
    }
}