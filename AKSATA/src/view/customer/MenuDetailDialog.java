package view.customer;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MenuDetailDialog extends JDialog {

    // Data yang akan diambil oleh Dashboard
    public boolean isConfirmed = false;
    public String selectedSize = "Regular";
    public String selectedSugar = "Normal";
    public String selectedIce = "Normal";
    public String selectedNote = "";
    public boolean isTumbler = false;
    public double finalPrice = 0;

    // Komponen Internal
    private double basePrice;
    private JLabel lblPrice;
    private JComboBox<String> cmbSize, cmbSugar, cmbIce;
    private JCheckBox chkTumbler;
    private JTextArea txtCatatan;

    public MenuDetailDialog(Frame parent, String name, double price, String desc) {
        super(parent, "Detail Pesanan: " + name, true);
        this.basePrice = price;
        this.finalPrice = price;

        setSize(450, 650); // Ukuran fix yang proporsional
        setResizable(false); // Agar tidak bisa diubah-ubah user
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // --- 1. PANEL ATAS (Judul & Deskripsi) ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        topPanel.setBackground(Color.WHITE);
        
        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("Serif", Font.BOLD, 22));
        lblName.setForeground(new Color(78, 52, 46));
        lblName.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextArea txtDesc = new JTextArea(desc);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setLineWrap(true);
        txtDesc.setEditable(false);
        txtDesc.setFont(new Font("SansSerif", Font.ITALIC, 12));
        txtDesc.setForeground(Color.GRAY);
        txtDesc.setBackground(null); // Transparan
        txtDesc.setBorder(new EmptyBorder(5, 0, 10, 0));
        txtDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        topPanel.add(lblName);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(txtDesc);

        // --- 2. PANEL TENGAH (Form Input dengan GridBagLayout) ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        // formPanel.setBackground(new Color(250, 250, 250)); // Warna background form
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Jarak antar komponen
        
        // Baris 1: Label Ukuran & Gula
        gbc.gridy = 0;
        gbc.gridx = 0; formPanel.add(new JLabel("Ukuran:"), gbc);
        gbc.gridx = 1; formPanel.add(new JLabel("Level Gula:"), gbc);
        
        // Baris 2: Input Ukuran & Gula
        cmbSize = new JComboBox<>(new String[]{"Regular", "Large (+5k)", "1 Liter (+40k)"});
        cmbSugar = new JComboBox<>(new String[]{"Normal", "Less Sugar", "No Sugar"});
        
        gbc.gridy = 1;
        gbc.gridx = 0; formPanel.add(cmbSize, gbc);
        gbc.gridx = 1; formPanel.add(cmbSugar, gbc);
        
        // Baris 3: Label Es & Wadah
        gbc.gridy = 2;
        gbc.gridx = 0; formPanel.add(new JLabel("Level Es:"), gbc);
        gbc.gridx = 1; formPanel.add(new JLabel("Wadah:"), gbc); // Kosong atau label wadah
        
        // Baris 4: Input Es & Wadah
        cmbIce = new JComboBox<>(new String[]{"Normal", "Less Ice", "No Ice"});
        chkTumbler = new JCheckBox("Bawa Tumbler (-1k)");
        
        gbc.gridy = 3;
        gbc.gridx = 0; formPanel.add(cmbIce, gbc);
        gbc.gridx = 1; formPanel.add(chkTumbler, gbc);
        
        // Baris 5: Label Catatan (Full Width)
        gbc.gridy = 4;
        gbc.gridx = 0; 
        gbc.gridwidth = 2; // Gabung 2 kolom
        formPanel.add(new JLabel("Catatan Tambahan:"), gbc);
        
        // Baris 6: Input Catatan
        txtCatatan = new JTextArea(3, 20);
        txtCatatan.setLineWrap(true);
        JScrollPane scrollCatatan = new JScrollPane(txtCatatan);
        
        gbc.gridy = 5;
        gbc.ipady = 40; // Tinggi ekstra untuk text area
        formPanel.add(scrollCatatan, gbc);

        // Reset gbc
        gbc.gridwidth = 1;
        gbc.ipady = 0;

        // Logic Update Harga
        cmbSize.addActionListener(e -> updateLogic());
        chkTumbler.addActionListener(e -> updateLogic());

        // --- 3. PANEL BAWAH (Total & Tombol) ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(15, 20, 20, 20));
        bottomPanel.setBackground(new Color(245, 245, 245)); // Footer sedikit abu

        lblPrice = new JLabel("Total: Rp " + (int)basePrice, SwingConstants.CENTER);
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPrice.setForeground(new Color(78, 52, 46));
        lblPrice.setBorder(new EmptyBorder(0, 0, 15, 0));

        JButton btnAdd = new JButton("Tambah Pesanan");
        btnAdd.setBackground(new Color(78, 52, 46));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setPreferredSize(new Dimension(100, 40));
        
        btnAdd.addActionListener(e -> {
            isConfirmed = true;
            selectedNote = txtCatatan.getText();
            dispose();
        });

        JButton btnCancel = new JButton("Batal");
        btnCancel.setPreferredSize(new Dimension(100, 40));
        btnCancel.addActionListener(e -> dispose());

        JPanel btnGroup = new JPanel(new GridLayout(1, 2, 10, 0));
        btnGroup.setOpaque(false); // Transparan agar warna bottomPanel terlihat
        btnGroup.add(btnCancel);
        btnGroup.add(btnAdd);

        bottomPanel.add(lblPrice, BorderLayout.NORTH);
        bottomPanel.add(btnGroup, BorderLayout.CENTER);

        // --- GABUNGKAN SEMUA ---
        add(topPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateLogic() {
        double hitung = basePrice;
        String size = (String) cmbSize.getSelectedItem();

        if (size.contains("Large")) hitung += 5000;
        else if (size.contains("1 Liter")) hitung += 40000;

        if (chkTumbler.isSelected()) hitung -= 1000;

        // Logic 1 Liter No Ice
        if (size.contains("1 Liter")) {
            cmbIce.setSelectedItem("No Ice");
            cmbIce.setEnabled(false);
        } else {
            cmbIce.setEnabled(true);
        }

        finalPrice = hitung;
        selectedSize = size.split(" ")[0]; // Ambil kata pertama
        selectedSugar = (String) cmbSugar.getSelectedItem();
        selectedIce = (String) cmbIce.getSelectedItem();
        isTumbler = chkTumbler.isSelected();

        lblPrice.setText("Total: Rp " + (int)finalPrice);
    }
}