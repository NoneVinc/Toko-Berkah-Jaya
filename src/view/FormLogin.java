package view;

import koneksi.Koneksi;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;

public class FormLogin extends javax.swing.JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnKeluar;

    // Warna tema
    private final Color PRIMARY      = new Color(33, 97, 140);
    private final Color PRIMARY_DARK = new Color(21, 67, 96);
    private final Color BG_PAGE      = new Color(244, 247, 251);
    private final Color BG_CARD      = Color.WHITE;
    private final Color TEXT_LABEL   = new Color(74, 85, 104);
    private final Color BORDER_INPUT = new Color(209, 221, 232);
    private final Color RED_BTN      = new Color(192, 57, 43);
    private final Color FOOTER_TEXT  = new Color(160, 174, 192);

    public FormLogin() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Login - Toko Berkah Jaya");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(420, 400);

        // ── Main panel ────────────────────────────────────────
        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(BG_PAGE);

        // ── Header ───────────────────────────────────────────
        JPanel headerPanel = new JPanel(null);
        headerPanel.setBackground(PRIMARY);
        headerPanel.setBounds(0, 0, 420, 110);

        JLabel lblIcon = new JLabel("🏪", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        lblIcon.setBounds(0, 10, 420, 36);

        JLabel lblTitle = new JLabel("TOKO BERKAH JAYA", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 50, 420, 26);

        JLabel lblSub = new JLabel("Sistem Informasi Penjualan", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(180, 210, 240));
        lblSub.setBounds(0, 78, 420, 22);

        headerPanel.add(lblIcon);
        headerPanel.add(lblTitle);
        headerPanel.add(lblSub);

        // ── Card ─────────────────────────────────────────────
        JPanel cardPanel = new JPanel(null);
        cardPanel.setBackground(BG_CARD);
        cardPanel.setBounds(30, 125, 360, 230);
        cardPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 230, 245), 1));

        // Username label
        JLabel lblUser = new JLabel("USERNAME");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblUser.setForeground(TEXT_LABEL);
        lblUser.setBounds(24, 18, 200, 18);

        // Username field
        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsername.setBounds(24, 38, 312, 36);
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_INPUT, 1),
            BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));

        // Password label
        JLabel lblPass = new JLabel("PASSWORD");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPass.setForeground(TEXT_LABEL);
        lblPass.setBounds(24, 86, 200, 18);

        // Password field
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setBounds(24, 106, 312, 36);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_INPUT, 1),
            BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));

        // Tombol Login
        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setBackground(PRIMARY);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBounds(24, 162, 146, 40);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Hover effect
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnLogin.setBackground(PRIMARY_DARK);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnLogin.setBackground(PRIMARY);
            }
        });

        // Tombol Keluar
        btnKeluar = new JButton("Keluar");
        btnKeluar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnKeluar.setBackground(new Color(253, 245, 245));
        btnKeluar.setForeground(RED_BTN);
        btnKeluar.setBounds(190, 162, 146, 40);
        btnKeluar.setBorder(BorderFactory.createLineBorder(new Color(232, 195, 190), 1));
        btnKeluar.setFocusPainted(false);
        btnKeluar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnKeluar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnKeluar.setBackground(new Color(248, 230, 228));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnKeluar.setBackground(new Color(253, 245, 245));
            }
        });

        cardPanel.add(lblUser);
        cardPanel.add(txtUsername);
        cardPanel.add(lblPass);
        cardPanel.add(txtPassword);
        cardPanel.add(btnLogin);
        cardPanel.add(btnKeluar);

        // ── Footer ───────────────────────────────────────────
        JLabel lblFooter = new JLabel("© 2024 Toko Berkah Jaya · All rights reserved",
            SwingConstants.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblFooter.setForeground(FOOTER_TEXT);
        lblFooter.setBounds(0, 370, 420, 20);

        mainPanel.add(headerPanel);
        mainPanel.add(cardPanel);
        mainPanel.add(lblFooter);
        setContentPane(mainPanel);

        // ── Action Listeners ──────────────────────────────────
        btnLogin.addActionListener(e -> btnLoginActionPerformed());
        btnKeluar.addActionListener(e -> btnKeluarActionPerformed());
        getRootPane().setDefaultButton(btnLogin); // Enter = Login
    }

private void btnLoginActionPerformed() {
    String username = txtUsername.getText().trim();
    String password = new String(txtPassword.getPassword()).trim();

    if (username.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Username dan Password tidak boleh kosong!",
            "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Disable tombol saat proses login
    btnLogin.setEnabled(false);
    btnLogin.setText("Loading...");

    // Jalankan di background thread agar UI tidak freeze
    SwingWorker<String[], Void> worker = new SwingWorker<>() {
        @Override
        protected String[] doInBackground() throws Exception {
            Connection conn = Koneksi.getConnection();
            String sql = "SELECT * FROM tb_user WHERE username = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new String[]{
                    rs.getString("nama_lengkap"),
                    rs.getString("level")
                };
            }
            return null;
        }

        @Override
        protected void done() {
            btnLogin.setEnabled(true);
            btnLogin.setText("Login");
            try {
                String[] hasil = get();
                if (hasil != null) {
                    JOptionPane.showMessageDialog(null,
                        "Selamat datang, " + hasil[0] + "!\nLevel: " + hasil[1],
                        "Login Berhasil", JOptionPane.INFORMATION_MESSAGE);
                    FormUtama formUtama = new FormUtama(hasil[0], hasil[1]);
                    formUtama.setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null,
                        "Username atau Password salah!",
                        "Login Gagal", JOptionPane.ERROR_MESSAGE);
                    txtPassword.setText("");
                    txtUsername.requestFocus();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    };
    worker.execute();
}

    private void btnKeluarActionPerformed() {
        int konfirmasi = JOptionPane.showConfirmDialog(this,
            "Yakin ingin keluar?", "Konfirmasi",
            JOptionPane.YES_NO_OPTION);
        if (konfirmasi == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
