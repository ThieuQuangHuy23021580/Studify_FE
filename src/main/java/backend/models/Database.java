package backend.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:sqlite:src/main/resources/database.db";
    private static Connection connection = null;

    public static Connection getConnect() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL);
                System.out.println("Kết nối cơ sở dữ liệu thành công");
                initializeDatabase(); // Tạo bảng nếu chưa có
            } catch (SQLException e) {
                System.out.println("Kết nối cơ sở dữ liệu thất bại");
                e.printStackTrace();
                throw new RuntimeException("Không thể kết nối cơ sở dữ liệu", e);
            }
        }
        return connection;
    }

    private static void initializeDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Tạo bảng users
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    email TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    background_id INTEGER,
                    FOREIGN KEY (background_id) REFERENCES backgrounds(id)
                )
                """;
            stmt.execute(createUsersTable);
            System.out.println("Bảng users đã được khởi tạo");
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Đã đóng kết nối cơ sở dữ liệu");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}