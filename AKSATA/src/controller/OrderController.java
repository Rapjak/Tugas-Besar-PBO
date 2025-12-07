package controller;

import config.DatabaseConnection;
import model.CartItem;
import model.AddOn;
import java.sql.*;
import java.util.List;

public class OrderController {
    
    private StockController stockController = new StockController();

    // PERHATIKAN: Parameter 'metodeBayar' sudah ditambahkan di sini
    public boolean processCheckout(int idCabang, String customerName, String tipePesanan, String metodeBayar, double totalBayar, List<CartItem> cartList) {
        
        Connection conn = DatabaseConnection.getConnection();
        
        try {
            conn.setAutoCommit(false); // Mulai Transaksi

            // --- 1. INSERT HEADER TRANSAKSI ---
            String trxId = "TRX-" + System.currentTimeMillis();
            // Query sudah menyertakan kolom metode_bayar
            String sqlHead = "INSERT INTO transaksi (id_transaksi, id_cabang, nama_customer, tipe_pesanan, total_bayar, status_pesanan, metode_bayar) VALUES (?, ?, ?, ?, ?, 'pending', ?)";
            
            PreparedStatement psHead = conn.prepareStatement(sqlHead);
            psHead.setString(1, trxId);
            psHead.setInt(2, idCabang);
            psHead.setString(3, customerName);
            psHead.setString(4, tipePesanan.equals("Dine In") ? "dine_in" : "take_away");
            psHead.setDouble(5, totalBayar);
            psHead.setString(6, metodeBayar); // Variabel ini sekarang dikenali karena sudah ada di parameter
            psHead.executeUpdate();

            // --- 2. INSERT DETAIL TRANSAKSI ---
            String sqlDet = "INSERT INTO detail_transaksi " +
                            "(id_transaksi, id_menu, ukuran, qty, sugar_level, ice_level, harga_satuan, subtotal_item, catatan) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement psDet = conn.prepareStatement(sqlDet, Statement.RETURN_GENERATED_KEYS);
            
            // Query untuk Add-ons
            String sqlAddon = "INSERT INTO detail_transaksi_addons (id_detail, id_addon) VALUES (?, ?)";
            PreparedStatement psAddon = conn.prepareStatement(sqlAddon);

            for (CartItem item : cartList) {
                // A. Parsing Detail String
                String[] parts = item.getDetail().split(", ");
                String size = parts.length > 0 ? parts[0].split(" ")[0].toLowerCase() : "regular"; 
                String sugar = parts.length > 1 ? parts[1].toLowerCase().replace(" sugar", "") : "normal"; 
                String ice = parts.length > 2 ? parts[2].toLowerCase().replace(" ice", "") : "normal";
                
                if (size.contains("1")) size = "1liter"; 
                if (sugar.contains("no")) sugar = "no";
                if (ice.contains("no")) ice = "no";

                // B. Set Parameter SQL Detail
                psDet.setString(1, trxId);
                psDet.setInt(2, item.getIdMenu());
                psDet.setString(3, size); 
                psDet.setInt(4, item.getQty()); 
                psDet.setString(5, sugar); 
                psDet.setString(6, ice); 
                psDet.setDouble(7, item.getHarga()); 
                psDet.setDouble(8, item.getHarga() * item.getQty()); 
                psDet.setString(9, item.getNote()); 
                
                psDet.executeUpdate(); 

                // C. Ambil ID Detail Baru 
                ResultSet rsKey = psDet.getGeneratedKeys();
                int idDetailBaru = -1;
                if (rsKey.next()) {
                    idDetailBaru = rsKey.getInt(1);
                }

                // D. Simpan Add-ons
                if (idDetailBaru != -1 && !item.getSelectedAddOns().isEmpty()) {
                    for (AddOn ad : item.getSelectedAddOns()) {
                        psAddon.setInt(1, idDetailBaru);
                        psAddon.setInt(2, ad.getId());
                        psAddon.addBatch();
                    }
                }
            }
            
            psAddon.executeBatch(); 
            
            // --- 3. POTONG STOK ---
            stockController.deductStock(conn, cartList, idCabang);

            conn.commit(); 
            System.out.println("Transaksi Berhasil: " + trxId);
            return true;

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) {} 
            System.err.println("GAGAL CHECKOUT KARENA:");
            e.printStackTrace(); 
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }
}