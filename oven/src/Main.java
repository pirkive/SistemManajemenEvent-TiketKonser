import database.DatabaseConfig;
import model.User;
import model.Event;
import service.TransactionService;
import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TransactionService transactionService = new TransactionService();
        
        System.out.println("==========================================");
        System.out.println("       SISTEM MANAJEMEN E-TICKET         ");
        System.out.println("==========================================");

        try (Connection conn = DatabaseConfig.getConnection()) {
            // --- 1. PROSES LOGIN (Sesuai Activity Diagram) ---
            System.out.print("Username : "); String inputUser = scanner.next();
            System.out.print("Password : "); String inputPass = scanner.next();

            String loginSql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement psLogin = conn.prepareStatement(loginSql);
            psLogin.setString(1, inputUser);
            psLogin.setString(2, inputPass);
            ResultSet rsUser = psLogin.executeQuery();

            if (rsUser.next()) {
                // Gunakan CONSTRUCTOR untuk membuat objek User
                User activeUser = new User(
                    rsUser.getInt("user_id"), 
                    rsUser.getString("username"), 
                    rsUser.getString("role")
                );
                System.out.println("\n[SISTEM] Login Berhasil! Halo, " + activeUser.getUsername());

                // --- 2. TAMPILKAN DAFTAR EVENT (Sesuai Activity Diagram) ---
                System.out.println("\n--- DAFTAR KONSER TERSEDIA ---");
                Statement stmt = conn.createStatement();
                ResultSet rsEvents = stmt.executeQuery("SELECT * FROM events");

                while (rsEvents.next()) {
                    System.out.println("ID: " + rsEvents.getString("event_id") + 
                                       " | " + rsEvents.getString("title") + 
                                       " | Stok: " + rsEvents.getInt("quota") + 
                                       " | Harga: Rp" + rsEvents.getDouble("price"));
                }

                // --- 3. PROSES PEMESANAN ---
                System.out.print("\nMasukkan ID Event yang ingin dibeli: ");
                String selectedId = scanner.next();
                
                // Cari data event yang dipilih untuk dijadikan OBJEK
                PreparedStatement psSelectEvent = conn.prepareStatement("SELECT * FROM events WHERE event_id = ?");
                psSelectEvent.setString(1, selectedId);
                ResultSet rsSelected = psSelectEvent.executeQuery();

                if (rsSelected.next()) {
                    // Buat objek Event (OOP)
                    Event selectedEvent = new Event(
                        rsSelected.getString("event_id"),
                        rsSelected.getString("title"),
                        rsSelected.getInt("quota"),
                        rsSelected.getDouble("price")
                    );

                    System.out.print("Jumlah tiket yang dibeli: ");
                    int qty = scanner.nextInt();

                    // --- 4. PANGGIL TRANSACTION SERVICE (Logika Terpisah) ---
                    System.out.println("\n--- MEMPROSES TRANSAKSI ---");
                    boolean sukses = transactionService.prosesPembayaran(activeUser, selectedEvent, qty);

                    if (sukses) {
                        System.out.println("Terima kasih telah memesan!");
                    } else {
                        System.out.println("Transaksi Gagal. Silakan coba lagi.");
                    }
                } else {
                    System.out.println("ID Event tidak ditemukan.");
                }

            } else {
                System.out.println("\n[SISTEM] Login Gagal! Username atau Password salah.");
            }

        } catch (SQLException e) {
            System.err.println("Koneksi Error: " + e.getMessage());
        } finally {
            System.out.println("\n==========================================");
            System.out.println("     Sesi Berakhir. Sampai Jumpa!        ");
            System.out.println("==========================================");
            scanner.close();
        }
    }
}