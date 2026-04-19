package view;

import database.DatabaseConfig;
import model.Event;
import model.User;
import service.TransactionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MainGUI {

    // --- Styling & Font Modern ---
    private final Font titleFont = new Font("Segoe UI", Font.BOLD, 24);
    private final Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private final Color primaryColor = new Color(41, 128, 185); // Biru
    private final Color successColor = new Color(46, 204, 113); // Hijau
    private final Color dangerColor = new Color(231, 76, 60);   // Merah
    private final Color warningColor = new Color(241, 196, 15); // Kuning

    // --- Instance Variable ---
    private User activeUser;
    private TransactionService transactionService = new TransactionService();

    public static void main(String[] args) {
        // Menggunakan UI Sistem
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new MainGUI().showLoginWindow());
    }

    // --- HELPER METHOD UNTUK TOMBOL (AGAR WARNA MUNCUL & MODERN) ---
    private void styleButton(JButton btn, Color bgColor, Color fgColor) {
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        // Tiga baris ini WAJIB di Windows agar warna background custom muncul
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Kursor jari
    }

    // ==========================================
    // 1. WINDOW LOGIN
    // ==========================================
    public void showLoginWindow() {
        JFrame loginFrame = new JFrame("E-Ticket System - Login");
        loginFrame.setSize(420, 330);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(primaryColor);
        JLabel lblHeader = new JLabel("Login E-Ticket");
        lblHeader.setFont(titleFont);
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setBorder(new EmptyBorder(20, 0, 20, 0));
        headerPanel.add(lblHeader);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 20));
        formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(regularFont);
        JTextField txtUser = new JTextField();
        txtUser.setFont(regularFont);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(regularFont);
        JPasswordField txtPass = new JPasswordField();
        txtPass.setFont(regularFont);

        formPanel.add(lblUser); formPanel.add(txtUser);
        formPanel.add(lblPass); formPanel.add(txtPass);

        // Button Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JButton btnRegister = new JButton("Daftar Akun");
        styleButton(btnRegister, Color.LIGHT_GRAY, Color.BLACK);
        
        JButton btnLogin = new JButton("Masuk");
        styleButton(btnLogin, primaryColor, Color.WHITE);

        btnPanel.add(btnRegister);
        btnPanel.add(btnLogin);

        // --- Action Listeners ---
        btnLogin.addActionListener(e -> {
            String user = txtUser.getText();
            String pass = new String(txtPass.getPassword());

            try (Connection conn = DatabaseConfig.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
                ps.setString(1, user);
                ps.setString(2, pass);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    activeUser = new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("role"));
                    loginFrame.dispose(); 
                    
                    if (activeUser.getRole().equalsIgnoreCase("Panitia") || activeUser.getRole().equalsIgnoreCase("Admin")) {
                        showAdminDashboard();
                    } else {
                        showDashboardPembeli();
                    }
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Username atau Password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(loginFrame, "Koneksi Database Error: " + ex.getMessage());
            }
        });

        btnRegister.addActionListener(e -> {
            loginFrame.dispose();
            showRegisterWindow();
        });

        loginFrame.add(headerPanel, BorderLayout.NORTH);
        loginFrame.add(formPanel, BorderLayout.CENTER);
        loginFrame.add(btnPanel, BorderLayout.SOUTH);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    // ==========================================
    // 2. WINDOW REGISTER
    // ==========================================
    public void showRegisterWindow() {
        JFrame regFrame = new JFrame("E-Ticket System - Daftar Baru");
        regFrame.setSize(420, 360);
        regFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        regFrame.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(successColor);
        JLabel lblHeader = new JLabel("Buat Akun Baru");
        lblHeader.setFont(titleFont);
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setBorder(new EmptyBorder(20, 0, 20, 0));
        headerPanel.add(lblHeader);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        formPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        formPanel.add(new JLabel("Username:")); 
        JTextField txtUser = new JTextField(); formPanel.add(txtUser);
        
        formPanel.add(new JLabel("Password:")); 
        JPasswordField txtPass = new JPasswordField(); formPanel.add(txtPass);
        
        formPanel.add(new JLabel("Konfirmasi Pass:")); 
        JPasswordField txtPassConfirm = new JPasswordField(); formPanel.add(txtPassConfirm);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnBatal = new JButton("Batal");
        styleButton(btnBatal, Color.LIGHT_GRAY, Color.BLACK);
        
        JButton btnSimpan = new JButton("Simpan");
        styleButton(btnSimpan, successColor, Color.WHITE);
        
        btnPanel.add(btnBatal); btnPanel.add(btnSimpan);

        btnBatal.addActionListener(e -> {
            regFrame.dispose();
            showLoginWindow();
        });

        btnSimpan.addActionListener(e -> {
            String user = txtUser.getText();
            String pass = new String(txtPass.getPassword());
            String passConfirm = new String(txtPassConfirm.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(regFrame, "Data tidak boleh kosong!");
                return;
            }
            if (!pass.equals(passConfirm)) {
                JOptionPane.showMessageDialog(regFrame, "Konfirmasi password berbeda!");
                return;
            }

            try (Connection conn = DatabaseConfig.getConnection()) {
                String sqlInsert = "INSERT INTO users (username, password, role) VALUES (?, ?, 'Pembeli')";
                PreparedStatement psInsert = conn.prepareStatement(sqlInsert);
                psInsert.setString(1, user);
                psInsert.setString(2, pass);
                psInsert.executeUpdate();

                JOptionPane.showMessageDialog(regFrame, "Akun berhasil dibuat! Silakan Login.");
                regFrame.dispose();
                showLoginWindow();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(regFrame, "Error (Mungkin username sudah ada): " + ex.getMessage());
            }
        });

        regFrame.add(headerPanel, BorderLayout.NORTH);
        regFrame.add(formPanel, BorderLayout.CENTER);
        regFrame.add(btnPanel, BorderLayout.SOUTH);
        regFrame.setLocationRelativeTo(null);
        regFrame.setVisible(true);
    }

    // ==========================================
    // 3. DASHBOARD PEMBELI (DENGAN FITUR STRUK)
    // ==========================================
    public void showDashboardPembeli() {
        JFrame dashboardFrame = new JFrame("Dashboard Pembeli - " + activeUser.getUsername());
        dashboardFrame.setSize(750, 520);
        dashboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dashboardFrame.setLayout(new BorderLayout(15, 15));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel("Event Konser Tersedia");
        lblTitle.setFont(titleFont);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnLogout = new JButton("Logout");
        styleButton(btnLogout, dangerColor, Color.WHITE);
        btnLogout.addActionListener(e -> {
            dashboardFrame.dispose();
            showLoginWindow();
        });
        headerPanel.add(btnLogout, BorderLayout.EAST);

        // Tabel Data
        String[] columnNames = {"ID Event", "Nama Konser", "Sisa Kuota", "Harga (Rp)"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable tableEvent = new JTable(tableModel);
        tableEvent.setRowHeight(30); // Dibuat lebih lega
        tableEvent.setFont(regularFont);
        tableEvent.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableEvent.getTableHeader().setBackground(new Color(236, 240, 241));
        
        JScrollPane scrollPane = new JScrollPane(tableEvent);
        scrollPane.setBorder(new EmptyBorder(0, 20, 0, 20));
        loadDataToTable(tableModel);

        // Panel Pembelian
        JPanel panelBeli = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        panelBeli.setBorder(BorderFactory.createTitledBorder("Beli Tiket Cepat"));
        
        panelBeli.add(new JLabel("ID Event:"));
        JTextField txtEventId = new JTextField(8);
        txtEventId.setFont(regularFont);
        panelBeli.add(txtEventId);
        
        panelBeli.add(new JLabel("Jumlah:"));
        JTextField txtQty = new JTextField(4);
        txtQty.setFont(regularFont);
        panelBeli.add(txtQty);
        
        JButton btnBeli = new JButton("Beli Tiket");
        styleButton(btnBeli, successColor, Color.WHITE);
        panelBeli.add(btnBeli);

        // Aksi Klik Tabel (Auto-Fill ID Event)
        tableEvent.getSelectionModel().addListSelectionListener(e -> {
            int row = tableEvent.getSelectedRow();
            if (row != -1) {
                txtEventId.setText(tableModel.getValueAt(row, 0).toString());
                txtQty.setText("1"); // Default isi 1
            }
        });

        btnBeli.addActionListener(e -> {
            String eventId = txtEventId.getText();
            try {
                int qty = Integer.parseInt(txtQty.getText());
                
                try (Connection conn = DatabaseConfig.getConnection()) {
                    PreparedStatement ps = conn.prepareStatement("SELECT * FROM events WHERE event_id = ?");
                    ps.setString(1, eventId);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        Event selectedEvent = new Event(
                            rs.getString("event_id"), 
                            rs.getString("title"), 
                            rs.getInt("quota"), 
                            rs.getDouble("price")
                        );

                        // Panggil Transaction Service
                        boolean sukses = transactionService.prosesPembayaran(activeUser, selectedEvent, qty);

                        if (sukses) {
                            // --- FITUR CETAK STRUK ---
                            double totalHarga = selectedEvent.getPrice() * qty;
                            String strukText = String.format(
                                "====================================\n" +
                                "           E-TICKET RECEIPT         \n" +
                                "====================================\n" +
                                "Pembeli      : %s\n" +
                                "Konser       : %s\n" +
                                "Harga Satuan : Rp %,.0f\n" +
                                "Jumlah Beli  : %d Tiket\n" +
                                "------------------------------------\n" +
                                "TOTAL BAYAR  : Rp %,.0f\n" +
                                "====================================\n" +
                                "Pembayaran Berhasil. Terima Kasih!\n",
                                activeUser.getUsername(),
                                selectedEvent.getTitle(),
                                selectedEvent.getPrice(),
                                qty,
                                totalHarga
                            );

                            JTextArea textArea = new JTextArea(strukText);
                            textArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); // Font struk kasir
                            textArea.setEditable(false);
                            textArea.setBackground(new Color(245, 245, 245));
                            textArea.setBorder(new EmptyBorder(10, 10, 10, 10));

                            JOptionPane.showMessageDialog(dashboardFrame, textArea, "Bukti Pembelian", JOptionPane.INFORMATION_MESSAGE);
                            // -------------------------

                            loadDataToTable(tableModel); // Refresh Tabel
                            txtEventId.setText(""); txtQty.setText("");
                        } else {
                            JOptionPane.showMessageDialog(dashboardFrame, "Transaksi Gagal! Cek sisa stok tiket.", "Gagal", JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(dashboardFrame, "ID Event tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dashboardFrame, "Jumlah tiket harus berupa angka!", "Input Tidak Valid", JOptionPane.ERROR_MESSAGE);
            }
        });

        dashboardFrame.add(headerPanel, BorderLayout.NORTH);
        dashboardFrame.add(scrollPane, BorderLayout.CENTER);
        dashboardFrame.add(panelBeli, BorderLayout.SOUTH);
        dashboardFrame.setLocationRelativeTo(null);
        dashboardFrame.setVisible(true);
    }

    // ==========================================
    // 4. DASHBOARD PANITIA (ADMIN)
    // ==========================================
    public void showAdminDashboard() {
        JFrame adminFrame = new JFrame("Admin Panel - " + activeUser.getUsername());
        adminFrame.setSize(850, 580);
        adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        adminFrame.setLayout(new BorderLayout(10, 10));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94)); // Biru Gelap
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel("Manajemen Event & Konser");
        lblTitle.setFont(titleFont);
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnLogout = new JButton("Logout");
        styleButton(btnLogout, dangerColor, Color.WHITE);
        btnLogout.addActionListener(e -> {
            adminFrame.dispose();
            showLoginWindow();
        });
        headerPanel.add(btnLogout, BorderLayout.EAST);

        String[] columnNames = {"ID Event", "Nama Konser", "Kuota", "Harga (Rp)"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable tableEvent = new JTable(tableModel);
        tableEvent.setRowHeight(30);
        tableEvent.setFont(regularFont);
        tableEvent.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(tableEvent);
        scrollPane.setBorder(new EmptyBorder(0, 20, 0, 20));
        loadDataToTable(tableModel);

        JPanel panelBawah = new JPanel(new BorderLayout());
        panelBawah.setBorder(new EmptyBorder(10, 20, 20, 20));

        JPanel panelForm = new JPanel(new GridLayout(2, 4, 10, 15));
        panelForm.setBorder(BorderFactory.createTitledBorder("Input / Edit Data Event"));
        
        JTextField txtId = new JTextField();
        JTextField txtJudul = new JTextField();
        JTextField txtKuota = new JTextField();
        JTextField txtHarga = new JTextField();

        panelForm.add(new JLabel("  ID Event:")); panelForm.add(txtId);
        panelForm.add(new JLabel("  Nama Konser:")); panelForm.add(txtJudul);
        panelForm.add(new JLabel("  Kuota Tiket:")); panelForm.add(txtKuota);
        panelForm.add(new JLabel("  Harga Tiket:")); panelForm.add(txtHarga);

        JPanel panelAksi = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnTambah = new JButton("Tambah"); styleButton(btnTambah, primaryColor, Color.WHITE);
        JButton btnUbah = new JButton("Ubah"); styleButton(btnUbah, warningColor, Color.BLACK);
        JButton btnHapus = new JButton("Hapus"); styleButton(btnHapus, dangerColor, Color.WHITE);
        JButton btnClear = new JButton("Clear"); styleButton(btnClear, Color.LIGHT_GRAY, Color.BLACK);
        
        panelAksi.add(btnClear); panelAksi.add(btnTambah); panelAksi.add(btnUbah); panelAksi.add(btnHapus);
        
        panelBawah.add(panelForm, BorderLayout.CENTER);
        panelBawah.add(panelAksi, BorderLayout.SOUTH);

        tableEvent.getSelectionModel().addListSelectionListener(e -> {
            int row = tableEvent.getSelectedRow();
            if (row != -1) {
                txtId.setText(tableModel.getValueAt(row, 0).toString());
                txtId.setEditable(false); // ID jangan diedit kalau update
                txtJudul.setText(tableModel.getValueAt(row, 1).toString());
                txtKuota.setText(tableModel.getValueAt(row, 2).toString());
                txtHarga.setText(tableModel.getValueAt(row, 3).toString());
            }
        });

        btnClear.addActionListener(e -> {
            txtId.setText(""); txtId.setEditable(true);
            txtJudul.setText(""); txtKuota.setText(""); txtHarga.setText("");
            tableEvent.clearSelection();
        });

        btnTambah.addActionListener(e -> {
            try (Connection conn = DatabaseConfig.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO events (event_id, title, quota, price) VALUES (?, ?, ?, ?)");
                ps.setString(1, txtId.getText());
                ps.setString(2, txtJudul.getText());
                ps.setInt(3, Integer.parseInt(txtKuota.getText()));
                ps.setDouble(4, Double.parseDouble(txtHarga.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(adminFrame, "Event Berhasil Ditambahkan!");
                loadDataToTable(tableModel);
                btnClear.doClick();
            } catch (Exception ex) { JOptionPane.showMessageDialog(adminFrame, "Error: " + ex.getMessage()); }
        });

        btnUbah.addActionListener(e -> {
            try (Connection conn = DatabaseConfig.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("UPDATE events SET title=?, quota=?, price=? WHERE event_id=?");
                ps.setString(1, txtJudul.getText());
                ps.setInt(2, Integer.parseInt(txtKuota.getText()));
                ps.setDouble(3, Double.parseDouble(txtHarga.getText()));
                ps.setString(4, txtId.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(adminFrame, "Event Berhasil Diupdate!");
                loadDataToTable(tableModel);
                btnClear.doClick();
            } catch (Exception ex) { JOptionPane.showMessageDialog(adminFrame, "Error: " + ex.getMessage()); }
        });

        btnHapus.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(adminFrame, "Yakin hapus event ini?", "Hapus", JOptionPane.YES_NO_OPTION) == 0) {
                try (Connection conn = DatabaseConfig.getConnection()) {
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM events WHERE event_id=?");
                    ps.setString(1, txtId.getText());
                    ps.executeUpdate();
                    loadDataToTable(tableModel);
                    btnClear.doClick();
                } catch (Exception ex) { JOptionPane.showMessageDialog(adminFrame, "Error: Tidak bisa dihapus karena mungkin ada tiket yang sudah terjual."); }
            }
        });

        adminFrame.add(headerPanel, BorderLayout.NORTH);
        adminFrame.add(scrollPane, BorderLayout.CENTER);
        adminFrame.add(panelBawah, BorderLayout.SOUTH);
        adminFrame.setLocationRelativeTo(null);
        adminFrame.setVisible(true);
    }

    // ==========================================
    // 5. HELPER: LOAD DATA
    // ==========================================
    private void loadDataToTable(DefaultTableModel model) {
        model.setRowCount(0); 
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM events")) {
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("event_id"),
                    rs.getString("title"),
                    rs.getInt("quota"),
                    rs.getDouble("price")
                });
            }
        } catch (SQLException e) {
            System.err.println("Gagal memuat data: " + e.getMessage());
        }
    }
}