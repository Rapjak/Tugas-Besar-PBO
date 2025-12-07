package model;

public class AddOn {
    private int id;
    private String name;
    private double price;

    public AddOn(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    
    @Override
    public String toString() {
        return name; // Untuk keperluan display sederhana jika butuh
    }
}