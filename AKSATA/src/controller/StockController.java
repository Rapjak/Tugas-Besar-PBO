package controller;

import model.CartItem;
import java.sql.*;
import java.util.List;

public class StockController {

    // Method ini dipanggil oleh OrderController
    public void deductStock(Connection conn, List<CartItem> items, int idCabang) throws SQLException {
        
        String sqlResep = "SELECT id_bahan, jumlah_pakai FROM resep WHERE id_menu = ? AND ukuran = 'regular'"; 
        // Note: Sementara kita hardcode ukuran 'regular' untuk simplifikasi stok
        // Idealnya CartItem menyimpan 'rawSize' (Regular/Large) untuk query resep yg tepat.
        
        String sqlUpdate = "UPDATE stok_cabang SET stok_sistem = stok_sistem - ? WHERE id_cabang = ? AND id_bahan = ?";
        
        PreparedStatement psResep = conn.prepareStatement(sqlResep);
        PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);

        for (CartItem item : items) {
            // 1. Cek Resep
            psResep.setInt(1, item.getIdMenu());
            ResultSet rs = psResep.executeQuery();

            while(rs.next()) {
                int idBahan = rs.getInt("id_bahan");
                double pakai = rs.getDouble("jumlah_pakai");
                double totalKurang = pakai * item.getQty();

                // 2. Kurangi Stok
                psUpdate.setDouble(1, totalKurang);
                psUpdate.setInt(2, idCabang);
                psUpdate.setInt(3, idBahan);
                psUpdate.executeUpdate();
            }
        }
    }
}