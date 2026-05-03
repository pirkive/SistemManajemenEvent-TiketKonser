package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String URL = "jdbc:mysql://localhost:3306,localhost:3307/eticket_db?connectTimeout=5000";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection getConnection() throws SQLException {
        // Memastikan Driver MySQL diload
        return DriverManager.getConnection(URL, USER, PASS);
    }
}