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
        // 1. Sesuaikan nama tabel dan kolom dengan database kamu
        String queryUpdateStok = "UPDATE events SET quota = quota - ? WHERE event_id = ?";
        String querySimpanTransaksi = "INSERT INTO transactions (user_id, event_id, event_title, quantity, total_price) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psUpdate = conn.prepareStatement(queryUpdateStok);
                PreparedStatement psTrans = conn.prepareStatement(querySimpanTransaksi)) {
            
                // Update Stok
                psUpdate.setInt(1, qty);
                psUpdate.setString(2, event.getEventId());
                psUpdate.executeUpdate();

                // Simpan ke tabel TRANSACTIONS (Bukan tickets)
                psTrans.setInt(1, user.getUserId());
                psTrans.setString(2, event.getEventId());
                psTrans.setString(3, event.getTitle());
                psTrans.setInt(4, qty);
                psTrans.setDouble(5, event.getPrice() * qty);
                psTrans.executeUpdate();

                conn.commit();
                event.setQuota(event.getQuota() - qty);
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Gagal simpan transaksi: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}