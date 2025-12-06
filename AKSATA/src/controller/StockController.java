package controller;

import config.DatabaseConnection;
import model.CartItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class StockController {
    
    // Mengurangi stok otomatis berdasarkan resep saat transaksi [cite: 334]
    public void deductStockByRecipe(List<CartItem> items, int idCabang) {
        Connection conn = DatabaseConnection.getConnection();
        
        try {
            for (CartItem item : items) {
                // 1. Ambil Resep: Berapa gram bahan dipakai untuk menu ini?
                String queryResep = "SELECT id_bahan, jumlah_pakai FROM resep WHERE id_menu = ? AND ukuran = ?";
                PreparedStatement psResep = conn.prepareStatement(queryResep);
                psResep.setInt(1, item.getMenu().getId());
                psResep.setString(2, item.getSize()); // Ukuran berpengaruh ke resep
                ResultSet rs = psResep.executeQuery();
                
                while (rs.next()) {
                    int idBahan = rs.getInt("id_bahan");
                    double jumlahPakai = rs.getDouble("jumlah_pakai");
                    double totalKurang = jumlahPakai * item.getQty(); 
                    
                    // 2. Update Stok di Database
                    String updateStok = "UPDATE stok_cabang SET stok_sistem = stok_sistem - ? " +
                                        "WHERE id_cabang = ? AND id_bahan = ?";
                    PreparedStatement psUpdate = conn.prepareStatement(updateStok);
                    psUpdate.setDouble(1, totalKurang);
                    psUpdate.setInt(2, idCabang);
                    psUpdate.setInt(3, idBahan);
                    psUpdate.executeUpdate();
                    
                    System.out.println("Log: Stok ID " + idBahan + " berkurang " + totalKurang);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}