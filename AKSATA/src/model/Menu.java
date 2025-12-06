package model;

public class Menu {
    private int id;
    private String name;
    private String category;
    private String deskripsi;   
    private String namaPetani;  
    private boolean isAvailable;
    private FlavorProfile flavorProfile;

    // Constructor Updated
    public Menu(int id, String name, String category, String deskripsi, String namaPetani, FlavorProfile profile) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.deskripsi = deskripsi;
        this.namaPetani = namaPetani;
        this.flavorProfile = profile;
        this.isAvailable = true;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getNamaPetani() { return namaPetani; }
    
    public String getDetails() {
        return name + "\nPetani: " + namaPetani + "\nDeskripsi: " + deskripsi;
    }
}