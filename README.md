🎫 ngonser yuk! - Sistem Manajemen Event & Tiket Konser

Aplikasi manajemen tiket konser berbasis Desktop yang dirancang untuk memberikan pengalaman pemesanan tiket yang mudah, cepat, dan aman. Proyek ini dibangun menggunakan Java Swing dengan integrasi database MySQL dan fitur QR Code dinamis.

🚀 Fitur Utama

👤 Pembeli (Customer)
* Katalog Konser: Melihat daftar event konser yang tersedia secara real-time.
* Alur Pemesanan Terintegrasi: Proses pemesanan tiket yang mengikuti standar *Activity Diagram* (Cek Stok -> Isi Data -> Pilih Metode Pembayaran -> Otorisasi).
* E-Tiket & QR Code: Mendapatkan struk digital dan QR Code unik setelah pembayaran berhasil dikonfirmasi.

🛡️ Admin / Panitia
* Manajemen Event (CRUD): Menambah, melihat, mengubah, dan menghapus data konser (Judul, Kuota, Harga).
* Update Stok Otomatis: Stok tiket akan berkurang secara otomatis setiap kali ada transaksi sukses.

🛠️ Teknologi yang Digunakan
* Bahasa Pemrograman: Java (JDK 8+)
* GUI Library: Java Swing & AWT
* Database: MySQL
* API Eksternal: [QR Server API](https://goqr.me/api/) (untuk generate QR Code)
* Version Control: Git & GitHub

📋 Prasyarat Sistem
1.  XAMPP / WAMP: Untuk menjalankan server database MySQL.
2.  Java Development Kit (JDK): Versi 8 atau yang lebih baru.
3.  MySQL Connector J: Driver JDBC untuk menghubungkan Java dengan MySQL.
4.  Koneksi Internet: Diperlukan untuk memuat QR Code pada struk pembayaran.

⚙️ Instalasi & Setup
1. Persiapan Database
Buat database baru dengan nama `eticket_db` dan jalankan query berikut untuk membuat tabel beserta relasinya:

```sql
-- 1. Tabel Users
CREATE TABLE `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('Pembeli','Penyelenggara','Admin') NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
);

-- 2. Tabel Events
CREATE TABLE `events` (
  `event_id` varchar(10) NOT NULL,
  `title` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `event_date` datetime DEFAULT NULL,
  `quota` int(11) NOT NULL,
  `price` double NOT NULL,
  PRIMARY KEY (`event_id`)
);

-- 3. Tabel Tickets (Histori Tiket)
CREATE TABLE `tickets` (
  `ticket_id` varchar(20) NOT NULL,
  `event_id` varchar(10) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `purchase_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `status` enum('Lunas','Dibatalkan') DEFAULT 'Lunas',
  PRIMARY KEY (`ticket_id`),
  FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
);

-- 4. Tabel Transactions (Histori Pembayaran)
CREATE TABLE `transactions` (
  `transaction_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `event_id` varchar(50) DEFAULT NULL,
  `event_title` varchar(255) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `total_price` double DEFAULT NULL,
  `transaction_date` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`transaction_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`)
);
