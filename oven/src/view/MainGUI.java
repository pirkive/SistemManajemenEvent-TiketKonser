package view;

import database.DatabaseConfig;
import model.Event;
import model.User;
import service.TransactionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MainGUI {

    // --- WARNA TEMA MODERN (ALA TIKET.COM) ---
    private final Color blueColor = new Color(0, 100, 210);      // Biru utama
    private final Color yellowColor = new Color(253, 233, 0);    // Kuning aksen
    private final Color bgGrayColor = new Color(244, 245, 247);  // Abu-abu background web
    private final Color cardColor = Color.WHITE;                 // Putih untuk kartu
    private final Color textDark = new Color(30, 30, 30);
    private final Color textMuted = new Color(120, 120, 120);

    // --- FONT MODERN ---
    private final Font fontLogo = new Font("Segoe UI", Font.BOLD, 26);
    private final Font fontTitle = new Font("Segoe UI", Font.BOLD, 20);
    private final Font fontNormal = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font fontBold = new Font("Segoe UI", Font.BOLD, 14);

    private User activeUser;
    private TransactionService transactionService = new TransactionService();

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new MainGUI().showLoginWindow());
    }

    // --- HELPER COMPONENT: KARTU (CARD PANEL) ---
    private JPanel createCardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        return panel;
    }

    // --- HELPER COMPONENT: TOMBOL ---
    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(fontBold);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleSidebarButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(textDark);
        btn.setFont(fontNormal);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // ==========================================
    // 1. WINDOW LOGIN 
    // ==========================================
    public void showLoginWindow() {
        JFrame loginFrame = new JFrame("E-Ticket System");
        loginFrame.setSize(500, 450);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.getContentPane().setBackground(bgGrayColor);
        loginFrame.setLayout(new GridBagLayout()); 

        JPanel cardLayout = createCardPanel();
        cardLayout.setLayout(new BoxLayout(cardLayout, BoxLayout.Y_AXIS));

        // Logo
        JLabel lblLogo = new JLabel("ngonser yuk!");
        lblLogo.setFont(fontLogo);
        lblLogo.setForeground(blueColor);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSub = new JLabel("top 1 ticketing app di Indonesia! ");
        lblSub.setFont(fontNormal);
        lblSub.setForeground(textMuted);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 0, 5));
        formPanel.setBackground(cardColor);
        formPanel.setMaximumSize(new Dimension(350, 150));
        
        JTextField txtUser = new JTextField(); txtUser.setFont(fontNormal);
        JPasswordField txtPass = new JPasswordField(); txtPass.setFont(fontNormal);
        
        formPanel.add(new JLabel("Username"));
        formPanel.add(txtUser);
        formPanel.add(new JLabel("Password"));
        formPanel.add(txtPass);

        // Buttons
        JButton btnLogin = new JButton("Masuk Sekarang");
        styleButton(btnLogin, blueColor, Color.WHITE);
        btnLogin.setMaximumSize(new Dimension(350, 40));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnRegister = new JButton("Belum punya akun? Daftar");
        styleButton(btnRegister, cardColor, blueColor);
        btnRegister.setMaximumSize(new Dimension(350, 40));
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Susun ke Card
        cardLayout.add(Box.createVerticalStrut(10));
        cardLayout.add(lblLogo);
        cardLayout.add(lblSub);
        cardLayout.add(Box.createVerticalStrut(30));
        cardLayout.add(formPanel);
        cardLayout.add(Box.createVerticalStrut(20));
        cardLayout.add(btnLogin);
        cardLayout.add(Box.createVerticalStrut(10));
        cardLayout.add(btnRegister);
        cardLayout.add(Box.createVerticalStrut(10));

        loginFrame.add(cardLayout); 

        // Action Login
        btnLogin.addActionListener(e -> {
            try (Connection conn = DatabaseConfig.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
                ps.setString(1, txtUser.getText());
                ps.setString(2, new String(txtPass.getPassword()));
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
                    JOptionPane.showMessageDialog(loginFrame, "Login Gagal! Periksa username/password.");
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
    // 2. WINDOW REGISTER 
    // ==========================================
    public void showRegisterWindow() {
        JFrame regFrame = new JFrame("Daftar Akun - E-Ticket");
        regFrame.setSize(500, 500);
        regFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        regFrame.getContentPane().setBackground(bgGrayColor);
        regFrame.setLayout(new GridBagLayout());

        JPanel cardLayout = createCardPanel();
        cardLayout.setLayout(new BoxLayout(cardLayout, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Daftar Akun Baru");
        lblTitle.setFont(fontTitle);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel formPanel = new JPanel(new GridLayout(6, 1, 0, 5));
        formPanel.setBackground(cardColor);
        formPanel.setMaximumSize(new Dimension(350, 200));
        
        JTextField txtUser = new JTextField(); txtUser.setFont(fontNormal);
        JPasswordField txtPass = new JPasswordField(); txtPass.setFont(fontNormal);
        JPasswordField txtPass2 = new JPasswordField(); txtPass2.setFont(fontNormal);
        
        formPanel.add(new JLabel("Username")); formPanel.add(txtUser);
        formPanel.add(new JLabel("Password")); formPanel.add(txtPass);
        formPanel.add(new JLabel("Konfirmasi Password")); formPanel.add(txtPass2);

        JButton btnSimpan = new JButton("Buat Akun");
        styleButton(btnSimpan, yellowColor, textDark); 
        btnSimpan.setMaximumSize(new Dimension(350, 40));
        btnSimpan.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnKembali = new JButton("Kembali ke Login");
        styleButton(btnKembali, cardColor, blueColor);
        btnKembali.setMaximumSize(new Dimension(350, 40));
        btnKembali.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardLayout.add(Box.createVerticalStrut(10));
        cardLayout.add(lblTitle);
        cardLayout.add(Box.createVerticalStrut(20));
        cardLayout.add(formPanel);
        cardLayout.add(Box.createVerticalStrut(20));
        cardLayout.add(btnSimpan);
        cardLayout.add(Box.createVerticalStrut(5));
        cardLayout.add(btnKembali);

        regFrame.add(cardLayout);

        btnKembali.addActionListener(e -> {
            regFrame.dispose();
            showLoginWindow();
        });

        btnSimpan.addActionListener(e -> {
            String u = txtUser.getText(), p = new String(txtPass.getPassword()), p2 = new String(txtPass2.getPassword());
            if(u.isEmpty()||p.isEmpty()) return;
            if(!p.equals(p2)) { JOptionPane.showMessageDialog(regFrame, "Password tidak sama!"); return; }

            try (Connection conn = DatabaseConfig.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username, password, role) VALUES (?, ?, 'Pembeli')");
                ps.setString(1, u); ps.setString(2, p); ps.executeUpdate();
                JOptionPane.showMessageDialog(regFrame, "Sukses! Silakan login.");
                btnKembali.doClick();
            } catch (Exception ex) { JOptionPane.showMessageDialog(regFrame, "Error: " + ex.getMessage()); }
        });

        regFrame.setLocationRelativeTo(null);
        regFrame.setVisible(true);
    }

    // ==========================================
    // 3. DASHBOARD PEMBELI 
    // ==========================================
    public void showDashboardPembeli() {
        JFrame frame = new JFrame("Dashboard - ngonser yuk!");
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(bgGrayColor);

        // --- TOP BAR ---
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(new EmptyBorder(15, 30, 15, 30));
        
        JLabel brand = new JLabel("ngonser yuk!");
        brand.setFont(fontLogo); brand.setForeground(blueColor);
        topBar.add(brand, BorderLayout.WEST);

        // --- SIDEBAR KIRI ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Profil Area
        JLabel lblProfileName = new JLabel(activeUser.getUsername());
        lblProfileName.setFont(fontTitle);
        JLabel lblRole = new JLabel("Tipe Akun: " + activeUser.getRole());
        lblRole.setFont(fontNormal); lblRole.setForeground(textMuted);
        
        JButton btnMenu1 = new JButton("Event Konser"); styleSidebarButton(btnMenu1); btnMenu1.setForeground(blueColor);
        JButton btnMenu2 = new JButton("My Order (Coming Soon)"); styleSidebarButton(btnMenu2);
        JButton btnLogout = new JButton("Keluar"); styleSidebarButton(btnLogout); btnLogout.setForeground(Color.RED);

        sidebar.add(lblProfileName);
        sidebar.add(lblRole);
        sidebar.add(Box.createVerticalStrut(30));
        sidebar.add(btnMenu1);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnMenu2);
        sidebar.add(Box.createVerticalGlue()); 
        sidebar.add(btnLogout);

        btnLogout.addActionListener(e -> { frame.dispose(); showLoginWindow(); });

        // --- KONTEN UTAMA (TENGAH) ---
        JPanel mainContent = new JPanel(new BorderLayout(0, 20));
        mainContent.setBackground(bgGrayColor);
        mainContent.setBorder(new EmptyBorder(20, 20, 20, 30));

        // Card Tabel
        JPanel tableCard = createCardPanel();
        JLabel lblCardTitle = new JLabel("Pilih Event Tersedia");
        lblCardTitle.setFont(fontTitle);
        tableCard.add(lblCardTitle, BorderLayout.NORTH);

        String[] cols = {"ID", "Nama Konser", "Stok Tersedia", "Harga"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        
        table.setFont(fontNormal);
        table.setRowHeight(40);
        table.setShowGrid(false); 
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(232, 240, 254)); 
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setFont(fontBold);
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setBorder(new LineBorder(new Color(230,230,230)));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        scroll.getViewport().setBackground(Color.WHITE);
        tableCard.add(scroll, BorderLayout.CENTER);

        loadDataToTable(model);

        // Card Form Beli (Bawah)
        JPanel buyCard = createCardPanel();
        buyCard.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        
        JTextField txtId = new JTextField(8); txtId.setFont(fontNormal);
        JTextField txtQty = new JTextField(3); txtQty.setFont(fontNormal);
        JButton btnBeli = new JButton("Proses Pembayaran");
        styleButton(btnBeli, yellowColor, textDark);

        buyCard.add(new JLabel("Pilih dari tabel atau ketik ID:"));
        buyCard.add(txtId);
        buyCard.add(new JLabel("Jumlah Beli:"));
        buyCard.add(txtQty);
        buyCard.add(btnBeli);

        // Interaksi Tabel -> Form
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) { txtId.setText(model.getValueAt(row, 0).toString()); txtQty.setText("1"); }
        });

        // Aksi Beli
        btnBeli.addActionListener(e -> {
            try {
                int qty = Integer.parseInt(txtQty.getText());
                try (Connection conn = DatabaseConfig.getConnection()) {
                    PreparedStatement ps = conn.prepareStatement("SELECT * FROM events WHERE event_id = ?");
                    ps.setString(1, txtId.getText());
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        Event evt = new Event(rs.getString("event_id"), rs.getString("title"), rs.getInt("quota"), rs.getDouble("price"));
                        if (transactionService.prosesPembayaran(activeUser, evt, qty)) {
                            // --- FITUR STRUK (PERBAIKAN FORMAT ANGKA) ---
                            double totalHarga = evt.getPrice() * qty;
                            String struk = String.format(
                                "====================================\n" +
                                "          TIKET.APP RECEIPT         \n" +
                                "====================================\n" +
                                "Event : %s\n" +
                                "Total : Rp %,.0f\n" +
                                "====================================\n" +
                                "Lunas! Cek tiket di lokasi acara.",
                                evt.getTitle(), totalHarga
                            );
                            
                            JTextArea textArea = new JTextArea(struk);
                            textArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); 
                            textArea.setEditable(false);
                            textArea.setBackground(new Color(245, 245, 245));
                            
                            JOptionPane.showMessageDialog(frame, textArea, "Pembayaran Sukses", JOptionPane.INFORMATION_MESSAGE);
                            loadDataToTable(model);
                        } else { JOptionPane.showMessageDialog(frame, "Stok tidak cukup!"); }
                    } else { JOptionPane.showMessageDialog(frame, "Event tidak ditemukan!"); }
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(frame, "Masukan tidak valid!"); }
        });

        mainContent.add(tableCard, BorderLayout.CENTER);
        mainContent.add(buyCard, BorderLayout.SOUTH);

        frame.add(topBar, BorderLayout.NORTH);
        frame.add(sidebar, BorderLayout.WEST);
        frame.add(mainContent, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // ==========================================
    // 4. DASHBOARD ADMIN
    // ==========================================
    public void showAdminDashboard() {
        JFrame frame = new JFrame("Admin Panel - tiket.app");
        frame.setSize(1000, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(bgGrayColor);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(30, 40, 50)); 
        topBar.setBorder(new EmptyBorder(15, 30, 15, 30));
        JLabel brand = new JLabel("Admin Control Panel");
        brand.setFont(fontLogo); brand.setForeground(Color.WHITE);
        topBar.add(brand, BorderLayout.WEST);

        JButton btnLogout = new JButton("Logout System");
        styleButton(btnLogout, dangerColor(), Color.WHITE);
        btnLogout.addActionListener(e -> { frame.dispose(); showLoginWindow(); });
        topBar.add(btnLogout, BorderLayout.EAST);

        JPanel mainContent = new JPanel(new BorderLayout(0, 20));
        mainContent.setBackground(bgGrayColor);
        mainContent.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel tableCard = createCardPanel();
        String[] cols = {"ID", "Nama Konser", "Stok", "Harga Dasar"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        
        table.setFont(fontNormal); table.setRowHeight(35); table.setShowGrid(false);
        table.setSelectionBackground(new Color(230, 230, 230));
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        tableCard.add(scroll, BorderLayout.CENTER);
        loadDataToTable(model);

        JPanel formCard = createCardPanel();
        formCard.setLayout(new BorderLayout(0, 15));
        formCard.add(new JLabel("Input / Update Data Event"), BorderLayout.NORTH);

        JPanel inputGrid = new JPanel(new GridLayout(2, 4, 15, 10));
        inputGrid.setBackground(Color.WHITE);
        JTextField tId = new JTextField(); JTextField tJudul = new JTextField();
        JTextField tKuota = new JTextField(); JTextField tHarga = new JTextField();
        
        inputGrid.add(new JLabel("ID Event")); inputGrid.add(tId);
        inputGrid.add(new JLabel("Nama Konser")); inputGrid.add(tJudul);
        inputGrid.add(new JLabel("Kuota")); inputGrid.add(tKuota);
        inputGrid.add(new JLabel("Harga (Rp)")); inputGrid.add(tHarga);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        JButton bAdd = new JButton("Tambah Data"); styleButton(bAdd, blueColor, Color.WHITE);
        JButton bUpd = new JButton("Update Data"); styleButton(bUpd, yellowColor, textDark);
        JButton bDel = new JButton("Hapus Data"); styleButton(bDel, dangerColor(), Color.WHITE);
        btnPanel.add(bAdd); btnPanel.add(bUpd); btnPanel.add(bDel);

        formCard.add(inputGrid, BorderLayout.CENTER);
        formCard.add(btnPanel, BorderLayout.SOUTH);

        // Aksi Admin
        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            if(r != -1) {
                tId.setText(model.getValueAt(r,0).toString());
                tJudul.setText(model.getValueAt(r,1).toString());
                tKuota.setText(model.getValueAt(r,2).toString());
                tHarga.setText(model.getValueAt(r,3).toString());
            }
        });

        bAdd.addActionListener(e -> executeAdminQuery("INSERT INTO events (title, quota, price, event_id) VALUES (?, ?, ?, ?)", tJudul, tKuota, tHarga, tId, model, frame, "ditambah"));
        bUpd.addActionListener(e -> executeAdminQuery("UPDATE events SET title=?, quota=?, price=? WHERE event_id=?", tJudul, tKuota, tHarga, tId, model, frame, "diupdate"));
        bDel.addActionListener(e -> {
            try(Connection c = DatabaseConfig.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM events WHERE event_id=?")) {
                p.setString(1, tId.getText()); p.executeUpdate(); loadDataToTable(model);
            } catch(Exception ex) {}
        });

        mainContent.add(tableCard, BorderLayout.CENTER);
        mainContent.add(formCard, BorderLayout.SOUTH);

        frame.add(topBar, BorderLayout.NORTH);
        frame.add(mainContent, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // --- HELPER UNTUK QUERY ADMIN BIAR KODE RAPI ---
    private void executeAdminQuery(String sql, JTextField tJudul, JTextField tKuota, JTextField tHarga, JTextField tId, DefaultTableModel model, JFrame frame, String msg) {
        try(Connection conn = DatabaseConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tJudul.getText());
            ps.setInt(2, Integer.parseInt(tKuota.getText()));
            ps.setDouble(3, Double.parseDouble(tHarga.getText()));
            ps.setString(4, tId.getText());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Data berhasil " + msg);
            loadDataToTable(model);
        } catch (Exception ex) { JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage()); }
    }

    private Color dangerColor() { return new Color(231, 76, 60); }

    // --- LOAD DATA ---
    private void loadDataToTable(DefaultTableModel model) {
        model.setRowCount(0);
        try (Connection conn = DatabaseConfig.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM events")) {
            while (rs.next()) {
                model.addRow(new Object[]{ rs.getString("event_id"), rs.getString("title"), rs.getInt("quota"), rs.getDouble("price") });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}