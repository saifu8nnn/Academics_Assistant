package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Make sure these match your MySQL Workbench connection settings
    private static final String URL = "jdbc:mysql://localhost:3306/academic_assistant?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "saif";
    private static final String PASSWORD = "sasasasa";  // Empty password

    public static Connection getConnection() throws SQLException {
        try {
            System.out.println("Step 1: Loading MySQL JDBC Driver...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Step 2: Driver loaded successfully!");

            System.out.println("Step 3: Attempting to connect to database...");
            System.out.println("URL: " + URL);
            System.out.println("User: " + USER);

            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Step 4: Database connection successful!");
            return conn;
        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: MySQL JDBC Driver not found!");
            System.out.println("Error message: " + e.getMessage());
            throw new SQLException("MySQL JDBC Driver not found.", e);
        } catch (SQLException e) {
            System.out.println("ERROR: Failed to connect to database!");
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Error code: " + e.getErrorCode());
            System.out.println("SQL State: " + e.getSQLState());
            throw e;
        }
    }
}