import database.DatabaseConfig;
import model.Event;
import model.User;
import service.TransactionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MainGUI {
    private User activeUser;
    private TransactionService transactionService = new TransactionService();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainGUI().showLoginWindow());
    }

    // ==========================================
    // 1. TAMPILAN JENDELA LOGIN (UPDATE LOGIKA ROLE)
    // ==========================================
    public void showLoginWindow() {
        JFrame loginFrame = new JFrame("Login E-Ticket");
        loginFrame.setSize(350, 220);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel lblUser = new JLabel("  Username:");
        JTextField txtUser = new JTextField();
        JLabel lblPass = new JLabel("  Password:");
        JPasswordField txtPass = new JPasswordField();
        
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRegister = new JButton("Daftar");
        JButton btnLogin = new JButton("Masuk");
        panelTombol.add(btnRegister);
        panelTombol.add(btnLogin);

        loginFrame.add(lblUser); loginFrame.add(txtUser);
        loginFrame.add(lblPass); loginFrame.add(txtPass);
        loginFrame.add(new JLabel("")); 
        loginFrame.add(panelTombol);

        btnLogin.addActionListener(e -> {
            String user = txtUser.getText();
            String pass = new String(txtPass.getPassword());

            try (Connection conn = DatabaseConfig.getConnection()) {
                String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, user);
                ps.setString(2, pass);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    activeUser = new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("role"));
                    JOptionPane.showMessageDialog(loginFrame, "Selamat Datang, " + activeUser.getUsername() + "!");
                    loginFrame.dispose(); 
                    
                    // LOGIKA ROLE: Cek apakah dia Panitia atau Pembeli biasa
                    if (activeUser.getRole().equalsIgnoreCase("Panitia") || activeUser.getRole().equalsIgnoreCase("Admin")) {
                        showAdminDashboardWindow(); // Buka Menu Panitia
                    } else {
                        showDashboardWindow(); // Buka Menu Pembeli
                    }
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Username atau Password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(loginFrame, "Koneksi Error: " + ex.getMessage());
            }
        });

        btnRegister.addActionListener(e -> {
            loginFrame.dispose(); 
            showRegisterWindow(); 
        });

        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    // ==========================================
    // 2. TAMPILAN JENDELA PENDAFTARAN
    // ==========================================
    public void showRegisterWindow() {
        JFrame regFrame = new JFrame("Daftar Akun Baru");
        regFrame.setSize(350, 250);
        regFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        regFrame.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel lblUser = new JLabel("  Username Baru:");
        JTextField txtUser = new JTextField();
        JLabel lblPass = new JLabel("  Password:");
        JPasswordField txtPass = new JPasswordField();
        JLabel lblPassConfirm = new JLabel("  Konfirmasi Password:");
        JPasswordField txtPassConfirm = new JPasswordField();

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBatal = new JButton("Batal");
        JButton btnSimpan = new JButton("Simpan");
        panelTombol.add(btnBatal);
        panelTombol.add(btnSimpan);

        regFrame.add(lblUser); regFrame.add(txtUser);
        regFrame.add(lblPass); regFrame.add(txtPass);
        regFrame.add(lblPassConfirm); regFrame.add(txtPassConfirm);
        regFrame.add(new JLabel("")); 
        regFrame.add(panelTombol);

        btnBatal.addActionListener(e -> {
            regFrame.dispose();
            showLoginWindow();
        });

        btnSimpan.addActionListener(e -> {
            String user = txtUser.getText();
            String pass = new String(txtPass.getPassword());
            String passConfirm = new String(txtPassConfirm.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(regFrame, "Data tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!pass.equals(passConfirm)) {
                JOptionPane.showMessageDialog(regFrame, "Konfirmasi password tidak cocok!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseConfig.getConnection()) {
                PreparedStatement psCheck = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
                psCheck.setString(1, user);
                if (psCheck.executeQuery().next()) {
                    JOptionPane.showMessageDialog(regFrame, "Username sudah digunakan, pilih yang lain!", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String sqlInsert = "INSERT INTO users (username, password, role) VALUES (?, ?, 'Pembeli')";
                PreparedStatement psInsert = conn.prepareStatement(sqlInsert);
                psInsert.setString(1, user);
                psInsert.setString(2, pass);
                psInsert.executeUpdate();

                JOptionPane.showMessageDialog(regFrame, "Pendaftaran berhasil! Silakan login.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                regFrame.dispose();
                showLoginWindow();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(regFrame, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        regFrame.setLocationRelativeTo(null);
        regFrame.setVisible(true);
    }

    // ==========================================
    // 3. TAMPILAN JENDELA UTAMA (PEMBELI)
    // ==========================================
    public void showDashboardWindow() {
        JFrame dashboardFrame = new JFrame("Menu Pembeli - " + activeUser.getUsername());
        dashboardFrame.setSize(750, 450); 
        dashboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dashboardFrame.setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("Daftar Konser Tersedia", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        dashboardFrame.add(lblTitle, BorderLayout.NORTH);

        String[] columnNames = {"ID Event", "Nama Konser", "Sisa Kuota", "Harga (Rp)"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable tableEvent = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableEvent); 
        
        loadDataToTable(tableModel);
        dashboardFrame.add(scrollPane, BorderLayout.CENTER);

        JPanel panelBeli = new JPanel(new FlowLayout());
        
        JLabel lblEventId = new JLabel("ID Event:");
        JTextField txtEventId = new JTextField(7);
        JLabel lblQty = new JLabel("Jumlah:");
        JTextField txtQty = new JTextField(3);
        JLabel lblPayment = new JLabel("Metode:");
        String[] paymentMethods = {"Transfer Bank", "QRIS", "E-Wallet", "Kartu Kredit"};
        JComboBox<String> cbPayment = new JComboBox<>(paymentMethods);
        JButton btnBeli = new JButton("Beli Tiket");

        panelBeli.add(lblEventId); panelBeli.add(txtEventId);
        panelBeli.add(lblQty); panelBeli.add(txtQty);
        panelBeli.add(lblPayment); panelBeli.add(cbPayment);
        panelBeli.add(btnBeli);
        
        dashboardFrame.add(panelBeli, BorderLayout.SOUTH);

        btnBeli.addActionListener(e -> {
            String eventId = txtEventId.getText();
            int qty;
            try {
                qty = Integer.parseInt(txtQty.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dashboardFrame, "Jumlah tiket harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String selectedPayment = (String) cbPayment.getSelectedItem();

            try (Connection conn = DatabaseConfig.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM events WHERE event_id = ?");
                ps.setString(1, eventId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    Event selectedEvent = new Event(
                        rs.getString("event_id"), rs.getString("title"),
                        rs.getInt("quota"), rs.getDouble("price")
                    );

                    boolean sukses = transactionService.prosesPembayaran(activeUser, selectedEvent, qty);

                    if (sukses) {
                        double totalHarga = selectedEvent.getPrice() * qty;
                        String receipt = "TRANSAKSI BERHASIL!\n\n" +
                                         "Konser: " + selectedEvent.getTitle() + "\n" +
                                         "Jumlah: " + qty + " Tiket\n" +
                                         "Total Tagihan: Rp " + totalHarga + "\n" +
                                         "Metode: " + selectedPayment + "\n\n" +
                                         "Terima kasih telah memesan!";
                        JOptionPane.showMessageDialog(dashboardFrame, receipt, "Struk", JOptionPane.INFORMATION_MESSAGE);
                        loadDataToTable(tableModel); 
                        txtEventId.setText(""); txtQty.setText("");
                    } else {
                        JOptionPane.showMessageDialog(dashboardFrame, "Stok tidak cukup!", "Gagal", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(dashboardFrame, "ID Event tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dashboardFrame, "Database Error: " + ex.getMessage());
            }
        });

        dashboardFrame.setLocationRelativeTo(null);
        dashboardFrame.setVisible(true);
    }

    // ==========================================
    // 4. TAMPILAN JENDELA KHUSUS PANITIA (FITUR BARU)
    // ==========================================
    public void showAdminDashboardWindow() {
        JFrame adminFrame = new JFrame("Menu Panitia - Kelola Konser");
        adminFrame.setSize(800, 500); 
        adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        adminFrame.setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("Panel Manajemen Data Event", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        adminFrame.add(lblTitle, BorderLayout.NORTH);

        // Tabel Data
        String[] columnNames = {"ID Event", "Nama Konser", "Kuota", "Harga"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable tableEvent = new JTable(tableModel);
        adminFrame.add(new JScrollPane(tableEvent), BorderLayout.CENTER);
        loadDataToTable(tableModel);

        // Panel Form Input (Kiri)
        JPanel panelForm = new JPanel(new GridLayout(5, 2, 5, 10));
        panelForm.setBorder(BorderFactory.createTitledBorder("Form Data Konser"));
        
        JTextField txtId = new JTextField();
        JTextField txtJudul = new JTextField();
        JTextField txtKuota = new JTextField();
        JTextField txtHarga = new JTextField();

        panelForm.add(new JLabel(" ID Event:")); panelForm.add(txtId);
        panelForm.add(new JLabel(" Nama Konser:")); panelForm.add(txtJudul);
        panelForm.add(new JLabel(" Kuota Tiket:")); panelForm.add(txtKuota);
        panelForm.add(new JLabel(" Harga Tiket:")); panelForm.add(txtHarga);

        // Panel Tombol Aksi (Bawah Form)
        JPanel panelAksi = new JPanel(new FlowLayout());
        JButton btnTambah = new JButton("Tambah");
        JButton btnUbah = new JButton("Ubah");
        JButton btnHapus = new JButton("Hapus");
        
        panelAksi.add(btnTambah);
        panelAksi.add(btnUbah);
        panelAksi.add(btnHapus);
        panelForm.add(new JLabel("")); // Spacer
        panelForm.add(panelAksi);

        adminFrame.add(panelForm, BorderLayout.SOUTH);

        // Aksi Klik Tabel (Auto-Fill ke Form)
        tableEvent.getSelectionModel().addListSelectionListener(e -> {
            int row = tableEvent.getSelectedRow();
            if (row != -1) {
                txtId.setText(tableModel.getValueAt(row, 0).toString());
                txtJudul.setText(tableModel.getValueAt(row, 1).toString());
                txtKuota.setText(tableModel.getValueAt(row, 2).toString());
                txtHarga.setText(tableModel.getValueAt(row, 3).toString());
            }
        });

        // Aksi Tambah
        btnTambah.addActionListener(e -> {
            try (Connection conn = DatabaseConfig.getConnection()) {
                String sql = "INSERT INTO events (event_id, title, quota, price) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, txtId.getText());
                ps.setString(2, txtJudul.getText());
                ps.setInt(3, Integer.parseInt(txtKuota.getText()));
                ps.setDouble(4, Double.parseDouble(txtHarga.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(adminFrame, "Event Berhasil Ditambahkan!");
                loadDataToTable(tableModel); // Refresh Tabel
            } catch (SQLException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(adminFrame, "Error: " + ex.getMessage());
            }
        });

        // Aksi Ubah (Update)
        btnUbah.addActionListener(e -> {
            try (Connection conn = DatabaseConfig.getConnection()) {
                String sql = "UPDATE events SET title=?, quota=?, price=? WHERE event_id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, txtJudul.getText());
                ps.setInt(2, Integer.parseInt(txtKuota.getText()));
                ps.setDouble(3, Double.parseDouble(txtHarga.getText()));
                ps.setString(4, txtId.getText()); // Acuan WHERE
                ps.executeUpdate();
                JOptionPane.showMessageDialog(adminFrame, "Data Event Berhasil Diubah!");
                loadDataToTable(tableModel);
            } catch (SQLException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(adminFrame, "Error: " + ex.getMessage());
            }
        });

        // Aksi Hapus
        btnHapus.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(adminFrame, "Yakin hapus event ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DatabaseConfig.getConnection()) {
                    String sql = "DELETE FROM events WHERE event_id=?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, txtId.getText());
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(adminFrame, "Event Dihapus!");
                    loadDataToTable(tableModel);
                    txtId.setText(""); txtJudul.setText(""); txtKuota.setText(""); txtHarga.setText("");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(adminFrame, "Error: Tidak bisa menghapus event karena mungkin sudah ada tiket yang terjual.");
                }
            }
        });

        adminFrame.setLocationRelativeTo(null);
        adminFrame.setVisible(true);
    }

    // ==========================================
    // 5. METHOD BERSAMA: AMBIL DATA KE TABEL
    // ==========================================
    private void loadDataToTable(DefaultTableModel model) {
        model.setRowCount(0); 
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM events")) {
             
            while (rs.next()) {
                Object[] rowData = {
                    rs.getString("event_id"),
                    rs.getString("title"),
                    rs.getInt("quota"),
                    rs.getDouble("price")
                };
                model.addRow(rowData);
            }
        } catch (SQLException e) {
            System.err.println("Gagal memuat data tabel: " + e.getMessage());
        }
    }
}