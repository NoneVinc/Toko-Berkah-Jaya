package view;

import koneksi.Koneksi;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.*;

public class FormUtama extends javax.swing.JFrame {

    private final Color SIDEBAR_BG    = new Color(26, 58, 92);
    private final Color SIDEBAR_DARK  = new Color(15, 40, 70);
    private final Color SIDEBAR_ACTIVE= new Color(255, 255, 255, 25);
    private final Color SIDEBAR_HOVER = new Color(255, 255, 255, 15);
    private final Color ACCENT        = new Color(93, 173, 226);
    private final Color MAIN_BG       = new Color(240, 244, 248);
    private final Color CARD_BG       = Color.WHITE;
    private final Color TEXT_PRIMARY  = new Color(26, 58, 92);
    private final Color TEXT_MUTED    = new Color(113, 128, 150);
    private final Color BORDER_COLOR  = new Color(226, 232, 240);

    private String  namaUser     = "Administrator";
    private String  levelUser    = "Admin";
    private JButton activeNavBtn = null;

    private JPanel     contentArea;
    private CardLayout cardLayout;
    private JLabel     lblNamaUser;
    private JLabel     lblLevelUser;

    public FormUtama() { this("Administrator", "Admin"); }

    public FormUtama(String nama, String level) {
        this.namaUser  = nama;
        this.levelUser = level;
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Toko Berkah Jaya - Menu Utama");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 600);
        setMinimumSize(new Dimension(800, 500));
        JPanel root = new JPanel(new BorderLayout());
        root.add(buildSidebar(),  BorderLayout.WEST);
        root.add(buildMainArea(), BorderLayout.CENTER);
        setContentPane(root);
    }

    // ═══════════════════════════════════════════════
    //  SIDEBAR
    // ═══════════════════════════════════════════════
    private JPanel buildSidebar() {
        // Sidebar pakai BorderLayout agar footer bisa pin ke bawah
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(210, 0));

        // Brand
        JPanel brand = new JPanel(null);
        brand.setBackground(SIDEBAR_DARK);
        brand.setPreferredSize(new Dimension(210, 70));

        JLabel lblBrand = new JLabel("TOKO BERKAH JAYA");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setBounds(14, 14, 182, 18);

        JLabel lblSub = new JLabel("Sistem Informasi Penjualan");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(new Color(150, 190, 220));
        lblSub.setBounds(14, 34, 182, 16);

        brand.add(lblBrand);
        brand.add(lblSub);

        // Nav
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(SIDEBAR_BG);
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        navPanel.add(navSection("MENU"));
        JButton btnDashboard = navBtn("Dashboard", "DASHBOARD");
        navPanel.add(btnDashboard);

        navPanel.add(navSection("MASTER DATA"));
        navPanel.add(navBtn("Data Barang",   "BARANG"));
        navPanel.add(navBtn("Data Customer", "CUSTOMER"));
        navPanel.add(navBtn("Kelola Stok",   "STOK"));

        navPanel.add(navSection("TRANSAKSI"));
        navPanel.add(navBtn("Penjualan", "PENJUALAN"));

        // Laporan hanya untuk Admin
        if (levelUser.equalsIgnoreCase("Admin")) {
            navPanel.add(navSection("LAPORAN"));
            navPanel.add(navBtn("Laporan Penjualan", "LAPORAN"));
        }

        setActiveNav(btnDashboard);

        // User footer — pin ke bawah dengan BorderLayout.SOUTH
        JPanel userPanel = new JPanel(null);
        userPanel.setBackground(SIDEBAR_DARK);
        userPanel.setPreferredSize(new Dimension(210, 60));
        userPanel.setBorder(BorderFactory.createMatteBorder(
            1, 0, 0, 0, new Color(40, 70, 110)));

        JLabel avatar = new JLabel(
            String.valueOf(namaUser.charAt(0)).toUpperCase(),
            SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        avatar.setForeground(Color.WHITE);
        avatar.setBackground(new Color(46, 134, 193));
        avatar.setOpaque(true);
        avatar.setBounds(12, 13, 34, 34);
        avatar.setBorder(BorderFactory.createLineBorder(ACCENT, 1));

        lblNamaUser = new JLabel(namaUser);
        lblNamaUser.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblNamaUser.setForeground(Color.WHITE);
        lblNamaUser.setBounds(54, 12, 110, 16);

        lblLevelUser = new JLabel(levelUser);
        lblLevelUser.setFont(new Font("Segoe UI", Font.BOLD, 9));
        // Admin = hijau, Petugas = biru muda
        lblLevelUser.setForeground(levelUser.equalsIgnoreCase("Admin")
            ? new Color(100, 220, 150) : new Color(150, 210, 255));
        lblLevelUser.setBounds(54, 30, 110, 14);

        JButton btnLogout = new JButton("\u23FB");
        btnLogout.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        btnLogout.setBounds(172, 18, 28, 26);
        btnLogout.setBorderPainted(false);
        btnLogout.setBackground(SIDEBAR_DARK);
        btnLogout.setForeground(new Color(150, 190, 220));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setToolTipText("Logout");
        btnLogout.addActionListener(e -> doLogout());

        userPanel.add(avatar);
        userPanel.add(lblNamaUser);
        userPanel.add(lblLevelUser);
        userPanel.add(btnLogout);

        // Rakit sidebar
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(SIDEBAR_BG);
        top.add(brand,    BorderLayout.NORTH);
        top.add(navPanel, BorderLayout.CENTER);

        sidebar.add(top,       BorderLayout.CENTER);
        sidebar.add(userPanel, BorderLayout.SOUTH); // selalu di pojok bawah

        return sidebar;
    }

    private JLabel navSection(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(new Color(120, 150, 175));
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 16, 4, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JButton navBtn(String text, String card) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(new Color(200, 220, 240));
        btn.setBackground(SIDEBAR_BG);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setPreferredSize(new Dimension(210, 36));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, SIDEBAR_BG),
            BorderFactory.createEmptyBorder(0, 14, 0, 0)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != activeNavBtn) btn.setBackground(SIDEBAR_HOVER);
            }
            public void mouseExited(MouseEvent e) {
                if (btn != activeNavBtn) {
                    btn.setBackground(SIDEBAR_BG);
                    btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 3, 0, 0, SIDEBAR_BG),
                        BorderFactory.createEmptyBorder(0, 14, 0, 0)));
                }
            }
        });

        btn.addActionListener(e -> {
            setActiveNav(btn);
            cardLayout.show(contentArea, card);
        });

        return btn;
    }

    private void setActiveNav(JButton btn) {
        if (activeNavBtn != null) {
            activeNavBtn.setBackground(SIDEBAR_BG);
            activeNavBtn.setForeground(new Color(200, 220, 240));
            activeNavBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, SIDEBAR_BG),
                BorderFactory.createEmptyBorder(0, 14, 0, 0)));
        }
        activeNavBtn = btn;
        btn.setBackground(SIDEBAR_ACTIVE);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, ACCENT),
            BorderFactory.createEmptyBorder(0, 14, 0, 0)));
    }

    // ═══════════════════════════════════════════════
    //  MAIN AREA
    // ═══════════════════════════════════════════════
    private JPanel buildMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(MAIN_BG);

        JPanel topbar = new JPanel(null);
        topbar.setBackground(CARD_BG);
        topbar.setPreferredSize(new Dimension(0, 52));
        topbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        JLabel lblPageTitle = new JLabel("Dashboard");
        lblPageTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblPageTitle.setForeground(TEXT_PRIMARY);
        lblPageTitle.setBounds(20, 14, 300, 24);

        String tgl = new SimpleDateFormat("EEEE, dd MMMM yyyy",
            new java.util.Locale("id", "ID")).format(new Date());
        JLabel lblDate = new JLabel("  " + tgl);
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDate.setForeground(TEXT_MUTED);
        lblDate.setBounds(560, 16, 350, 20);
        lblDate.setHorizontalAlignment(SwingConstants.RIGHT);

        topbar.add(lblPageTitle);
        topbar.add(lblDate);

        cardLayout  = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(MAIN_BG);

        contentArea.add(buildDashboardPanel(), "DASHBOARD");
        contentArea.add(new PanelBarang(),     "BARANG");
        contentArea.add(new PanelCustomer(),   "CUSTOMER");
        contentArea.add(new PanelStok(namaUser, getUserId()), "STOK");
        contentArea.add(new PanelPenjualan(namaUser, getUserId()), "PENJUALAN");
        contentArea.add(new PanelLaporan(),    "LAPORAN");

        main.add(topbar,      BorderLayout.NORTH);
        main.add(contentArea, BorderLayout.CENTER);
        return main;
    }

    // ═══════════════════════════════════════════════
    //  DASHBOARD
    // ═══════════════════════════════════════════════
    private JPanel buildDashboardPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(MAIN_BG);

        String[] labels  = {"Total Barang", "Total Customer", "Transaksi Hari Ini", "Pendapatan Hari Ini"};
        String[] icons   = {"[Brg]", "[Cust]", "[Trx]", "[Rp]"};
        String[] queries = {
            "SELECT COUNT(*) FROM tb_barang",
            "SELECT COUNT(*) FROM tb_customer",
            "SELECT COUNT(*) FROM tb_penjualan WHERE DATE(tgl_transaksi) = CURDATE()",
            "SELECT COALESCE(SUM(total_bayar),0) FROM tb_penjualan WHERE DATE(tgl_transaksi) = CURDATE()"
        };
        String[] defaults = {"0", "0", "0", "Rp 0"};

        int cardW = 158;
        for (int i = 0; i < 4; i++) {
            String val = fetchStat(queries[i], defaults[i], i == 3);
            panel.add(buildStatCard(16 + i * (cardW + 12), 16, cardW, 90,
                labels[i], val, i == 3));
        }

        // Welcome card
        JPanel welcome = new JPanel(null);
        welcome.setBackground(CARD_BG);
        welcome.setBounds(16, 122, 718, 130);
        welcome.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        JLabel wTitle = new JLabel("Selamat datang, " + namaUser + "!");
        wTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        wTitle.setForeground(TEXT_PRIMARY);
        wTitle.setBounds(16, 16, 660, 22);

        String descText = levelUser.equalsIgnoreCase("Admin")
            ? "<html>Gunakan menu di sebelah kiri untuk mengelola data <b>Barang</b>, <b>Customer</b>, <b>Transaksi Penjualan</b>, dan melihat <b>Laporan</b>.</html>"
            : "<html>Gunakan menu di sebelah kiri untuk mengelola data <b>Barang</b>, <b>Customer</b>, dan <b>Transaksi Penjualan</b>.</html>";
        JLabel wDesc = new JLabel(descText);
        wDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        wDesc.setForeground(TEXT_MUTED);
        wDesc.setBounds(16, 44, 660, 40);

        // Quick buttons — Laporan hanya Admin
        boolean isAdmin = levelUser.equalsIgnoreCase("Admin");
        String[][] quickBtns = isAdmin
            ? new String[][]{{"Tambah Barang","BARANG"},{"Transaksi Baru","PENJUALAN"},{"Lihat Laporan","LAPORAN"}}
            : new String[][]{{"Tambah Barang","BARANG"},{"Transaksi Baru","PENJUALAN"}};

        for (int i = 0; i < quickBtns.length; i++) {
            final String card    = quickBtns[i][1];
            final String label   = quickBtns[i][0];
            JButton qb = new JButton(label);
            qb.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            qb.setForeground(new Color(41, 128, 185));
            qb.setBackground(new Color(235, 245, 255));
            qb.setBounds(16 + i * 130, 90, 120, 30);
            qb.setBorder(BorderFactory.createLineBorder(new Color(190, 220, 245), 1));
            qb.setFocusPainted(false);
            qb.setCursor(new Cursor(Cursor.HAND_CURSOR));
            qb.addActionListener(e -> {
                // Cari nav button yang cocok lalu aktifkan
                findNavButtonByCard(card);
                cardLayout.show(contentArea, card);
            });
            welcome.add(qb);
        }

        welcome.add(wTitle);
        welcome.add(wDesc);
        panel.add(welcome);
        return panel;
    }

    private JPanel buildStatCard(int x, int y, int w, int h,
            String label, String value, boolean isGreen) {
        JPanel card = new JPanel(null);
        card.setBackground(CARD_BG);
        card.setBounds(x, y, w, h);
        card.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(TEXT_MUTED);
        lbl.setBounds(12, 12, w - 20, 14);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 22));
        val.setForeground(isGreen ? new Color(39, 174, 96) : TEXT_PRIMARY);
        val.setBounds(12, 34, w - 20, 30);

        card.add(lbl);
        card.add(val);
        return card;
    }

    // ═══════════════════════════════════════════════
    //  HELPER
    // ═══════════════════════════════════════════════
    private String fetchStat(String sql, String def, boolean isCurrency) {
        try {
            Connection conn = Koneksi.getConnection();
            if (conn == null) return def;
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (isCurrency) {
                    double val = rs.getDouble(1);
                    return "Rp " + String.format("%,.0f", val).replace(",", ".");
                }
                return String.valueOf(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("fetchStat error: " + e.getMessage());
        }
        return def;
    }

    private int getUserId() {
        try {
            Connection conn = Koneksi.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT id_user FROM tb_user WHERE nama_lengkap = ?");
            ps.setString(1, namaUser);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id_user");
            rs.close(); ps.close();
        } catch (SQLException e) {
            System.err.println("getUserId error: " + e.getMessage());
        }
        return 1;
    }

    // Cari nav button berdasarkan card name lalu aktifkan
    private void findNavButtonByCard(String card) {
        // Cari semua JButton di sidebar dan cocokkan dengan actionCommand
        JPanel root = (JPanel) getContentPane();
        JPanel sidebar = (JPanel) root.getComponent(0);
        searchAndActivate(sidebar, card);
    }

    private void searchAndActivate(Container container, String card) {
        for (Component c : container.getComponents()) {
            if (c instanceof JButton) {
                JButton btn = (JButton) c;
                // Cek apakah action listener btn mengarah ke card ini
                for (java.awt.event.ActionListener al : btn.getActionListeners()) {
                    String btnText = btn.getText().trim();
                    if ((card.equals("BARANG")    && btnText.equals("Data Barang"))   ||
                        (card.equals("CUSTOMER")  && btnText.equals("Data Customer")) ||
                        (card.equals("STOK")      && btnText.equals("Kelola Stok"))   ||
                        (card.equals("PENJUALAN") && btnText.equals("Penjualan"))     ||
                        (card.equals("LAPORAN")   && btnText.equals("Laporan Penjualan"))) {
                        setActiveNav(btn);
                        return;
                    }
                }
            } else if (c instanceof Container) {
                searchAndActivate((Container) c, card);
            }
        }
    }

    private void doLogout() {
        int opt = JOptionPane.showConfirmDialog(this,
            "Yakin ingin logout?", "Konfirmasi Logout",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) {
            Koneksi.closeConnection();
            new FormLogin().setVisible(true);
            this.dispose();
        }
    }
}