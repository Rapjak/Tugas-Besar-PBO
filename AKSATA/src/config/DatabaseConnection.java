package config; // Sesuai nama package di gambar

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection;
    
    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Ganti user/password sesuai setting MySQL Anda
                String url = "jdbc:mysql://localhost:3306/db_aksata";
                String user = "root"; 
                String password = ""; 
                
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Status: Terkoneksi ke Database");
            } catch (SQLException e) {
                System.out.println("Gagal Koneksi: " + e.getMessage());
            }
        }
        return connection;
    }
}