package model;

import java.sql.Date;

public class Discount {
    private int id;
    private String nama;
    private String tipe; // "persen" atau "nominal"
    private double nilai;
    private double minBelanja;

    public Discount(int id, String nama, String tipe, double nilai, double minBelanja) {
        this.id = id;
        this.nama = nama;
        this.tipe = tipe;
        this.nilai = nilai;
        this.minBelanja = minBelanja;
    }

    public int getId() { return id; }
    public String getNama() { return nama; }
    public String getTipe() { return tipe; }
    public double getNilai() { return nilai; }
    public double getMinBelanja() { return minBelanja; }
}