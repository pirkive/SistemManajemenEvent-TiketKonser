package model;

public class User {
    // ENKAPSULASI: Atribut dibuat private
    private int userId;
    private String username;
    private String role;

    // CONSTRUCTOR: Mengisi data saat objek dibuat (new User)
    public User(int userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    // GETTER & SETTER: Akses data secara aman
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}