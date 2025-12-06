/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Lenovo
 */
public class Cabang {
    private int id;
    private String nama;
    private String alamat;

    public Cabang(int id, String nama, String alamat) {
        this.id = id;
        this.nama = nama;
        this.alamat = alamat;
    }

    public int getId() { return id; }
    public String getNama() { return nama; }
    public String getAlamat() { return alamat; }
    
    // Optional: Agar jika objek ini diprint, muncul namanya
    @Override
    public String toString() {
        return nama;
    }
}