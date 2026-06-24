package view;

import koneksi.Koneksi;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/**
 * PanelStok – Manajemen stok barang.
 *
 *  Fitur:
 *   - Tabel semua barang + stok saat ini
 *   - Tambah stok via dialog (input jumlah & keterangan)
 *   - Riwayat penambahan stok hari ini
 *   - Stok otomatis berkurang saat transaksi penjualan dikonfirmasi
 *     (logika pengurangan ada di PanelPenjualan → simpanTransaksi)
 */
public class PanelStok extends JPanel {

    // ── Palet warna (seragam dengan panel lain) ──────────
    private final Color PRIMARY      = new Color(33,  97,  140);
    private final Color DARK         = new Color(26,  58,  92);
    private final Color GREEN        = new Color(39,  174, 96);
    private final Color ORANGE       = new Color(230, 126, 34);
    private final Color RED          = new Color(231, 76,  60);
    private final Color BG           = new Color(240, 244, 248);
    private final Color CARD_BG      = Color.WHITE;
    private final Color BORDER_COLOR = new Color(226, 232, 240);
    private final Color TEXT_LABEL   = new Color(74,  85,  104);
    private final Color TEXT_MUTED   = new Color(113, 128, 150);

    private JTable            tblBarang;
    private DefaultTableModel modelBarang;
    private JTable            tblRiwayat;
    private DefaultTableModel modelRiwayat;
    private JTextField        txtCari;

    private String namaUser = "Admin";
    private int    idUser   = 1;

    // ── Konstruktor ──────────────────────────────────────
    public PanelStok() { this("Admin", 1); }

    public PanelStok(String namaUser, int idUser) {
        this.namaUser = namaUser;
        this.idUser   = idUser;
        setLayout(new BorderLayout(0, 10));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        add(buildTopBar(),      BorderLayout.NORTH);
        add(buildMainPanel(),   BorderLayout.CENTER);
        loadBarang("");
        loadRiwayat();
    }

    // ════════════════════════════════════════════════════
    //  TOP BAR
    // ════════════════════════════════════════════════════
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG);

        JLabel title = new JLabel("Manajemen Stok Barang");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(DARK);

        String tgl = new SimpleDateFormat("EEEE, dd MMMM yyyy",
            new Locale("id", "ID")).format(new java.util.Date());
        JLabel lblTgl = new JLabel(tgl);
        lblTgl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTgl.setForeground(TEXT_MUTED);

        bar.add(title,  BorderLayout.WEST);
        bar.add(lblTgl, BorderLayout.EAST);
        return bar;
    }

    // ════════════════════════════════════════════════════
    //  MAIN PANEL (tabel barang + riwayat)
    // ════════════════════════════════════════════════════
    private JPanel buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout(0, 10));
        main.setBackground(BG);
        main.add(buildBarangCard(),  BorderLayout.CENTER);
        main.add(buildRiwayatCard(), BorderLayout.SOUTH);
        return main;
    }

    // ════════════════════════════════════════════════════
    //  CARD: DAFTAR BARANG
    // ════════════════════════════════════════════════════
    private JPanel buildBarangCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        // ── Header + search bar ──────────────────────
        JPanel hdr = new JPanel(new BorderLayout(10, 0));
        hdr.setBackground(DARK);
        hdr.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));

        JLabel lblHdr = new JLabel("Daftar Barang & Stok");
        lblHdr.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblHdr.setForeground(Color.WHITE);

        // Search field
        txtCari = new JTextField();
        txtCari.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtCari.setPreferredSize(new Dimension(200, 26));
        txtCari.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 140, 180), 1),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        txtCari.putClientProperty("JTextField.placeholderText", "Cari barang...");
        txtCari.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { loadBarang(txtCari.getText().trim()); }
        });

        JPanel searchWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        searchWrap.setBackground(DARK);
        JLabel lbSearch = new JLabel("🔍  ");
        lbSearch.setForeground(Color.WHITE);
        searchWrap.add(lbSearch);
        searchWrap.add(txtCari);

        JButton btnRefresh = new JButton("↻");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setBackground(DARK);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.setToolTipText("Refresh data");
        btnRefresh.addActionListener(e -> { loadBarang(txtCari.getText().trim()); loadRiwayat(); });
        searchWrap.add(Box.createHorizontalStrut(6));
        searchWrap.add(btnRefresh);

        hdr.add(lblHdr,      BorderLayout.WEST);
        hdr.add(searchWrap,  BorderLayout.EAST);

        // ── Tabel ────────────────────────────────────
        // Kolom: # | Kode | Nama Barang | Kategori | Harga | Stok | Status | Tambah Stok
        String[] kolom = {"#", "Kode", "Nama Barang", "Harga Satuan", "Stok Saat Ini", "Status", "Aksi"};
        modelBarang = new DefaultTableModel(kolom, 0) {
            public boolean isCellEditable(int r, int c) { return c == 6; }
            public Class<?> getColumnClass(int c) {
                return c == 6 ? JButton.class : Object.class;
            }
        };

        tblBarang = new JTable(modelBarang);
        tblBarang.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblBarang.setRowHeight(34);
        tblBarang.setShowHorizontalLines(true);
        tblBarang.setGridColor(BORDER_COLOR);
        tblBarang.setSelectionBackground(new Color(235, 245, 255));
        tblBarang.setFocusable(false);

        JTableHeader header = tblBarang.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(new Color(44, 80, 115));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 32));
        header.setReorderingAllowed(false);

        int[] widths = {35, 80, 200, 120, 110, 90, 120};
        for (int i = 0; i < widths.length; i++)
            tblBarang.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Renderer berwarna untuk kolom Status & Stok
        tblBarang.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setFont(new Font("Segoe UI", Font.PLAIN, 12));
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

                Color bg = row % 2 == 0 ? Color.WHITE : new Color(247, 250, 252);
                Color fg = new Color(45, 55, 72);

                if (sel) { bg = new Color(235, 245, 255); fg = DARK; }

                if (!sel && col == 4) { // Stok
                    try {
                        int stok = Integer.parseInt(val.toString().replace(",", "").trim());
                        fg = stok == 0 ? RED : (stok <= 5 ? ORANGE : GREEN);
                        setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } catch (NumberFormatException ignored) {}
                }
                if (!sel && col == 5) { // Status badge
                    String status = val != null ? val.toString() : "";
                    fg = "Habis".equals(status) ? RED :
                         "Menipis".equals(status) ? ORANGE : GREEN;
                    setFont(new Font("Segoe UI", Font.BOLD, 11));
                }

                setBackground(bg);
                setForeground(fg);
                return this;
            }
        });

        // Renderer & editor tombol "Tambah Stok"
        tblBarang.getColumn("Aksi").setCellRenderer(new TambahStokRenderer());
        tblBarang.getColumn("Aksi").setCellEditor(new TambahStokEditor(new JCheckBox()));

        JScrollPane scroll = new JScrollPane(tblBarang);
        scroll.setBorder(null);
        scroll.setPreferredSize(new Dimension(0, 280));
        scroll.getViewport().setBackground(Color.WHITE);

        // ── Legenda stok ─────────────────────────────
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 4));
        legend.setBackground(new Color(248, 250, 252));
        legend.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));
        legend.add(buatBadge("● Aman",    GREEN));
        legend.add(buatBadge("● Menipis (≤5)", ORANGE));
        legend.add(buatBadge("● Habis",   RED));

        card.add(hdr,    BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        card.add(legend, BorderLayout.SOUTH);
        return card;
    }

    // ════════════════════════════════════════════════════
    //  CARD: RIWAYAT PENAMBAHAN STOK
    // ════════════════════════════════════════════════════
    private JPanel buildRiwayatCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        card.add(buildCardHeader("Riwayat Penambahan Stok Hari Ini"), BorderLayout.NORTH);

        String[] kolom = {"#", "Waktu", "Kode", "Nama Barang", "Jumlah Ditambah", "Stok Sebelum", "Stok Sesudah", "Keterangan", "Oleh"};
        modelRiwayat = new DefaultTableModel(kolom, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblRiwayat = new JTable(modelRiwayat);
        tblRiwayat.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblRiwayat.setRowHeight(30);
        tblRiwayat.setShowHorizontalLines(true);
        tblRiwayat.setGridColor(BORDER_COLOR);
        tblRiwayat.setSelectionBackground(new Color(235, 245, 255));
        tblRiwayat.setFocusable(false);

        JTableHeader hdr = tblRiwayat.getTableHeader();
        hdr.setFont(new Font("Segoe UI", Font.BOLD, 11));
        hdr.setBackground(new Color(44, 80, 115));
        hdr.setForeground(Color.WHITE);
        hdr.setPreferredSize(new Dimension(0, 32));
        hdr.setReorderingAllowed(false);

        int[] widths = {30, 80, 70, 160, 110, 100, 100, 140, 80};
        for (int i = 0; i < widths.length; i++)
            tblRiwayat.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Warnai kolom Jumlah Ditambah hijau
        tblRiwayat.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setFont(new Font("Segoe UI", Font.PLAIN, 12));
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                Color bg = row % 2 == 0 ? Color.WHITE : new Color(247, 250, 252);
                if (sel) bg = new Color(235, 245, 255);
                setBackground(bg);
                if (!sel && col == 4) {
                    setForeground(GREEN);
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else {
                    setForeground(new Color(45, 55, 72));
                }
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(tblRiwayat);
        scroll.setBorder(null);
        scroll.setPreferredSize(new Dimension(0, 160));
        scroll.getViewport().setBackground(Color.WHITE);

        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    // ════════════════════════════════════════════════════
    //  DIALOG: TAMBAH STOK
    // ════════════════════════════════════════════════════
    private void showDialogTambahStok(int row) {
        // Ambil data dari baris yang dipilih
        String kode      = modelBarang.getValueAt(row, 1).toString();
        String namaBarang= modelBarang.getValueAt(row, 2).toString();
        String stokStr   = modelBarang.getValueAt(row, 4).toString();
        int    stokSaat  = Integer.parseInt(stokStr);

        // Cari id_barang dari DB berdasarkan kode
        String idBarang = kode; // pakai kode sebagai id (sesuaikan jika perlu)

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                                  "Tambah Stok", true);
        dlg.setLayout(new BorderLayout());
        dlg.setResizable(false);

        // Header
        JPanel hdrDlg = new JPanel(new BorderLayout());
        hdrDlg.setBackground(DARK);
        hdrDlg.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        JLabel hdrTitle = new JLabel("📦  Tambah Stok Barang");
        hdrTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        hdrTitle.setForeground(Color.WHITE);
        hdrDlg.add(hdrTitle, BorderLayout.WEST);

        // Body
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(new Color(245, 248, 252));
        body.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));

        // Info barang
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 8, 6));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        infoPanel.add(buatLabelInfo("Kode Barang"));
        infoPanel.add(buatLabelInfo("Nama Barang"));
        JLabel valKode = new JLabel(kode);
        valKode.setFont(new Font("Segoe UI", Font.BOLD, 13));
        valKode.setForeground(PRIMARY);
        JLabel valNama = new JLabel(namaBarang);
        valNama.setFont(new Font("Segoe UI", Font.BOLD, 13));
        valNama.setForeground(DARK);
        infoPanel.add(valKode);
        infoPanel.add(valNama);

        // Stok sekarang
        JPanel stokNowPanel = new JPanel(new BorderLayout());
        stokNowPanel.setBackground(stokSaat == 0 ? new Color(255, 245, 245) :
                                    stokSaat <= 5 ? new Color(255, 248, 230) :
                                    new Color(240, 255, 245));
        stokNowPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(stokSaat == 0 ? RED : stokSaat <= 5 ? ORANGE : GREEN, 1),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        stokNowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel stokNowLbl = new JLabel("Stok Saat Ini");
        stokNowLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        stokNowLbl.setForeground(TEXT_LABEL);
        JLabel stokNowVal = new JLabel(stokSaat + " unit  " +
            (stokSaat == 0 ? "⚠ Stok Habis" : stokSaat <= 5 ? "⚠ Stok Menipis" : "✔ Aman"));
        stokNowVal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        stokNowVal.setForeground(stokSaat == 0 ? RED : stokSaat <= 5 ? ORANGE : GREEN);
        stokNowPanel.add(stokNowLbl, BorderLayout.NORTH);
        stokNowPanel.add(stokNowVal, BorderLayout.CENTER);

        // Input jumlah tambah
        JPanel inputPanel = new JPanel(new GridLayout(2, 1, 0, 6));
        inputPanel.setBackground(new Color(245, 248, 252));
        inputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel lblJml = new JLabel("JUMLAH YANG DITAMBAHKAN");
        lblJml.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblJml.setForeground(TEXT_LABEL);

        SpinnerNumberModel spinModel = new SpinnerNumberModel(1, 1, 99999, 1);
        JSpinner spnTambah = new JSpinner(spinModel);
        spnTambah.setFont(new Font("Segoe UI", Font.BOLD, 15));
        spnTambah.setPreferredSize(new Dimension(0, 38));
        ((JSpinner.DefaultEditor) spnTambah.getEditor())
            .getTextField().setHorizontalAlignment(JTextField.CENTER);

        inputPanel.add(lblJml);
        inputPanel.add(spnTambah);

        // Preview stok sesudah
        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBackground(new Color(235, 248, 255));
        previewPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 180, 230), 1),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        previewPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel previewLbl = new JLabel("Stok Sesudah Ditambah");
        previewLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        previewLbl.setForeground(TEXT_LABEL);
        JLabel previewVal = new JLabel(String.valueOf(stokSaat + 1) + " unit");
        previewVal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        previewVal.setForeground(PRIMARY);
        previewPanel.add(previewLbl, BorderLayout.NORTH);
        previewPanel.add(previewVal, BorderLayout.CENTER);

        // Update preview saat spinner berubah
        spnTambah.addChangeListener(e -> {
            int tambah = (int) spnTambah.getValue();
            previewVal.setText((stokSaat + tambah) + " unit");
        });

        // Input keterangan
        JPanel ketPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        ketPanel.setBackground(new Color(245, 248, 252));
        ketPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JLabel lblKet = new JLabel("KETERANGAN (opsional)");
        lblKet.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblKet.setForeground(TEXT_LABEL);

        JTextField txtKet = new JTextField();
        txtKet.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtKet.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        txtKet.putClientProperty("JTextField.placeholderText", "Contoh: Restock dari supplier, Koreksi stok...");

        ketPanel.add(lblKet);
        ketPanel.add(txtKet);

        body.add(infoPanel);
        body.add(Box.createVerticalStrut(10));
        body.add(stokNowPanel);
        body.add(Box.createVerticalStrut(10));
        body.add(inputPanel);
        body.add(Box.createVerticalStrut(8));
        body.add(previewPanel);
        body.add(Box.createVerticalStrut(10));
        body.add(ketPanel);
        body.add(Box.createVerticalStrut(4));

        // Tombol
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setBackground(new Color(245, 248, 252));
        btnPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        JButton btnSimpan = buatBtn("✅  Simpan Penambahan Stok", GREEN, Color.WHITE, 210);
        JButton btnBatal  = buatBtn("Batal", Color.WHITE, TEXT_MUTED, 80);
        btnBatal.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        btnSimpan.addActionListener(e -> {
            int jumlahTambah = (int) spnTambah.getValue();
            String ket       = txtKet.getText().trim();
            if (ket.isEmpty()) ket = "Penambahan stok";

            // Konfirmasi
            int opt = JOptionPane.showConfirmDialog(dlg,
                String.format("Tambah stok \"%s\" sebanyak %d unit?\n" +
                              "Stok: %d → %d unit",
                    namaBarang, jumlahTambah, stokSaat, stokSaat + jumlahTambah),
                "Konfirmasi", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (opt != JOptionPane.YES_OPTION) return;

            // Simpan ke DB
            if (doTambahStok(idBarang, jumlahTambah, stokSaat, ket)) {
                dlg.dispose();
                loadBarang(txtCari.getText().trim());
                loadRiwayat();
                JOptionPane.showMessageDialog(PanelStok.this,
                    String.format("Stok \"%s\" berhasil ditambah %d unit.\nStok sekarang: %d unit.",
                        namaBarang, jumlahTambah, stokSaat + jumlahTambah),
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        btnBatal.addActionListener(e -> dlg.dispose());

        btnPanel.add(btnSimpan);
        btnPanel.add(btnBatal);

        dlg.add(hdrDlg,   BorderLayout.NORTH);
        dlg.add(body,     BorderLayout.CENTER);
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.pack();
        dlg.setMinimumSize(new Dimension(460, 0));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    // ════════════════════════════════════════════════════
    //  DATABASE OPERATIONS
    // ════════════════════════════════════════════════════

    /** Muat semua barang ke tabel (filter opsional). */
    private void loadBarang(String keyword) {
        modelBarang.setRowCount(0);
        try {
            Connection conn = Koneksi.getConnection();
            String sql = "SELECT id_barang, nama_barang, harga_jual, stok FROM tb_barang " +
                         (keyword.isEmpty() ? "" :
                          "WHERE nama_barang LIKE ? OR id_barang LIKE ? ") +
                         "ORDER BY nama_barang ASC";
            PreparedStatement ps = conn.prepareStatement(sql);
            if (!keyword.isEmpty()) {
                ps.setString(1, "%" + keyword + "%");
                ps.setString(2, "%" + keyword + "%");
            }
            ResultSet rs = ps.executeQuery();
            int no = 1;
            while (rs.next()) {
                int    stok  = rs.getInt("stok");
                String status = stok == 0 ? "Habis" : stok <= 5 ? "Menipis" : "Aman";
                modelBarang.addRow(new Object[]{
                    no++,
                    rs.getString("id_barang"),
                    rs.getString("nama_barang"),
                    "Rp " + formatRupiah(rs.getDouble("harga_jual")),
                    stok,
                    status,
                    "Tambah Stok"
                });
            }
            rs.close(); ps.close();
        } catch (SQLException e) {
            showError("Gagal memuat data barang:\n" + e.getMessage());
        }
    }

    /**
     * Muat riwayat penambahan stok hari ini dari tb_stok_log.
     *
     * Tabel tb_stok_log harus dibuat terlebih dahulu (lihat komentar di bawah).
     * Jika tabel belum ada, baris riwayat akan kosong tanpa error.
     */
    private void loadRiwayat() {
        modelRiwayat.setRowCount(0);
        try {
            Connection conn = Koneksi.getConnection();
            // Cek apakah tabel log sudah ada
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables(null, null, "tb_stok_log", null);
            if (!tables.next()) {
                tables.close();
                return; // tabel belum dibuat, lewati
            }
            tables.close();

            String sql =
                "SELECT sl.waktu, b.id_barang, b.nama_barang, sl.jumlah_tambah, " +
                "sl.stok_sebelum, sl.stok_sesudah, sl.keterangan, u.nama_lengkap " +
                "FROM tb_stok_log sl " +
                "JOIN tb_barang b ON sl.id_barang  = b.id_barang " +
                "JOIN tb_user   u ON sl.id_user    = u.id_user " +
                "WHERE DATE(sl.waktu) = CURDATE() " +
                "ORDER BY sl.id_log DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            int no = 1;
            while (rs.next()) {
                modelRiwayat.addRow(new Object[]{
                    no++,
                    new SimpleDateFormat("HH:mm:ss").format(rs.getTimestamp("waktu")),
                    rs.getString("id_barang"),
                    rs.getString("nama_barang"),
                    "+" + rs.getInt("jumlah_tambah"),
                    rs.getInt("stok_sebelum"),
                    rs.getInt("stok_sesudah"),
                    rs.getString("keterangan"),
                    rs.getString("nama_lengkap")
                });
            }
            rs.close(); ps.close();
        } catch (SQLException e) {
            // Tabel log ada tapi query gagal
            showError("Gagal memuat riwayat stok:\n" + e.getMessage());
        }
    }

    /**
     * Simpan penambahan stok ke DB.
     *
     * 1) UPDATE stok di tb_barang
     * 2) INSERT log ke tb_stok_log
     *
     * ── DDL tb_stok_log (jalankan sekali di MySQL) ──────
     *
     * CREATE TABLE IF NOT EXISTS tb_stok_log (
     *   id_log       INT AUTO_INCREMENT PRIMARY KEY,
     *   waktu        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
     *   id_barang    VARCHAR(20) NOT NULL,
     *   id_user      INT         NOT NULL,
     *   jumlah_tambah INT        NOT NULL,
     *   stok_sebelum  INT        NOT NULL,
     *   stok_sesudah  INT        NOT NULL,
     *   keterangan   VARCHAR(200),
     *   FOREIGN KEY (id_barang) REFERENCES tb_barang(id_barang),
     *   FOREIGN KEY (id_user)   REFERENCES tb_user(id_user)
     * );
     */
    private boolean doTambahStok(String idBarang, int jumlahTambah, int stokSebelum, String ket) {
        Connection conn = null;
        try {
            conn = Koneksi.getConnection();
            conn.setAutoCommit(false);

            // 1. Update stok barang
            PreparedStatement psUpdate = conn.prepareStatement(
                "UPDATE tb_barang SET stok = stok + ? WHERE id_barang = ?");
            psUpdate.setInt(1, jumlahTambah);
            psUpdate.setString(2, idBarang);
            psUpdate.executeUpdate();
            psUpdate.close();

            // 2. Catat ke log (jika tabel sudah ada)
            try {
                PreparedStatement psLog = conn.prepareStatement(
                    "INSERT INTO tb_stok_log " +
                    "(id_barang, id_user, jumlah_tambah, stok_sebelum, stok_sesudah, keterangan) " +
                    "VALUES (?, ?, ?, ?, ?, ?)");
                psLog.setString(1, idBarang);
                psLog.setInt(2,    idUser);
                psLog.setInt(3,    jumlahTambah);
                psLog.setInt(4,    stokSebelum);
                psLog.setInt(5,    stokSebelum + jumlahTambah);
                psLog.setString(6, ket);
                psLog.executeUpdate();
                psLog.close();
            } catch (SQLException logEx) {
                // Tabel log belum dibuat → skip logging, transaksi tetap jalan
                System.out.println("[INFO] tb_stok_log belum ada, log dilewati: " + logEx.getMessage());
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            showError("Gagal menambah stok:\n" + e.getMessage());
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }

    // ════════════════════════════════════════════════════
    //  RENDERER & EDITOR TOMBOL "TAMBAH STOK"
    // ════════════════════════════════════════════════════
    class TambahStokRenderer extends JButton implements TableCellRenderer {
        public TambahStokRenderer() {
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setBackground(new Color(39, 174, 96));
            setForeground(Color.WHITE);
            setBorderPainted(false);
            setFocusPainted(false);
        }
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean foc, int row, int col) {
            setText("+ Tambah Stok");
            return this;
        }
    }

    class TambahStokEditor extends DefaultCellEditor {
        private JButton btn;
        private int     editRow;
        public TambahStokEditor(JCheckBox cb) {
            super(cb);
            btn = new JButton("+ Tambah Stok");
            btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btn.setBackground(new Color(39, 174, 96));
            btn.setForeground(Color.WHITE);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setOpaque(true);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> {
                fireEditingStopped();
                showDialogTambahStok(editRow);
            });
        }
        public Component getTableCellEditorComponent(JTable t, Object val,
                boolean sel, int row, int col) {
            editRow = row; return btn;
        }
        public Object getCellEditorValue() { return "+ Tambah Stok"; }
    }

    // ════════════════════════════════════════════════════
    //  UI HELPERS
    // ════════════════════════════════════════════════════
    private JPanel buildCardHeader(String text) {
        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        hdr.setBackground(DARK);
        hdr.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(Color.WHITE);
        hdr.add(lbl);
        return hdr;
    }

    private JLabel buatLabelInfo(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(TEXT_LABEL);
        return l;
    }

    private JLabel buatBadge(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(color);
        return l;
    }

    private JButton buatBtn(String text, Color bg, Color fg, int width) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(width, 34));
        return btn;
    }

    private String formatRupiah(double angka) {
        return String.format("%,.0f", angka).replace(",", ".");
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}   