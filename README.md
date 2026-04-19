Sistem E-Ticket "ngonser yuk!"

A. Deskripsi Umum
"ngonser yuk!" adalah aplikasi sistem manajemen event dan pemesanan tiket konser berbasis *desktop*. Aplikasi ini dibangun menggunakan antarmuka grafis (GUI) Java Swing dan terintegrasi dengan database MySQL. Sistem ini memisahkan hak akses antara Admin (untuk mengelola data event) dan Pembeli (untuk memesan tiket).



B. Teknologi yang Digunakan
* Bahasa Pemrograman: Java (JDK 8 atau lebih baru)
* Antarmuka Pengguna (GUI): Java Swing & AWT
* Database: MySQL
* Konektivitas Database: JDBC (Java Database Connectivity) MySQL Connector
* API Eksternal: QR Server API (`api.qrserver.com`) untuk men-*generate* QR Code secara dinamis.



C. Struktur Direktori Kode (Arsitektur MVC)
Aplikasi ini menggunakan pendekatan arsitektur *Model-View-Controller/Service* (MVC) agar kode rapi dan modular:
* `model/`
    * `User.java`: Representasi data pengguna (id, username, role).
    * `Event.java`: Representasi data konser (id, judul, kuota, harga).
* `view/`
    * `MainGUI.java`: Menangani seluruh tampilan antarmuka (Login, Register, Dashboard Admin, Dashboard Pembeli).
* `service/`
    * `TransactionService.java`: Menangani logika bisnis, seperti memproses pembayaran dan mengurangi stok tiket di database.
* `database/`
    * `DatabaseConfig.java`: Kelas utilitas untuk mengatur koneksi ke database MySQL.



D. Fitur Utama
1. Fitur Umum (Autentikasi)
* Login: Pengguna masuk menggunakan username dan password. Sistem akan mengarahkan ke dashboard yang sesuai dengan *role* (Admin/Pembeli).
* Register: Pengguna baru dapat mendaftarkan akun. Secara *default*, akun baru akan mendapatkan *role* "Pembeli".

2. Dashboard Admin (Manajemen Event)
* Read: Melihat daftar event konser beserta ID, sisa stok, dan harga dasar di dalam tabel.
* Create: Menambahkan data event konser baru ke dalam sistem.
* Update: Mengubah detail event (seperti menambah kuota tiket atau mengubah harga).
* Delete: Menghapus data event dari sistem.

3. Dashboard Pembeli (Pemesanan Tiket)
* Katalog Event: Melihat daftar konser yang tersedia.
* Sistem Transaksi: Fitur pembelian tiket dengan alur yang disesuaikan dengan standar *Payment Gateway*.



E. Alur Pembelian Tiket (Sesuai Activity Diagram)
Proses pembelian dirancang agar interaktif dan meminimalisir *error* dari pengguna, dengan alur sebagai berikut:
* Pilih Event: Pembeli memilih konser dari tabel katalog.
* Validasi Stok (Sistem): Sistem secara otomatis mengecek apakah kuota tiket masih lebih dari 0. Jika habis, proses dihentikan dengan peringatan.
* Input Data Pemesanan: Pembeli memasukkan jumlah tiket yang ingin dibeli. Sistem memvalidasi agar input tidak melebihi stok yang ada.
* Pilih Metode Pembayaran: Pembeli memilih metode pembayaran (*Transfer Bank, QRIS, E-Wallet, Kartu Kredit*) melalui *dropdown*.
* Otorisasi Payment Gateway: Sistem melakukan simulasi persetujuan pembayaran.
* Update Data (Sistem): Jika pembayaran berhasil, sistem akan memanggil `TransactionService` untuk mengurangi stok tiket di database.
* Generate E-Tiket & QR Code: Sistem membuat kode struk unik (kombinasi "TIX", username, dan *timestamp*) lalu mengunduh gambar QR Code dari Web API untuk ditampilkan pada resi digital.
   


F. Panduan Instalasi dan Konfigurasi
Untuk menjalankan aplikasi ini di komputer lokal, ikuti langkah-langkah berikut:
Langkah 1: Setup Database MySQL
* Buka XAMPP/WAMP dan jalankan service MySQL.
* Buat database baru (misal: `eticket_db`).
* Buat tabel `users` (kolom: user_id, username, password, role) dan tabel `events` (kolom: event_id, title, quota, price).
* Pastikan kredensial di `DatabaseConfig.java` (URL, User, Password) sudah sesuai dengan database lokalmu.

Langkah 2: Setup IDE (NetBeans/IntelliJ/Eclipse)
1. *Clone* atau *import* folder project ini ke dalam IDE.
2. Tambahkan *library* MySQL JDBC Driver / Connector (.jar) ke dalam *dependencies* atau *build path* project.

Langkah 3: Menjalankan Aplikasi
1. Pastikan komputer terhubung ke internet (wajib untuk memunculkan gambar QR Code E-Tiket).
2. Jalankan (Run) file `MainGUI.java`.
