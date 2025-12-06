package controller;

import config.DatabaseConnection;
import model.CartItem;
import java.sql.*;
import java.util.List;

public class OrderController {
    
    private StockController stockController = new StockController();

    // Method Checkout Full Logic
    public boolean processCheckout(int idCabang, String customerName, String tipePesanan, 
                                   double totalBayar, List<CartItem> cartList) {
        
        Connection conn = DatabaseConnection.getConnection();
        
        try {
            conn.setAutoCommit(false); // Mulai Transaksi (Safety)

            // 1. Generate ID Unik
            String trxId = "TRX-" + System.currentTimeMillis();

            // 2. Insert Header Transaksi
            String sqlHead = "INSERT INTO transaksi (id_transaksi, id_cabang, nama_customer, tipe_pesanan, total_bayar, status_pesanan) VALUES (?, ?, ?, ?, ?, 'pending')";
            PreparedStatement psHead = conn.prepareStatement(sqlHead);
            psHead.setString(1, trxId);
            psHead.setInt(2, idCabang);
            psHead.setString(3, customerName);
            psHead.setString(4, tipePesanan.equals("Dine In") ? "dine_in" : "take_away");
            psHead.setDouble(5, totalBayar);
            psHead.executeUpdate();

            // 3. Insert Detail & Potong Stok
            String sqlDet = "INSERT INTO detail_transaksi (id_transaksi, id_menu, catatan, subtotal_item) VALUES (?, ?, ?, ?)";
            PreparedStatement psDet = conn.prepareStatement(sqlDet);

            for (CartItem item : cartList) {
                // Masukkan ke Tabel Detail
                psDet.setString(1, trxId);
                psDet.setInt(2, item.getIdMenu());
                psDet.setString(3, item.getDetail() + " " + item.getNote());
                psDet.setDouble(4, item.getHarga());
                psDet.addBatch();
            }
            psDet.executeBatch();
            
            // 4. Panggil StockController (Kirim Connection yang sama agar 1 paket transaksi)
            stockController.deductStock(conn, cartList, idCabang);

            conn.commit(); // Simpan Permanen
            return true;

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) {} 
            e.printStackTrace();
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }
}