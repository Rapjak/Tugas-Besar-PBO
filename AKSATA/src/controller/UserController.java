/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import config.DatabaseConnection;
import model.User;
import java.sql.*;

/**
 *
 * @author Lenovo
 */
public class UserController {
    public User login(String username, String password) {
        Connection conn = DatabaseConnection.getConnection();
        User user = null;
        
        // Query cek user (Sesuai tabel users di database)
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password); // Idealnya password di-hash (MD5/Bcrypt), tapi untuk belajar plain text dulu oke
            
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Login Sukses -> Bungkus data ke Model User
                user = new User(
                    rs.getInt("id_user"),
                    rs.getString("username"),
                    rs.getString("role"),
                    rs.getInt("id_cabang")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return user;
    }
}
