package backend.models;

import java.sql.*;

public class Database {
    private static final String URL = "jdbc:sqlite:src/main/resources/database.db";
    private static Connection connection = null;

    public static Connection getConnect() {
        try {
            connection = DriverManager.getConnection(URL);
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA journal_mode=WAL;");
                System.out.println("WAL mode enabled.");
            } catch (SQLException walEx) {
                System.err.println("Failed to enable WAL mode: " + walEx.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Failed to connect to database");
            e.printStackTrace();
            return null;
        }
        return connection;
    }
}
