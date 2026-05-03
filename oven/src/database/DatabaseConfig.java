package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String DB_NAME = "eticket_db";
    private static final String USER = "root";
    private static final String PASS = ""; 

    public static Connection getConnection() throws SQLException {
        // Daftar port: 3306 (punyamu), 3307 (punya temanmu)
        String[] ports = {"3306", "3307"};
        SQLException lastException = null;

        for (String port : ports) {
            try {
                // Mencoba koneksi satu per satu dengan format URL yang benar
                String url = "jdbc:mysql://localhost:" + port + "/" + DB_NAME + "?connectTimeout=2000";
                
                // Memaksa load driver agar tidak muncul "No suitable driver"
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                return DriverManager.getConnection(url, USER, PASS);
            } catch (ClassNotFoundException e) {
                System.err.println("Driver tidak ditemukan! Pastikan file .jar sudah di-add ke library.");
            } catch (SQLException e) {
                lastException = e;
                System.out.println("Port " + port + " gagal, mencoba port berikutnya...");
            }
        }

        throw new SQLException("Koneksi Database Gagal di port 3306 & 3307. Pastikan MySQL sudah ON!", lastException);
    }
}