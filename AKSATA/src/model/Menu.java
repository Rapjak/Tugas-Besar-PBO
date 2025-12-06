package model;

public class Menu {
    private int id; // ID Menu
    private String name;
    private String category;
    private String description;
    private String namaPetani;
    private FlavorProfile flavorProfile;
    private double price;

    // Constructor Updated
    public Menu(int id, String name, String category, String description, 
                String namaPetani, FlavorProfile flavorProfile, double price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.namaPetani = namaPetani;
        this.flavorProfile = flavorProfile;
        this.price = price;
    }

    // Getter
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; } // <--- Getter Baru
    // ... getter lainnya jika perlu
}