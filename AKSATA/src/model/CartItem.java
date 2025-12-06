package model;

public class CartItem {
    private Menu menu;
    private String size; // REGULAR, LARGE, 1LITER
    private int qty;
    private String sugarLevel; 
    private String iceLevel;
    private boolean isTumbler;

    // Constructor ini yang dicari oleh Main.java
    public CartItem(Menu menu, String size, int qty, boolean isTumbler) {
        this.menu = menu;
        this.size = size;
        this.qty = qty;
        this.isTumbler = isTumbler;
        validate(); // Cek aturan 1 Liter
    }

    // Logic: Jika size 1 Liter, otomatis No Ice
    private void validate() {
        if ("1LITER".equalsIgnoreCase(this.size)) {
            this.iceLevel = "NO";
        }
    }
    
    // Method Getters (PENTING AGAR TIDAK ERROR DI MAIN)
    public Menu getMenu() { 
        return menu; 
    }
    
    public int getQty() { 
        return qty; 
    }
    
    public String getSize() { 
        return size; 
    }
}