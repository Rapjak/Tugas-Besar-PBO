package view.customer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.AddOn; // Import Model Baru

public class MenuDetailDialog extends JDialog {

    public boolean isConfirmed = false;
    public String selectedSize = "Regular";
    public String selectedSugar = "Normal";
    public String selectedIce = "Normal";
    public String selectedNote = "";
    public boolean isTumbler = false;
    public double finalPrice = 0;
    
    // TAMBAHAN: Untuk menyimpan hasil pilihan user
    public List<AddOn> selectedAddOns = new ArrayList<>(); 

    // Komponen Internal
    private double basePrice;
    private JLabel lblPrice;
    private JComboBox<String> cmbSize, cmbSugar, cmbIce;
    private JCheckBox chkTumbler;
    private JTextArea txtCatatan;
    
    // TAMBAHAN: List Checkbox untuk Addons
    private List<JCheckBox> addOnCheckBoxes = new ArrayList<>();
    private List<AddOn> availableAddOns; // Data dari DB

    public MenuDetailDialog(Frame parent, String name, double price, String desc, List<AddOn> addons) {
        super(parent, "Detail Pesanan: " + name, true);
        this.basePrice = price;
        this.finalPrice = price;
        this.availableAddOns = addons; // Terima data add-on

        setSize(500, 700); // Sedikit diperlebar
        setResizable(false);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // --- 1. PANEL ATAS (Sama seperti sebelumnya) ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        topPanel.setBackground(Color.WHITE);
        
        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("Serif", Font.BOLD, 22));
        lblName.setForeground(new Color(78, 52, 46));
        
        JTextArea txtDesc = new JTextArea(desc);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setLineWrap(true);
        txtDesc.setEditable(false);
        txtDesc.setFont(new Font("SansSerif", Font.ITALIC, 12));
        txtDesc.setForeground(Color.GRAY);
        txtDesc.setBackground(null);
        txtDesc.setBorder(new EmptyBorder(5, 0, 10, 0));
        
        topPanel.add(lblName);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(txtDesc);

        // --- 2. PANEL TENGAH (Form Scrollable) ---
        // Kita pakai ScrollPane untuk panel tengah karena add-on bisa banyak
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBorder(new EmptyBorder(10, 20, 10, 20));

        // A. Form Standard (Ukuran, Gula, Es)
        JPanel standardOptions = new JPanel(new GridLayout(4, 2, 10, 10));
        standardOptions.setMaximumSize(new Dimension(500, 150));
        
        cmbSize = new JComboBox<>(new String[]{"Regular", "Large (+5k)", "1 Liter (+40k)"});
        cmbSugar = new JComboBox<>(new String[]{"Normal", "Less Sugar", "No Sugar"});
        cmbIce = new JComboBox<>(new String[]{"Normal", "Less Ice", "No Ice"});
        chkTumbler = new JCheckBox("Bawa Tumbler (-1k)");

        standardOptions.add(new JLabel("Ukuran:"));
        standardOptions.add(new JLabel("Level Gula:"));
        standardOptions.add(cmbSize);
        standardOptions.add(cmbSugar);
        standardOptions.add(new JLabel("Level Es:"));
        standardOptions.add(new JLabel("Wadah:"));
        standardOptions.add(cmbIce);
        standardOptions.add(chkTumbler);
        
        mainContent.add(standardOptions);
        mainContent.add(Box.createVerticalStrut(15));
        mainContent.add(new JSeparator());
        mainContent.add(Box.createVerticalStrut(10));

        // B. ADD-ONS SECTION (Dinamis)
        JLabel lblAddon = new JLabel("Tambahan (Add-ons):");
        lblAddon.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainContent.add(lblAddon);
        
        JPanel addonContainer = new JPanel(new GridLayout(0, 2, 5, 5)); // 2 Kolom
        addonContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        if (availableAddOns != null) {
            for (AddOn ad : availableAddOns) {
                // Buat Checkbox: "Extra Shot (+5000)"
                String label = ad.getName() + " (+" + (int)ad.getPrice() + ")";
                JCheckBox chk = new JCheckBox(label);
                
                // Simpan objek AddOn di client property checkbox agar mudah diambil nanti
                chk.putClientProperty("data", ad);
                
                // Tambahkan action listener untuk update harga real-time
                chk.addActionListener(e -> updateLogic());
                
                addOnCheckBoxes.add(chk);
                addonContainer.add(chk);
            }
        }
        mainContent.add(addonContainer);
        
        mainContent.add(Box.createVerticalStrut(15));
        mainContent.add(new JSeparator());
        mainContent.add(Box.createVerticalStrut(10));

        // C. Catatan
        mainContent.add(new JLabel("Catatan Tambahan:"));
        txtCatatan = new JTextArea(3, 20);
        txtCatatan.setLineWrap(true);
        mainContent.add(new JScrollPane(txtCatatan));

        // Bungkus MainContent dengan ScrollPane agar aman jika addon banyak
        JScrollPane scrollForm = new JScrollPane(mainContent);
        scrollForm.setBorder(null);
        scrollForm.getVerticalScrollBar().setUnitIncrement(16);

        // Logic Update Harga Standard
        cmbSize.addActionListener(e -> updateLogic());
        chkTumbler.addActionListener(e -> updateLogic());

        // --- 3. PANEL BAWAH (Total & Tombol) ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(15, 20, 20, 20));
        bottomPanel.setBackground(new Color(245, 245, 245));

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
            // Simpan addons yang dipilih ke list
            selectedAddOns.clear();
            for (JCheckBox chk : addOnCheckBoxes) {
                if (chk.isSelected()) {
                    selectedAddOns.add((AddOn) chk.getClientProperty("data"));
                }
            }
            dispose();
        });

        JButton btnCancel = new JButton("Batal");
        btnCancel.setPreferredSize(new Dimension(100, 40));
        btnCancel.addActionListener(e -> dispose());

        JPanel btnGroup = new JPanel(new GridLayout(1, 2, 10, 0));
        btnGroup.setOpaque(false);
        btnGroup.add(btnCancel);
        btnGroup.add(btnAdd);

        bottomPanel.add(lblPrice, BorderLayout.NORTH);
        bottomPanel.add(btnGroup, BorderLayout.CENTER);

        // --- GABUNGKAN SEMUA ---
        add(topPanel, BorderLayout.NORTH);
        add(scrollForm, BorderLayout.CENTER); // Pakai ScrollPane di tengah
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateLogic() {
        double hitung = basePrice;
        
        // 1. Hitung Size
        String size = (String) cmbSize.getSelectedItem();
        if (size.contains("Large")) hitung += 5000;
        else if (size.contains("1 Liter")) hitung += 40000;

        // 2. Hitung Tumbler
        if (chkTumbler.isSelected()) hitung -= 1000;

        // 3. Logic 1 Liter No Ice
        if (size.contains("1 Liter")) {
            cmbIce.setSelectedItem("No Ice");
            cmbIce.setEnabled(false);
        } else {
            cmbIce.setEnabled(true);
        }
        
        // 4. Hitung Add-ons (LOOPING CHECKBOX)
        for (JCheckBox chk : addOnCheckBoxes) {
            if (chk.isSelected()) {
                AddOn ad = (AddOn) chk.getClientProperty("data");
                hitung += ad.getPrice();
            }
        }

        finalPrice = hitung;
        selectedSize = size.split(" ")[0];
        selectedSugar = (String) cmbSugar.getSelectedItem();
        selectedIce = (String) cmbIce.getSelectedItem();
        isTumbler = chkTumbler.isSelected();

        lblPrice.setText("Total: Rp " + (int)finalPrice);
    }
}