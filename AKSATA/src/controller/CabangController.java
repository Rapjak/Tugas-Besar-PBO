/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import config.DatabaseConnection;
import model.Cabang;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Lenovo
 */

public class CabangController {

    public List<Cabang> getAllCabang() {
        List<Cabang> listCabang = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        
        // Query sesuai struktur tabel cabang [cite: 94]
        String sql = "SELECT id_cabang, nama_cabang, alamat FROM cabang";

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                // Bungkus data SQL ke dalam Model
                Cabang c = new Cabang(
                    rs.getInt("id_cabang"),
                    rs.getString("nama_cabang"),
                    rs.getString("alamat")
                );
                listCabang.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return listCabang;
    }
}
