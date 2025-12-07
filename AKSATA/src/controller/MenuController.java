/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import config.DatabaseConnection;
import model.FlavorProfile;
import model.Menu;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Lenovo
 */
public class MenuController {
    public List<Menu> getAvailableMenus(int idCabang) {
        List<Menu> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        
        String sql = "SELECT m.*, mh.harga_jual " +
                     "FROM menu m " +
                     "JOIN menu_cabang mc ON m.id_menu = mc.id_menu " +
                     "LEFT JOIN menu_harga mh ON m.id_menu = mh.id_menu AND mh.ukuran = 'regular' " +
                     "WHERE mc.id_cabang = ? AND mc.is_available = 1";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idCabang);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                // 1. Buat Objek FlavorProfile (Data dummy/dari DB jika ada kolomnya)
                FlavorProfile flavor = new FlavorProfile(
                    rs.getInt("level_acidity"), 
                    rs.getInt("level_sweetness"), 
                    rs.getInt("level_body"), 
                    rs.getInt("level_bitterness")
                );

                // 2. Buat Objek Menu
                Menu m = new Menu(
                    rs.getInt("id_menu"),
                    rs.getString("nama_menu"),
                    rs.getString("kategori"),
                    rs.getString("deskripsi"),
                    rs.getString("nama_petani"),
                    flavor,
                    rs.getDouble("harga_jual") // Harga Regular
                );
                
                list.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public java.util.List<model.AddOn> getAddOnsForMenu(int menuId) {
        java.util.List<model.AddOn> list = new java.util.ArrayList<>();
        Connection conn = config.DatabaseConnection.getConnection();
        
        // Query ini mengambil SEMUA add-on. 
        // Jika di database Anda ada relasi khusus menu-addon, tambahkan WHERE id_menu = ?
        String sql = "SELECT * FROM addons"; 
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            // ps.setInt(1, menuId); // Aktifkan jika tabel addons punya kolom id_menu
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                list.add(new model.AddOn(
                    rs.getInt("id_addon"),
                    rs.getString("nama_addon"),
                    rs.getDouble("harga")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
