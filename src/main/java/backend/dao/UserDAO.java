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
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("id"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
        }
        return false;
    }

    public boolean setUserBackground(int userId, int backgroundId) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE users SET background_id = ? WHERE id = ?"
            );
            stmt.setInt(1, backgroundId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getUserBackgroundId(int userId) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT background_id FROM users WHERE id = ?"
            );
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("background_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean setUserEducationLevel(int userId, String educationLevel) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE users SET education_level = ? WHERE id = ?"
            );
            stmt.setString(1, educationLevel);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getUserEducationLevel(int userId) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT education_level FROM users WHERE id = ?"
            );
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("education_level");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean setUserAvatar(int userId, String avatar) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE users SET avatar = ? WHERE id = ?"
            );
            stmt.setString(1, avatar);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getUserAvatar(int userId) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT avatar FROM users WHERE id = ?"
            );
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("avatar");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean setUserName(int userId, String name) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE users SET user_name = ? WHERE id = ?"
            );
            stmt.setString(1, name);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getUserName(int userId) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT user_name FROM users WHERE id = ?"
            );
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("user_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
