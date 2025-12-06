package controller;

import model.CartItem;
import java.util.ArrayList;
import java.util.List;

public class OrderController {
    private List<CartItem> currentCart = new ArrayList<>();
    private StockController stockController = new StockController();
    
    public void addToCart(CartItem item) {
        currentCart.add(item);
    }
    
    public void checkout(int idCabang) {
        if (currentCart.isEmpty()) return;

        System.out.println("--- Memproses Checkout ---");
        
        // 1. (Nanti) Simpan data ke tabel 'transaksi' dan 'detail_transaksi'
        
        // 2. Kurangi Stok Otomatis via StockController [cite: 329]
        stockController.deductStockByRecipe(currentCart, idCabang);
        
        // 3. Reset Cart
        currentCart.clear();
        System.out.println("--- Transaksi Selesai & Stok Terupdate ---");
    }
}