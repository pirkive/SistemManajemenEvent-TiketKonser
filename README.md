# 🎫 ngonser yuk! - Sistem Manajemen Event & Tiket Konser Berbasis Desktop

**ngonser yuk!** adalah aplikasi manajemen event dan pemesanan tiket konser berbasis Java Swing. Aplikasi ini dirancang untuk menangani alur transaksi tiket secara end-to-end, mulai dari pengelolaan event oleh Admin hingga penerbitan e-tiket dengan QR Code untuk Pembeli.

## ✨ Fitur Utama

### 🛒 Fitur Pembeli
- **Katalog Event**: Melihat daftar konser aktif dengan informasi kuota dan harga real-time.
- **Validasi Stok**: Sistem otomatis mengecek ketersediaan tiket sebelum melanjutkan transaksi.
- **Multi-Metode Pembayaran**: Mendukung simulasi pembayaran via Transfer Bank, QRIS, E-Wallet, dan Kartu Kredit.
- **Otorisasi Payment Gateway**: Simulasi persetujuan transaksi yang aman.
- **E-Tiket & QR Code**: Generate struk digital otomatis dengan QR Code unik menggunakan integrasi API eksternal.

### ⚙️ Fitur Admin & Penyelenggara
- **Dashboard Manajemen**: CRUD (Create, Read, Update, Delete) data konser.
- **Monitoring Transaksi**: Melihat riwayat pembelian tiket secara sistematis.
- **Sinkronisasi Stok**: Pengurangan kuota otomatis setiap kali transaksi berhasil dilakukan.

## 🛠️ Teknologi & Library
- **Java SE (JDK 8+)**
- **Java Swing & AWT** (Antarmuka Grafis)
- **MySQL** (Database Relasional)
- **JDBC Connector** (Koneksi Database)
- **QR Server API** (Generator QR Code)

## 🗄️ Struktur Database

Database `eticket_db` terdiri dari 4 tabel utama yang saling berelasi:

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
