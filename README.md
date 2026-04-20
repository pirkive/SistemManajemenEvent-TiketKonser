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
-- Tabel Users
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE,
    password VARCHAR(50),
    role ENUM('Admin', 'Pembeli', 'Penyelenggara')
);

-- Tabel Events
CREATE TABLE events (
    event_id VARCHAR(10) PRIMARY KEY,
    title VARCHAR(100),
    quota INT,
    price DOUBLE
);

-- Tabel Tickets (Histori Transaksi)
CREATE TABLE tickets (
    ticket_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    event_id VARCHAR(10),
    quantity INT,
    total_price DOUBLE,
    purchase_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE
);

-- Contoh Data Awal (User & Event)
INSERT INTO users (username, password, role) VALUES ('admin1', 'admin123', 'Admin');
INSERT INTO users (username, password, role) VALUES ('amanda', 'pass123', 'Pembeli');

INSERT INTO events (event_id, title, quota, price) VALUES ('EVT-001', 'Konser Sheila On 7', 500, 350000);
