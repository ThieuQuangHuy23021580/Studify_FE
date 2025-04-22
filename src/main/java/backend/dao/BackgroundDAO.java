package backend.dao;

import backend.models.Background;
import backend.models.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BackgroundDAO {
    private Connection connection;

    public BackgroundDAO() {
        this.connection = Database.getConnect();
    }

    public List<Background> getAllBackgrounds() {
        List<Background> list = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM backgrounds");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Background bg = new Background(
                        rs.getInt("id"),
                        rs.getString("image_path"),
                        rs.getString("category")
                );
                list.add(bg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Background> getBackgroundsByCategory(String category) {
        List<Background> list = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM backgrounds WHERE category = ?"
            );
            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Background bg = new Background(
                        rs.getInt("id"),
                        rs.getString("image_path"),
                        rs.getString("category")
                );
                list.add(bg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Background getBackgroundById(int id) {
        String sql = "SELECT * FROM backgrounds WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Background background = new Background();
                background.setId(rs.getInt("id"));
                background.setImagePath(rs.getString("image_path"));
                background.setCategory(rs.getString("category"));
                return background;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
