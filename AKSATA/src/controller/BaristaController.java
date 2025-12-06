package controller;

import config.DatabaseConnection;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class BaristaController {
    
    Connection conn = DatabaseConnection.getConnection();
    int ID_CABANG = 1; // HARDCODE: Asumsi Barista ini ada di Cabang 1 (Jakarta)

    // ==========================================
    // LOGIC TAB 1: PESANAN (Incoming Orders)
    // ==========================================
    public void loadPesananTable(DefaultTableModel model) {
        model.setRowCount(0); // Bersihkan tabel
        // Ambil transaksi yang belum selesai/batal
        String sql = "SELECT t.id_transaksi, m.nama_menu, dt.qty, dt.ukuran, dt.sugar_level, t.status_pesanan " +
                     "FROM transaksi t " +
                     "JOIN detail_transaksi dt ON t.id_transaksi = dt.id_transaksi " +
                     "JOIN menu m ON dt.id_menu = m.id_menu " +
                     "WHERE t.id_cabang = ? AND t.status_pesanan IN ('pending', 'diproses') " +
                     "ORDER BY t.tanggal ASC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ID_CABANG);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector row = new Vector();
                row.add(rs.getString("id_transaksi"));
                row.add(rs.getString("nama_menu"));
                row.add(rs.getInt("qty"));
                // Gabung detail supaya ringkas
                String detail = rs.getString("ukuran") + ", " + rs.getString("sugar_level");
                row.add(detail);
                row.add(rs.getString("status_pesanan"));
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStatusPesanan(String idTransaksi, String statusBaru) {
        String sql = "UPDATE transaksi SET status_pesanan = ? WHERE id_transaksi = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statusBaru);
            ps.setString(2, idTransaksi);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==========================================
    // LOGIC TAB 2: CLOSING (Stock Opname)
    // ==========================================
    // Sesuai rumus: Stok Sistem diambil dari Database (Running Balance)
    public void loadClosingTable(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT mb.id_bahan, mb.nama_bahan, mb.satuan, sc.stok_sistem " +
                     "FROM stok_cabang sc " +
                     "JOIN master_bahan mb ON sc.id_bahan = mb.id_bahan " +
                     "WHERE sc.id_cabang = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ID_CABANG);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector row = new Vector();
                row.add(rs.getInt("id_bahan"));       // Hidden Column (ID)
                row.add(rs.getString("nama_bahan"));
                row.add(rs.getDouble("stok_sistem") + " " + rs.getString("satuan"));
                row.add(0.0); // Stok Fisik (Default 0, nanti diisi user)
                row.add(0.0); // Selisih (Hitung otomatis)
                row.add("-"); // Alasan
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void submitStockOpname(int idBahan, double stokSistem, double stokFisik, String alasan) {
        // 1. Simpan ke Log
        String sqlLog = "INSERT INTO log_stok_opname (tanggal, id_cabang, id_bahan, stok_sistem, stok_fisik, selisih, keterangan) " +
                        "VALUES (CURDATE(), ?, ?, ?, ?, ?, ?)";
        // 2. Update Stok Real di database agar sesuai fisik untuk besok
        String sqlUpdate = "UPDATE stok_cabang SET stok_sistem = ? WHERE id_cabang = ? AND id_bahan = ?";
        
        try {
            // Insert Log
            PreparedStatement psLog = conn.prepareStatement(sqlLog);
            double selisih = stokFisik - stokSistem;
            psLog.setInt(1, ID_CABANG);
            psLog.setInt(2, idBahan);
            psLog.setDouble(3, stokSistem);
            psLog.setDouble(4, stokFisik);
            psLog.setDouble(5, selisih);
            psLog.setString(6, alasan);
            psLog.executeUpdate();

            // Update Master Stok (Reset ke fisik)
            PreparedStatement psUp = conn.prepareStatement(sqlUpdate);
            psUp.setDouble(1, stokFisik);
            psUp.setInt(2, ID_CABANG);
            psUp.setInt(3, idBahan);
            psUp.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==========================================
    // LOGIC TAB 3: RESTOCK (Barang Masuk)
    // ==========================================
    // Mencari ID Bahan berdasarkan Nama yang diketik di TextField
    public int getIdBahanByName(String namaBahan) {
        String sql = "SELECT id_bahan FROM master_bahan WHERE nama_bahan LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + namaBahan + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id_bahan");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Not found
    }

    public boolean submitRestock(int idBahan, double jumlah) {
        // Tambah Stok
        String sql = "UPDATE stok_cabang SET stok_sistem = stok_sistem + ? WHERE id_cabang = ? AND id_bahan = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, jumlah);
            ps.setInt(2, ID_CABANG);
            ps.setInt(3, idBahan);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}