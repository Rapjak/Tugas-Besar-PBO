package model;

import java.util.ArrayList;
import java.util.List;

public class CartItem {
    private int idMenu;
    private String namaMenu;
    private String detail;
    private double harga;
    private String note;
    private int qty;
    
    // TAMBAHAN: List Addon yang dipilih
    private List<AddOn> selectedAddOns; 

    public CartItem(int idMenu, String namaMenu, String detail, double harga, String note, List<AddOn> addons) {
        this.idMenu = idMenu;
        this.namaMenu = namaMenu;
        this.detail = detail;
        this.harga = harga;
        this.note = note;
        this.qty = 1;
        this.selectedAddOns = addons; // Simpan list
    }

    public int getIdMenu() { return idMenu; }
    public String getNamaMenu() { return namaMenu; }
    public String getDetail() { return detail; }
    public double getHarga() { return harga; }
    public String getNote() { return note; }
    public int getQty() { return qty; }
    
    // Getter Addon
    public List<AddOn> getSelectedAddOns() { 
        return selectedAddOns != null ? selectedAddOns : new ArrayList<>(); 
    }
}