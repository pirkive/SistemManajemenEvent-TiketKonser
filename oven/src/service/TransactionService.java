package service;

import database.DatabaseConfig;
import model.Event;
import model.User;
import java.sql.*;

public class TransactionService {

    // Method untuk memproses pesanan (Sesuai alur Activity Diagram)
    public boolean prosesPembayaran(User pembeli, Event event, int jumlahBeli) {
        
        // 1. Cek Ketersediaan Stok (Logika Enkapsulasi & Getter)
        if (event.getQuota() < jumlahBeli) {
            System.out.println("Gagal: Stok tiket '" + event.getTitle() + "' tidak mencukupi.");
            return false;
        }

        // 2. Simulasi Otorisasi Pembayaran (Activity Diagram: Payment Gateway)
        System.out.println("Menghubungi Payment Gateway...");
        double totalHarga = event.getPrice() * jumlahBeli;
        System.out.println("Total Tagihan untuk " + pembeli.getUsername() + ": Rp" + totalHarga);
        
        // Anggap saja pembayaran selalu berhasil untuk simulasi ini
        boolean isPaymentSuccess = true; 

        if (isPaymentSuccess) {
            // 3. Update Database (Activity Diagram: Update Kuota Tiket)
            return updateDataTransaksi(pembeli, event, jumlahBeli);
        }

        return false;
    }

    // Method Internal untuk urusan Database
    private boolean updateDataTransaksi(User user, Event event, int qty) {
        String queryUpdateStok = "UPDATE events SET quota = quota - ? WHERE event_id = ?";
        String querySimpanTiket = "INSERT INTO tickets (ticket_id, event_id, user_id) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection()) {
            // Memulai Transaksi Database (Agar data konsisten)
            conn.setAutoCommit(false);

            try (PreparedStatement psUpdate = conn.prepareStatement(queryUpdateStok);
                 PreparedStatement psTicket = conn.prepareStatement(querySimpanTiket)) {
                
                // Update Stok Event
                psUpdate.setInt(1, qty);
                psUpdate.setString(2, event.getEventId());
                psUpdate.executeUpdate();

                // Simpan Data Tiket Baru
                String ticketCode = "TIX-" + System.currentTimeMillis(); // Generate ID unik
                psTicket.setString(1, ticketCode);
                psTicket.setString(2, event.getEventId());
                psTicket.setInt(3, user.getUserId());
                psTicket.executeUpdate();

                // Jika semua oke, simpan permanen
                conn.commit();
                
                // Update sisa kuota di objek Java (Setter)
                event.setQuota(event.getQuota() - qty);
                
                System.out.println("Transaksi Berhasil! Kode Tiket: " + ticketCode);
                return true;

            } catch (SQLException e) {
                conn.rollback(); // Batalkan jika ada yang error
                System.err.println("Transaksi Database Gagal: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}