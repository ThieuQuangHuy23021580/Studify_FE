package backend.models;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:sqlite:src/main/resources/database.db";
    private static Connection connection = null;

    public static Connection getConnect() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL);
                System.out.println("Connected to database");
            } catch (SQLException e) {
                System.out.println("Failed to connect to database");
                e.printStackTrace();
            }
        }
        return connection;
    }
}
