package view;

import controller.OrderController;
import controller.StockController; // Asumsi ada untuk dashboard
import model.CartItem;
import model.FlavorProfile;
import model.Menu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    // Controller
    private OrderController orderController;
    
    // Components
    private JTabbedPane tabbedPane;
    private JPanel clientPanel, baristaPanel, adminPanel;
    
    // Client Page Components
    private DefaultListModel<String> cartListModel;
    private JList<String> cartList;
    private JLabel totalLabel;

    public MainFrame() {
        // Init Controller
        orderController = new OrderController();

        setTitle("Aksata Coffee App System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Setup Tabs
        tabbedPane = new JTabbedPane();
        
        // 1. Init Panels
        initClientPanel();
        initBaristaPanel();
        initAdminPanel();

        // 2. Add Tabs (Sesuai Rancangan PDF: Client, Barista, Admin)
        tabbedPane.addTab("Client Order", new ImageIcon(), clientPanel, "Halaman Pemesanan");
        tabbedPane.addTab("Barista Room", new ImageIcon(), baristaPanel, "Operational Flow");
        tabbedPane.addTab("Manager/Admin", new ImageIcon(), adminPanel, "Monitoring & Stock");

        add(tabbedPane);
    }

    // ==========================================
    // 1. CLIENT PAGE (Sesuai PDF Hal 2 & 3)
    // ==========================================
    private void initClientPanel() {
        clientPanel = new JPanel(new BorderLayout());

        // --- Bagian Kiri: Menu Gallery ---
        JPanel menuPanel = new JPanel(new GridLayout(0, 3, 10, 10)); // Grid 3 Kolom
        JScrollPane scrollMenu = new JScrollPane(menuPanel);
        scrollMenu.setBorder(BorderFactory.createTitledBorder("Menu Gallery & Flavor Profile"));

        // MOCK DATA MENU (Karena belum connect DB penuh untuk GUI)
        // Sesuai Constructor Menu kamu: id, name, category, desc, petani, profile
        FlavorProfile profileArabica = new FlavorProfile(8, 6, 7, 5); // Acidity, Sweetness, Body, Bitterness 
        Menu menu1 = new Menu(1, "Arabica V60", "coffee", "Seduhan manual.", "Petani Ciwidey", profileArabica);
        
        FlavorProfile profileLatte = new FlavorProfile(4, 8, 6, 4);
        Menu menu2 = new Menu(2, "Ice Latte", "coffee", "Espresso + Fresh Milk.", "House Blend", profileLatte);

        // Tambahkan Card Menu ke Panel
        menuPanel.add(createMenuCard(menu1));
        menuPanel.add(createMenuCard(menu2));

        // --- Bagian Kanan: Cart & Checkout (PDF: Floating Cart) ---
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setPreferredSize(new Dimension(300, 0));
        cartPanel.setBorder(BorderFactory.createTitledBorder("Keranjang Belanja"));

        cartListModel = new DefaultListModel<>();
        cartList = new JList<>(cartListModel);
        
        JButton btnCheckout = new JButton("Checkout / Bayar");
        btnCheckout.setBackground(new Color(46, 204, 113)); // Hijau
        btnCheckout.setForeground(Color.WHITE);
        
        btnCheckout.addActionListener(e -> {
            // Panggil Controller Checkout [cite: 329]
            orderController.checkout(1); // Hardcode ID Cabang 1
            cartListModel.clear();
            JOptionPane.showMessageDialog(this, "Transaksi Berhasil! Stok Terupdate.");
        });

        cartPanel.add(new JScrollPane(cartList), BorderLayout.CENTER);
        cartPanel.add(btnCheckout, BorderLayout.SOUTH);

        clientPanel.add(scrollMenu, BorderLayout.CENTER);
        clientPanel.add(cartPanel, BorderLayout.EAST);
    }

    // Helper untuk membuat Card Menu (Gambar, Info Rasa, Tombol Add)
    private JPanel createMenuCard(Menu menu) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        card.setBackground(Color.WHITE);

        JLabel lblName = new JLabel("<html><b>" + menu.getName() + "</b></html>");
        JLabel lblPetani = new JLabel("Origin: " + menu.getNamaPetani()); // [cite: 61]
        
        // Input Size (PDF Hal 2: Pilih Ukuran)
        String[] sizes = {"REGULAR", "LARGE", "1LITER"};
        JComboBox<String> cbSize = new JComboBox<>(sizes);
        
        // Input Sugar (PDF Hal 3: Kustomisasi Gula)
        String[] sugars = {"NORMAL", "LESS", "NO"};
        JComboBox<String> cbSugar = new JComboBox<>(sugars);

        // Input Tumbler (PDF Hal 2: Pilih Wadah)
        JCheckBox chkTumbler = new JCheckBox("Bawa Tumbler Sendiri?"); // [cite: 65]

        JButton btnAdd = new JButton("Add +");
        btnAdd.addActionListener(e -> {
            String size = (String) cbSize.getSelectedItem();
            boolean isTumbler = chkTumbler.isSelected();
            
            // Validasi 1 Liter (PDF Hal 3: Jika 1 Liter, No Ice)
            if(size.equals("1LITER")) {
                // Logic ini sudah ada di CartItem.java kamu, tapi kita info di GUI
                JOptionPane.showMessageDialog(this, "Ukuran 1 Liter otomatis No Ice.");
            }

            // Masukkan ke Keranjang via Controller
            CartItem item = new CartItem(menu, size, 1, isTumbler);
            orderController.addToCart(item); // [cite: 328]
            
            // Update UI Keranjang
            cartListModel.addElement(menu.getName() + " (" + size + ") - " + (isTumbler ? "Tumbler" : "Cup"));
        });

        card.add(lblName);
        card.add(lblPetani);
        card.add(new JLabel("Size:"));
        card.add(cbSize);
        card.add(new JLabel("Sugar:"));
        card.add(cbSugar);
        card.add(chkTumbler);
        card.add(Box.createVerticalStrut(10));
        card.add(btnAdd);

        return card;
    }

    // ==========================================
    // 2. BARISTA PAGE (Sesuai PDF Hal 1 & 2)
    // ==========================================
    private void initBaristaPanel() {
        baristaPanel = new JPanel(new BorderLayout());
        
        // Tabel Pesanan Masuk (PDF: Dashboard Pesanan Masuk) [cite: 30]
        String[] columnNames = {"No Order", "Menu", "Qty", "Status"};
        Object[][] data = {
            {"TRX-001", "Ice Latte", "2", "Pending"}, // Dummy Data
            {"TRX-002", "V60", "1", "Sedang Dibuat"}
        };
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable tableOrders = new JTable(model);
        
        JPanel actionPanel = new JPanel();
        JButton btnProcess = new JButton("Proses Pesanan");
        JButton btnReady = new JButton("Siap Disajikan");
        JButton btnStockIn = new JButton("Input Stok Masuk"); // [cite: 36]
        
        actionPanel.add(btnProcess);
        actionPanel.add(btnReady);
        actionPanel.add(btnStockIn);

        baristaPanel.add(new JScrollPane(tableOrders), BorderLayout.CENTER);
        baristaPanel.add(actionPanel, BorderLayout.SOUTH);
    }

    // ==========================================
    // 3. ADMIN PAGE (Sesuai PDF Hal 1)
    // ==========================================
    private void initAdminPanel() {
        adminPanel = new JPanel(null); // Absolute Layout (Manual positioning)

        // Dashboard Overview [cite: 2]
        JLabel lblTitle = new JLabel("Dashboard Manager");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setBounds(20, 20, 300, 30);
        adminPanel.add(lblTitle);

        // Kartu Omzet
        JPanel pnlOmzet = new JPanel();
        pnlOmzet.setBackground(Color.ORANGE);
        pnlOmzet.setBounds(20, 70, 200, 100);
        pnlOmzet.add(new JLabel("Total Omzet Hari Ini"));
        pnlOmzet.add(new JLabel("Rp 1.500.000")); // [cite: 4]
        adminPanel.add(pnlOmzet);

        // Low Stock Alert [cite: 5]
        JPanel pnlAlert = new JPanel();
        pnlAlert.setBackground(Color.RED);
        pnlAlert.setBounds(240, 70, 200, 100);
        JLabel lblAlert = new JLabel("<html><center>Low Stock Alert!<br>Susu < 2L</center></html>");
        lblAlert.setForeground(Color.WHITE);
        pnlAlert.add(lblAlert);
        adminPanel.add(pnlAlert);
        
        // Grafik Penjualan (Placeholder)
        JLabel lblChart = new JLabel("[Grafik Penjualan Mingguan akan tampil di sini]"); // [cite: 6]
        lblChart.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        lblChart.setBounds(20, 200, 420, 200);
        adminPanel.add(lblChart);
    }

    // Main Method untuk Menjalankan GUI
    public static void main(String[] args) {
        // Gunakan Look and Feel bawaan sistem agar terlihat modern
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}