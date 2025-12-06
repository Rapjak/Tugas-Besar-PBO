package model;

public class CartItem {
    private int idMenu;
    private String namaMenu;
    private String detail;
    private double harga;
    private String note;
    private int qty;

    public CartItem(int idMenu, String namaMenu, String detail, double harga, String catatan) {
        this.idMenu = idMenu;
        this.namaMenu = namaMenu;
        this.detail = detail;
        this.harga = harga;
        this.note = catatan;
        this.qty = 1; // Default qty 1
    }

    // Getters
    public int getIdMenu() { return idMenu; }
    public String getNamaMenu() { return namaMenu; }
    public String getDetail() { return detail; }
    public double getHarga() { return harga; }
    public String getNote() { return note; }
    public int getQty() { return qty; }
}