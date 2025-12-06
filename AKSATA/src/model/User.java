/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Lenovo
 */
public class User {
    private int id;
    private String username;
    private String role; // 'admin', 'barista', 'manager'
    private int idCabang;

    public User(int id, String username, String role, int idCabang) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.idCabang = idCabang;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public int getIdCabang() { return idCabang; }
}
