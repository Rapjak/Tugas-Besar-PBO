package controller;

import config.DatabaseConnection;
import java.sql.*;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.table.DefaultTableModel;

public class AdminController {
    Connection conn = DatabaseConnection.getConnection();

    // ================= DASHBOARD LOGIC =================
    public double getGlobalOmzet() {
        double total = 0;
        try {
            String sql = "SELECT SUM(total_bayar) FROM transaksi WHERE DATE(tanggal) = CURDATE()";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) total = rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return total;
    }

    public int getGlobalTransCount() {
        int total = 0;
        try {
            String sql = "SELECT COUNT(*) FROM transaksi WHERE DATE(tanggal) = CURDATE()";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) total = rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return total;
    }

    public void loadLowStockGlobal(DefaultListModel listModel) {
        listModel.clear();
        try {
            // Alert jika stok < 1000 (satuan bebas)
            String sql = "SELECT c.nama_cabang, mb.nama_bahan, sc.stok_sistem, mb.satuan " +
                         "FROM stok_cabang sc " +
                         "JOIN master_bahan mb ON sc.id_bahan = mb.id_bahan " +
                         "JOIN cabang c ON sc.id_cabang = c.id_cabang " +
                         "WHERE sc.stok_sistem < 1000"; 
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()) {
                String alert = rs.getString("nama_cabang") + ": " + 
                               rs.getString("nama_bahan") + " (" + 
                               rs.getString("stok_sistem") + " " + rs.getString("satuan") + ")";
                listModel.addElement(alert);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public Vector<String> getAllCabangNames() {
        Vector<String> list = new Vector<>();
        try {
            String sql = "SELECT nama_cabang FROM cabang";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()) list.add(rs.getString("nama_cabang"));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public int getCabangID(String namaCabang) {
        try {
            String sql = "SELECT id_cabang FROM cabang WHERE nama_cabang = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, namaCabang);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    // ================= DETAIL CABANG LOGIC =================
    public void loadTopSelling(int idCabang, DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT m.nama_menu, SUM(dt.qty) as terjual " +
                     "FROM detail_transaksi dt " +
                     "JOIN transaksi t ON dt.id_transaksi = t.id_transaksi " +
                     "JOIN menu m ON dt.id_menu = m.id_menu " +
                     "WHERE t.id_cabang = ? " +
                     "GROUP BY m.nama_menu ORDER BY terjual DESC LIMIT 5";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idCabang);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                model.addRow(new Object[]{rs.getString("nama_menu") + " (" + rs.getInt("terjual") + " Sold)"});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void loadStokByCabang(int idCabang, DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT mb.nama_bahan, sc.stok_sistem, mb.satuan " +
                     "FROM stok_cabang sc JOIN master_bahan mb ON sc.id_bahan = mb.id_bahan " +
                     "WHERE sc.id_cabang = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idCabang);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                double stok = rs.getDouble("stok_sistem");
                String status = (stok < 1000) ? "LOW STOCK" : "Aman";
                model.addRow(new Object[]{rs.getString("nama_bahan"), stok, rs.getString("satuan"), status});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void loadVarianceReport(int idCabang, DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT l.tanggal, mb.nama_bahan, l.stok_sistem, l.stok_fisik, l.selisih, l.keterangan, u.username " +
                     "FROM log_stok_opname l " +
                     "JOIN master_bahan mb ON l.id_bahan = mb.id_bahan " +
                     "LEFT JOIN users u ON l.id_user = u.id_user " +
                     "WHERE l.id_cabang = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idCabang);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getDate("tanggal"), rs.getString("nama_bahan"), rs.getDouble("stok_sistem"),
                    rs.getDouble("stok_fisik"), rs.getDouble("selisih"), rs.getString("keterangan"), rs.getString("username")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void loadMenuByCabang(int idCabang, DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT m.id_menu, m.nama_menu, m.kategori, mh.ukuran, mh.harga_jual, mc.is_available " +
                     "FROM menu_cabang mc " +
                     "JOIN menu m ON mc.id_menu = m.id_menu " +
                     "JOIN menu_harga mh ON m.id_menu = mh.id_menu " +
                     "WHERE mc.id_cabang = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idCabang);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_menu"), rs.getString("nama_menu"), rs.getString("kategori"),
                    rs.getString("ukuran"), rs.getDouble("harga_jual"), rs.getBoolean("is_available")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void loadDiskonByCabang(int idCabang, DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT d.id_diskon, d.nama_promo, CONCAT(d.tipe, ' ', d.nilai) as nilai, d.min_belanja, " +
                     "CONCAT(d.start_date, ' s/d ', d.end_date) as periode, dc.is_active " +
                     "FROM diskon_cabang dc " +
                     "JOIN diskon d ON dc.id_diskon = d.id_diskon " +
                     "WHERE dc.id_cabang = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idCabang);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_diskon"), rs.getString("nama_promo"), rs.getString("nilai"),
                    rs.getDouble("min_belanja"), rs.getString("periode"), rs.getBoolean("is_active")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void loadLogTransaksi(int idCabang, DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT t.tanggal, t.id_transaksi, COALESCE(u.username, 'Kiosk') as kasir, t.total_bayar, t.metode_bayar " +
                     "FROM transaksi t LEFT JOIN users u ON t.id_user = u.id_user " +
                     "WHERE t.id_cabang = ? ORDER BY t.tanggal DESC LIMIT 50";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idCabang);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getTimestamp("tanggal"), rs.getString("id_transaksi"), rs.getString("kasir"),
                    rs.getDouble("total_bayar"), rs.getString("metode_bayar")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void updateMenuStatus(int idCabang, int idMenu, boolean status) {
        try {
            String sql = "UPDATE menu_cabang SET is_available = ? WHERE id_cabang = ? AND id_menu = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBoolean(1, status);
            ps.setInt(2, idCabang);
            ps.setInt(3, idMenu);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ================= COMPARISON LOGIC =================
    public double getOmzetCabangHarian(int idCabang) {
        double total = 0;
        try {
            String sql = "SELECT SUM(total_bayar) FROM transaksi WHERE id_cabang = ? AND DATE(tanggal) = CURDATE()";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idCabang);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) total = rs.getDouble(1);
        } catch (Exception e) { e.printStackTrace(); }
        return total;
    }

    // ================= USER MANAGEMENT LOGIC =================
    public void loadUsers(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT u.id_user, u.username, u.role, c.nama_cabang FROM users u " +
                     "LEFT JOIN cabang c ON u.id_cabang = c.id_cabang";
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()) {
                model.addRow(new Object[]{rs.getInt("id_user"), rs.getString("username"), rs.getString("role"), rs.getString("nama_cabang")});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    public void loadUserActivityLog(int idUser, DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT tanggal, 'Melakukan Transaksi' as aktivitas, CONCAT('Total: ', total_bayar) as keterangan FROM transaksi WHERE id_user = ? " +
                     "UNION ALL " +
                     "SELECT tanggal, 'Input Stok Opname' as aktivitas, keterangan FROM log_stok_opname WHERE id_user = ? " +
                     "ORDER BY tanggal DESC LIMIT 50";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idUser);
            ps.setInt(2, idUser);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                model.addRow(new Object[]{rs.getTimestamp("tanggal"), rs.getString("aktivitas"), rs.getString("keterangan")});
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void addUser(String user, String pass, String role, int idCabang) {
        try {
            String sql = "INSERT INTO users (username, password, role, id_cabang) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user); ps.setString(2, pass); ps.setString(3, role);
            if(idCabang == 0) ps.setNull(4, java.sql.Types.INTEGER); else ps.setInt(4, idCabang);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    public void deleteUser(int idUser) {
        try {
            String sql = "DELETE FROM users WHERE id_user = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idUser);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    public void resetUserPassword(int idUser, String newPassword) {
        try {
            String sql = "UPDATE users SET password = ? WHERE id_user = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newPassword);
            ps.setInt(2, idUser);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}