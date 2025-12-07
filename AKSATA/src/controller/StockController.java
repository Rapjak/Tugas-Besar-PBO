package controller;

import config.DatabaseConnection;
import model.CartItem;
import model.AddOn;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class StockController {
    
    // Method ini dipanggil oleh OrderController saat checkout
    // Menerima Connection yang sama agar transaksi bersifat atomik (semua sukses atau semua gagal)
    public void deductStock(Connection conn, List<CartItem> items, int idCabang) throws SQLException {
        
        // 1. Query Ambil Resep Menu Utama
        String sqlResepMenu = "SELECT id_bahan, jumlah_pakai FROM resep WHERE id_menu = ? AND ukuran = ?";
        PreparedStatement psResepMenu = conn.prepareStatement(sqlResepMenu);
        
        // 2. Query Ambil Resep Add-on (Jika add-on terhubung ke bahan baku)
        String sqlResepAddon = "SELECT id_bahan_terkait, jumlah_pakai_bahan FROM addons WHERE id_addon = ?";
        PreparedStatement psResepAddon = conn.prepareStatement(sqlResepAddon);
        
        // 3. Query Update Stok
        String sqlUpdateStok = "UPDATE stok_cabang SET stok_sistem = stok_sistem - ? WHERE id_cabang = ? AND id_bahan = ?";
        PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateStok);
        
        // 4. Query Cek Stok Cukup (Opsional, tapi disarankan)
        String sqlCekStok = "SELECT stok_sistem FROM stok_cabang WHERE id_cabang = ? AND id_bahan = ?";
        PreparedStatement psCek = conn.prepareStatement(sqlCekStok);

        for (CartItem item : items) {
            
            // --- A. POTONG STOK UNTUK MENU UTAMA ---
            // Kita perlu mapping ukuran dari GUI ("Regular", "Large (+5k)") ke database enum ('regular', 'large', '1liter')
            String dbSize = mapSizeToDB(item.getDetail()); 
            
            psResepMenu.setInt(1, item.getIdMenu());
            psResepMenu.setString(2, dbSize);
            ResultSet rsMenu = psResepMenu.executeQuery();
            
            while (rsMenu.next()) {
                int idBahan = rsMenu.getInt("id_bahan");
                double qtyPakai = rsMenu.getDouble("jumlah_pakai");
                
                // Kurangi stok
                reduceStock(psUpdate, psCek, idCabang, idBahan, qtyPakai);
            }
            
            // --- B. POTONG STOK UNTUK ADD-ONS ---
            for (AddOn ad : item.getSelectedAddOns()) {
                psResepAddon.setInt(1, ad.getId());
                ResultSet rsAddon = psResepAddon.executeQuery();
                
                if (rsAddon.next()) {
                    int idBahan = rsAddon.getInt("id_bahan_terkait");
                    double qtyPakai = rsAddon.getDouble("jumlah_pakai_bahan");
                    
                    // Jika id_bahan_terkait tidak NULL (artinya addon ini mengurangi stok fisik)
                    if (idBahan != 0) {
                        reduceStock(psUpdate, psCek, idCabang, idBahan, qtyPakai);
                    }
                }
            }
        }
    }
    
    // Helper untuk mengurangi stok
    private void reduceStock(PreparedStatement psUpdate, PreparedStatement psCek, int idCabang, int idBahan, double qty) throws SQLException {
        // Cek dulu apakah stok cukup? (Opsional, kalau mau maksa minus bisa di-skip)
        /*
        psCek.setInt(1, idCabang);
        psCek.setInt(2, idBahan);
        ResultSet rs = psCek.executeQuery();
        if (rs.next()) {
            double currentStock = rs.getDouble("stok_sistem");
            if (currentStock < qty) {
                throw new SQLException("Stok Bahan ID " + idBahan + " Tidak Cukup!");
            }
        }
        */

        // Lakukan Update
        psUpdate.setDouble(1, qty);
        psUpdate.setInt(2, idCabang);
        psUpdate.setInt(3, idBahan);
        psUpdate.executeUpdate();
    }
    
    // Helper Mapping Ukuran GUI ke Database ENUM
    private String mapSizeToDB(String detailString) {
        // Detail string format: "Regular, Normal, Normal" atau "Large (+5k), ..."
        String lower = detailString.toLowerCase();
        if (lower.contains("large")) return "large";
        if (lower.contains("1 liter")) return "1liter";
        return "regular"; // Default
    }
}