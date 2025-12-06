package app;

import config.DatabaseConnection;
import controller.OrderController;
import model.CartItem;
import model.FlavorProfile;
import model.Menu;
import java.sql.Connection;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== SIMULASI APLIKASI AKSATA ===");
        
        // 1. Cek Koneksi (Pastikan di DatabaseConnection.java url-nya: jdbc:mysql://localhost:3306/db_aksata)
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return;

        // 2. Setup Objek Dummy (DATA HARUS COCOK DENGAN DB)
        FlavorProfile profileKopi = new FlavorProfile(5, 7, 6, 4);
        
        // Constructor Baru: ID, Nama, Kategori, Deskripsi, Nama Petani, Profile
        Menu menuKopi = new Menu(1, 
                "Kopi Susu Aksata", 
                "coffee", 
                "Kopi susu creamy gula aren", 
                "Pak Asep Ciwidey", // Ini masuk ke kolom nama_petani
                profileKopi);

        // 3. Simulasi Pelanggan Memesan
        OrderController orderController = new OrderController();
        CartItem pesanan1 = new CartItem(menuKopi, "regular", 2, false);
        
        System.out.println("Memesan: " + pesanan1.getMenu().getName());
        System.out.println("Origin/Petani: " + pesanan1.getMenu().getNamaPetani());

        orderController.addToCart(pesanan1);

        // 4. Proses Checkout
        System.out.println("\n--- Proses Checkout ---");
        // Pastikan ID Cabang 1 ada di database Anda
        orderController.checkout(1); 
    }
}