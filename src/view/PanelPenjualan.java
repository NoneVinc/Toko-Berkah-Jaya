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

public class PanelPenjualan extends JPanel {

    private final Color PRIMARY      = new Color(33, 97, 140);
    private final Color DARK         = new Color(26, 58, 92);
    private final Color GREEN        = new Color(39, 174, 96);
    private final Color RED          = new Color(231, 76, 60);
    private final Color BG           = new Color(240, 244, 248);
    private final Color CARD_BG      = Color.WHITE;
    private final Color BORDER_COLOR = new Color(226, 232, 240);
    private final Color TEXT_LABEL   = new Color(74, 85, 104);
    private final Color TEXT_MUTED   = new Color(113, 128, 150);

    private JTextField        txtNoTransaksi;
    private JComboBox<String> cmbCustomer, cmbBarang;
    private JSpinner          spnJumlah;
    private JLabel            lblStokInfo;
    private JButton           btnTambah, btnSimpan, btnReset, btnBayar;
    private JLabel            lblTotal, lblJumlahItem;
    private JTextField        txtBayar;
    private JLabel            lblKembalian;

    private JTable            tblKeranjang;
    private DefaultTableModel modelKeranjang;
    private JTable            tblRiwayat;
    private DefaultTableModel modelRiwayat;

    private double  hargaSatuan      = 0;
    private int     stokTersedia     = 0;
    private double  grandTotal       = 0;
    private boolean sedangLoadBarang = false;
    private String  namaUser         = "Admin";
    private int     idUser           = 1;

    private final java.util.List<Map<String, Object>> keranjang = new ArrayList<>();

    public PanelPenjualan() { this("Admin", 1); }

    public PanelPenjualan(String namaUser, int idUser) {
        this.namaUser = namaUser;
        this.idUser   = idUser;
        setLayout(new BorderLayout(0, 10));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        add(buildTopBar(), BorderLayout.NORTH);
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setBackground(BG);
        center.add(buildFormCard(),      BorderLayout.NORTH);
        center.add(buildKeranjangCard(), BorderLayout.CENTER);
        center.add(buildRiwayatPanel(),  BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);
        loadCustomer();
        loadBarang();
        generateNoTransaksi();
        loadRiwayat();
    }

    // ═══════════════════════════════════════════════
    //  TOP BAR
    // ═══════════════════════════════════════════════
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG);
        JLabel title = new JLabel("Transaksi Penjualan");
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

    // ═══════════════════════════════════════════════
    //  FORM CARD
    // ═══════════════════════════════════════════════
    private JPanel buildFormCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        card.add(buildCardHeader("Form Transaksi"), BorderLayout.NORTH);

        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(CARD_BG);
        body.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill   = GridBagConstraints.HORIZONTAL;

        txtNoTransaksi = buatFieldReadOnly();
        cmbCustomer    = new JComboBox<>();
        cmbCustomer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbCustomer.setPreferredSize(new Dimension(0, 30));

        g.gridx=0; g.gridy=0; g.weightx=0.3;
        body.add(wrapField("NO. TRANSAKSI", txtNoTransaksi), g);
        g.gridx=1; g.weightx=0.7;
        body.add(wrapCombo("CUSTOMER", cmbCustomer), g);

        cmbBarang = new JComboBox<>();
        cmbBarang.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbBarang.addActionListener(e -> onBarangDipilih());

        SpinnerNumberModel mdl = new SpinnerNumberModel(1, 1, 9999, 1);
        spnJumlah = new JSpinner(mdl);
        spnJumlah.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        spnJumlah.setPreferredSize(new Dimension(0, 30));

        lblStokInfo = new JLabel("Pilih barang terlebih dahulu");
        lblStokInfo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblStokInfo.setForeground(TEXT_MUTED);

        JPanel barangWrapper = new JPanel(new BorderLayout(0, 3));
        barangWrapper.setBackground(CARD_BG);
        barangWrapper.add(buatLabel("BARANG"), BorderLayout.NORTH);
        barangWrapper.add(cmbBarang,           BorderLayout.CENTER);
        barangWrapper.add(lblStokInfo,         BorderLayout.SOUTH);

        btnTambah = new JButton("+ Tambah ke Keranjang");
        btnTambah.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTambah.setBackground(GREEN);
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setBorderPainted(false);
        btnTambah.setFocusPainted(false);
        btnTambah.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTambah.setPreferredSize(new Dimension(180, 30));
        btnTambah.addActionListener(e -> tambahKeKeranjang());

        JPanel addWrapper = new JPanel(new BorderLayout());
        addWrapper.setBackground(CARD_BG);
        addWrapper.add(new JLabel(" "), BorderLayout.NORTH);
        addWrapper.add(btnTambah, BorderLayout.SOUTH);

        g.gridx=0; g.gridy=1; g.weightx=0.5;
        body.add(barangWrapper, g);
        g.gridx=1; g.weightx=0.15;
        body.add(wrapSpinner("JUMLAH", spnJumlah), g);
        g.gridx=2; g.weightx=0.35;
        body.add(addWrapper, g);

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    // ═══════════════════════════════════════════════
    //  KERANJANG CARD
    // ═══════════════════════════════════════════════
    private JPanel buildKeranjangCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        hdr.setBackground(DARK);
        hdr.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        JLabel lblHdr = new JLabel("Keranjang Belanja");
        lblHdr.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblHdr.setForeground(Color.WHITE);
        lblJumlahItem = new JLabel("  (0 item)");
        lblJumlahItem.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblJumlahItem.setForeground(new Color(150, 200, 240));
        hdr.add(lblHdr);
        hdr.add(lblJumlahItem);

        String[] kolom = {"#", "Barang", "Harga Satuan", "Jumlah", "Sisa Stok", "Subtotal", "Hapus"};
        modelKeranjang = new DefaultTableModel(kolom, 0) {
            public boolean isCellEditable(int r, int c) { return c == 6; }
            public Class<?> getColumnClass(int c) {
                return c == 6 ? JButton.class : Object.class;
            }
        };

        tblKeranjang = new JTable(modelKeranjang);
        tblKeranjang.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblKeranjang.setRowHeight(34);
        tblKeranjang.setShowHorizontalLines(true);
        tblKeranjang.setGridColor(BORDER_COLOR);
        tblKeranjang.setSelectionBackground(new Color(235, 245, 255));
        tblKeranjang.setFocusable(false);

        JTableHeader header = tblKeranjang.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(new Color(44, 80, 115));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 32));
        header.setReorderingAllowed(false);

        int[] widths = {35, 190, 110, 60, 80, 110, 70};
        for (int i = 0; i < widths.length; i++)
            tblKeranjang.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        tblKeranjang.getColumn("Hapus").setCellRenderer(new ButtonRenderer());
        tblKeranjang.getColumn("Hapus").setCellEditor(new ButtonEditor(new JCheckBox()));

        tblKeranjang.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setFont(new Font("Segoe UI", Font.PLAIN, 12));
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (sel) {
                    setBackground(new Color(235, 245, 255));
                    setForeground(DARK);
                } else {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(247, 250, 252));
                    if (col == 4) { // Sisa Stok
                        try {
                            int sisa = Integer.parseInt(val.toString());
                            if (sisa == 0) {
                                setForeground(RED);
                                setFont(new Font("Segoe UI", Font.BOLD, 12));
                            } else if (sisa <= 5) {
                                setForeground(new Color(230, 126, 34));
                                setFont(new Font("Segoe UI", Font.BOLD, 12));
                            } else {
                                setForeground(GREEN);
                                setFont(new Font("Segoe UI", Font.BOLD, 12));
                            }
                        } catch (NumberFormatException ex) {
                            setForeground(new Color(45, 55, 72));
                        }
                    } else if (col == 5) { // Subtotal
                        setForeground(GREEN);
                        setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } else {
                        setForeground(new Color(45, 55, 72));
                    }
                }
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(tblKeranjang);
        scroll.setBorder(null);
        scroll.setPreferredSize(new Dimension(0, 150));
        scroll.getViewport().setBackground(Color.WHITE);

        // ── Total bar ─────────────────────────────────
        JPanel totalBar = new JPanel(new BorderLayout());
        totalBar.setBackground(DARK);
        totalBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        JLabel lblTotalTxt = new JLabel("TOTAL PEMBAYARAN");
        lblTotalTxt.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTotalTxt.setForeground(new Color(150, 190, 220));
        lblTotal = new JLabel("Rp 0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotal.setForeground(Color.WHITE);
        totalBar.add(lblTotalTxt, BorderLayout.WEST);
        totalBar.add(lblTotal,    BorderLayout.EAST);

        // ── Bayar + Kembalian ─────────────────────────
        JPanel bayarRow = new JPanel(new GridLayout(1, 2, 10, 0));
        bayarRow.setBackground(new Color(235, 245, 255));
        bayarRow.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        // Input bayar
        JPanel bayarWrapper = new JPanel(new BorderLayout(0, 4));
        bayarWrapper.setBackground(new Color(235, 245, 255));
        JLabel lblBayarTxt = new JLabel("UANG BAYAR");
        lblBayarTxt.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblBayarTxt.setForeground(TEXT_LABEL);
        txtBayar = new JTextField();
        txtBayar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtBayar.setForeground(PRIMARY);
        txtBayar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        txtBayar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { hitungKembalian(); }
        });
        bayarWrapper.add(lblBayarTxt, BorderLayout.NORTH);
        bayarWrapper.add(txtBayar,    BorderLayout.CENTER);

        // Kembalian
        JPanel kembalianWrapper = new JPanel(new BorderLayout(0, 4));
        kembalianWrapper.setBackground(new Color(235, 245, 255));
        JLabel lblKembalianTxt = new JLabel("KEMBALIAN");
        lblKembalianTxt.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblKembalianTxt.setForeground(TEXT_LABEL);
        lblKembalian = new JLabel("Rp 0");
        lblKembalian.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblKembalian.setForeground(GREEN);
        lblKembalian.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        lblKembalian.setOpaque(true);
        lblKembalian.setBackground(Color.WHITE);
        kembalianWrapper.add(lblKembalianTxt, BorderLayout.NORTH);
        kembalianWrapper.add(lblKembalian,    BorderLayout.CENTER);

        bayarRow.add(bayarWrapper);
        bayarRow.add(kembalianWrapper);

        // Tombol simpan & reset
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(new Color(247, 250, 252));
        btnRow.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        btnBayar  = buatBtn("Bayar Sekarang", new Color(22, 160, 133), Color.WHITE, 170);
        btnSimpan = buatBtn("Simpan Transaksi", PRIMARY, Color.WHITE, 160);
        btnReset  = buatBtn("Reset", Color.WHITE, new Color(113, 128, 150), 80);
        btnReset.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        btnBayar.addActionListener(e  -> showDialogPembayaran());
        btnSimpan.addActionListener(e -> simpanTransaksi("Cash", (long) grandTotal));
        btnReset.addActionListener(e  -> resetForm());
        btnRow.add(btnBayar);
        btnRow.add(btnSimpan);
        btnRow.add(btnReset);

        card.add(hdr,    BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(totalBar, BorderLayout.NORTH);
        bottom.add(bayarRow, BorderLayout.CENTER);
        bottom.add(btnRow,   BorderLayout.SOUTH);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }

    // ═══════════════════════════════════════════════
    //  RIWAYAT PANEL
    // ═══════════════════════════════════════════════
    private JPanel buildRiwayatPanel() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        card.add(buildCardHeader("Riwayat Transaksi Hari Ini"), BorderLayout.NORTH);

        String[] kolom = {"#", "No. Transaksi", "Customer", "Barang", "Jml", "Total", "Kasir"};
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

        int[] widths = {35, 160, 140, 150, 50, 110, 90};
        for (int i = 0; i < widths.length; i++)
            tblRiwayat.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(tblRiwayat);
        scroll.setBorder(null);
        scroll.setPreferredSize(new Dimension(0, 140));
        scroll.getViewport().setBackground(Color.WHITE);

        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    // ═══════════════════════════════════════════════
    //  DATABASE OPERATIONS
    // ═══════════════════════════════════════════════
    private void loadCustomer() {
        cmbCustomer.removeAllItems();
        cmbCustomer.addItem("-- Pilih Customer --");
        try {
            Connection conn = Koneksi.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT id_customer, nama_customer FROM tb_customer ORDER BY nama_customer");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                cmbCustomer.addItem(rs.getString("id_customer") +
                    " - " + rs.getString("nama_customer"));
            rs.close(); ps.close();
        } catch (SQLException e) { showError("Gagal load customer: " + e.getMessage()); }
    }

    private void loadBarang() {
        sedangLoadBarang = true;
        cmbBarang.removeAllItems();
        cmbBarang.addItem("-- Pilih Barang --");
        try {
            Connection conn = Koneksi.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT id_barang, nama_barang FROM tb_barang WHERE stok > 0 ORDER BY nama_barang");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                cmbBarang.addItem(rs.getString("id_barang") +
                    " - " + rs.getString("nama_barang"));
            rs.close(); ps.close();
        } catch (SQLException e) { showError("Gagal load barang: " + e.getMessage()); }
        sedangLoadBarang = false;
    }

    private void onBarangDipilih() {
        if (sedangLoadBarang) return;
        if (cmbBarang.getSelectedIndex() == 0) {
            lblStokInfo.setText("Pilih barang terlebih dahulu");
            lblStokInfo.setForeground(TEXT_MUTED);
            hargaSatuan = 0; stokTersedia = 0;
            return;
        }
        String idBarang = getSelectedIdBarang();
        int stokDiKeranjang = 0;
        for (Map<String, Object> item : keranjang)
            if (item.get("idBarang").equals(idBarang))
                stokDiKeranjang = (int) item.get("jumlah");
        try {
            Connection conn = Koneksi.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT harga_jual, stok FROM tb_barang WHERE id_barang = ?");
            ps.setString(1, idBarang);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                hargaSatuan  = rs.getDouble("harga_jual");
                int stokDB   = rs.getInt("stok");
                stokTersedia = stokDB - stokDiKeranjang;
                lblStokInfo.setText("Stok tersedia: " + stokTersedia +
                    "  |  Harga: Rp " + formatRupiah(hargaSatuan));
                lblStokInfo.setForeground(stokTersedia > 10
                    ? new Color(39, 174, 96) : new Color(243, 156, 18));
                ((SpinnerNumberModel) spnJumlah.getModel()).setMaximum(stokTersedia);
                if ((int) spnJumlah.getValue() > stokTersedia && stokTersedia > 0)
                    spnJumlah.setValue(stokTersedia);
            }
            rs.close(); ps.close();
        } catch (SQLException e) { showError("Gagal ambil data barang: " + e.getMessage()); }
    }

    private void tambahKeKeranjang() {
        if (cmbBarang.getSelectedIndex() == 0) {
            showWarn("Pilih barang terlebih dahulu!"); return; }
        if (stokTersedia <= 0) {
            showWarn("Stok barang tidak mencukupi!"); return; }
        int    jumlah   = (int) spnJumlah.getValue();
        String idBarang = getSelectedIdBarang();
        String nmBarang = getSelectedNamaBarang();
        if (jumlah > stokTersedia) {
            showWarn("Jumlah melebihi stok tersedia (" + stokTersedia + ")!"); return; }

        for (Map<String, Object> item : keranjang) {
            if (item.get("idBarang").equals(idBarang)) {
                int jmlBaru = (int) item.get("jumlah") + jumlah;
                int stokDB  = (int) item.get("stokDB");
                if (jmlBaru > stokDB) {
                    showWarn("Total jumlah melebihi stok (" + stokDB + ")!"); return; }
                item.put("jumlah",   jmlBaru);
                item.put("subtotal", hargaSatuan * jmlBaru);
                refreshTabelKeranjang();
                cmbBarang.setSelectedIndex(0);
                spnJumlah.setValue(1);
                return;
            }
        }

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("idBarang", idBarang);
        item.put("nama",     nmBarang);
        item.put("harga",    hargaSatuan);
        item.put("jumlah",   jumlah);
        item.put("subtotal", hargaSatuan * jumlah);
        item.put("stokDB",   stokTersedia + jumlah);
        keranjang.add(item);

        refreshTabelKeranjang();
        cmbBarang.setSelectedIndex(0);
        spnJumlah.setValue(1);
        lblStokInfo.setText("Pilih barang terlebih dahulu");
        lblStokInfo.setForeground(TEXT_MUTED);
    }

    private void refreshTabelKeranjang() {
        modelKeranjang.setRowCount(0);
        grandTotal = 0;
        int no = 1;
        for (Map<String, Object> item : keranjang) {
            double sub    = (double) item.get("subtotal");
            int    stokDB = (int)    item.get("stokDB");
            int    jml    = (int)    item.get("jumlah");
            int    sisa   = stokDB - jml;
            grandTotal += sub;
            modelKeranjang.addRow(new Object[]{
                no++,
                item.get("nama"),
                "Rp " + formatRupiah((double) item.get("harga")),
                jml,
                sisa,
                "Rp " + formatRupiah(sub),
                "Hapus"
            });
        }
        lblTotal.setText("Rp " + formatRupiah(grandTotal));
        lblJumlahItem.setText("  (" + keranjang.size() + " item)");
    }

    private void hapusDariKeranjang(int row) {
        if (row >= 0 && row < keranjang.size()) {
            keranjang.remove(row);
            refreshTabelKeranjang();
            String prev = (String) cmbBarang.getSelectedItem();
            loadBarang();
            cmbBarang.setSelectedItem(prev);
        }
    }

    // ═══════════════════════════════════════════════
    //  DIALOG PEMBAYARAN
    // ═══════════════════════════════════════════════
    private void showDialogPembayaran() {
        if (cmbCustomer.getSelectedIndex() == 0) {
            showWarn("Pilih Customer terlebih dahulu!"); return; }
        if (keranjang.isEmpty()) {
            showWarn("Keranjang masih kosong!"); return; }

        // ── Warna lokal dialog ──────────────────────
        Color TEAL    = new Color(22, 160, 133);
        Color QRIS_CL = new Color(142, 68, 173);

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                                  "Pembayaran", true);
        dlg.setLayout(new BorderLayout());
        dlg.setResizable(false);

        // Header dialog
        JPanel hdrDlg = new JPanel(new BorderLayout());
        hdrDlg.setBackground(DARK);
        hdrDlg.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        JLabel hdrTitle = new JLabel("💳  Pembayaran");
        hdrTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        hdrTitle.setForeground(Color.WHITE);
        JLabel hdrNo = new JLabel(txtNoTransaksi.getText());
        hdrNo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hdrNo.setForeground(new Color(150, 200, 240));
        hdrDlg.add(hdrTitle, BorderLayout.WEST);
        hdrDlg.add(hdrNo,    BorderLayout.EAST);

        // ── Body ────────────────────────────────────
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(new Color(245, 248, 252));
        body.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // --- Baris Total ---
        JPanel rowTotal = new JPanel(new BorderLayout());
        rowTotal.setBackground(DARK);
        rowTotal.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        rowTotal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        JLabel lbTotalTxt = new JLabel("TOTAL PEMBAYARAN");
        lbTotalTxt.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbTotalTxt.setForeground(new Color(150, 190, 220));
        JLabel lbTotalVal = new JLabel("Rp " + formatRupiah(grandTotal));
        lbTotalVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbTotalVal.setForeground(Color.WHITE);
        rowTotal.add(lbTotalTxt, BorderLayout.WEST);
        rowTotal.add(lbTotalVal, BorderLayout.EAST);

        // --- Metode Bayar toggle ---
        JPanel rowMetode = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        rowMetode.setBackground(new Color(245, 248, 252));
        rowMetode.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        JLabel lbMetode = new JLabel("METODE BAYAR");
        lbMetode.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbMetode.setForeground(TEXT_LABEL);

        JToggleButton btnCash = new JToggleButton("Cash");
        JToggleButton btnQris = new JToggleButton("QRIS");
        for (JToggleButton tb : new JToggleButton[]{btnCash, btnQris}) {
            tb.setFont(new Font("Segoe UI", Font.BOLD, 12));
            tb.setFocusPainted(false);
            tb.setCursor(new Cursor(Cursor.HAND_CURSOR));
            tb.setPreferredSize(new Dimension(110, 32));
        }
        ButtonGroup bgMetode = new ButtonGroup();
        bgMetode.add(btnCash); bgMetode.add(btnQris);
        btnCash.setSelected(true);
        btnCash.setBackground(TEAL); btnCash.setForeground(Color.WHITE);
        btnQris.setBackground(new Color(220, 220, 220)); btnQris.setForeground(new Color(80,80,80));

        rowMetode.add(lbMetode);
        rowMetode.add(Box.createHorizontalStrut(8));
        rowMetode.add(btnCash);
        rowMetode.add(btnQris);

        // --- Panel Cash ---
        JPanel panelCash = new JPanel(new GridLayout(2, 2, 10, 8));
        panelCash.setBackground(new Color(245, 248, 252));
        panelCash.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        panelCash.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        JLabel lbBayarTxt = new JLabel("UANG BAYAR");
        lbBayarTxt.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbBayarTxt.setForeground(TEXT_LABEL);
        JTextField dlgBayar = new JTextField();
        dlgBayar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dlgBayar.setForeground(PRIMARY);
        dlgBayar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));

        JLabel lbKembalianTxt = new JLabel("KEMBALIAN");
        lbKembalianTxt.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbKembalianTxt.setForeground(TEXT_LABEL);
        JLabel dlgKembalian = new JLabel("Rp 0");
        dlgKembalian.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dlgKembalian.setForeground(GREEN);
        dlgKembalian.setOpaque(true);
        dlgKembalian.setBackground(Color.WHITE);
        dlgKembalian.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        panelCash.add(lbBayarTxt);    panelCash.add(lbKembalianTxt);
        panelCash.add(dlgBayar);      panelCash.add(dlgKembalian);

        dlgBayar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                try {
                    String s = dlgBayar.getText().trim().replace(".", "");
                    if (s.isEmpty()) { dlgKembalian.setText("Rp 0"); dlgKembalian.setForeground(GREEN); return; }
                    long kmb = Long.parseLong(s) - (long) grandTotal;
                    if (kmb < 0) {
                        dlgKembalian.setText("Kurang Rp " + formatRupiah(Math.abs(kmb)));
                        dlgKembalian.setForeground(RED);
                    } else {
                        dlgKembalian.setText("Rp " + formatRupiah(kmb));
                        dlgKembalian.setForeground(GREEN);
                    }
                } catch (NumberFormatException ex) {
                    dlgKembalian.setText("Tidak valid"); dlgKembalian.setForeground(RED);
                }
            }
        });

        // --- Panel QRIS ---
        JPanel panelQris = new JPanel(new BorderLayout(10, 0));
        panelQris.setBackground(new Color(245, 248, 252));
        panelQris.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        panelQris.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        panelQris.setVisible(false);

        JPanel qrisBox = new JPanel(new BorderLayout());
        qrisBox.setBackground(Color.WHITE);
        qrisBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(QRIS_CL, 2),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        JLabel qrisLbl = new JLabel("<html><center>Scan QRIS untuk membayar<br>"
            + "<span style='color:#8e44ad;font-size:11px;'>"
            + "Rp " + formatRupiah(grandTotal) + "</span></center></html>",
            SwingConstants.CENTER);
        qrisLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        qrisLbl.setForeground(QRIS_CL);
        qrisBox.add(qrisLbl, BorderLayout.CENTER);
        panelQris.add(qrisBox, BorderLayout.CENTER);

        // Toggle listener
        btnCash.addActionListener(e -> {
            btnCash.setBackground(TEAL); btnCash.setForeground(Color.WHITE);
            btnQris.setBackground(new Color(220,220,220)); btnQris.setForeground(new Color(80,80,80));
            panelCash.setVisible(true); panelQris.setVisible(false);
            dlg.pack();
        });
        btnQris.addActionListener(e -> {
            btnQris.setBackground(QRIS_CL); btnQris.setForeground(Color.WHITE);
            btnCash.setBackground(new Color(220,220,220)); btnCash.setForeground(new Color(80,80,80));
            panelQris.setVisible(true); panelCash.setVisible(false);
            dlg.pack();
        });

        // --- Separator ---
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        // ── Tombol Konfirmasi ────────────────────────
        JPanel btnPanelDlg = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanelDlg.setBackground(new Color(245, 248, 252));
        btnPanelDlg.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        JButton btnKonfirmasi = buatBtn("Konfirmasi Pembayaran", TEAL, Color.WHITE, 200);
        JButton btnBatal      = buatBtn("Batal", Color.WHITE, new Color(113,128,150), 80);
        btnBatal.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        btnKonfirmasi.addActionListener(e -> {
            String metode = btnCash.isSelected() ? "Cash" : "QRIS";
            long bayarFinal;
            if ("Cash".equals(metode)) {
                String bs = dlgBayar.getText().trim().replace(".", "");
                if (bs.isEmpty()) { showWarn("Masukkan uang bayar!"); return; }
                try { bayarFinal = Long.parseLong(bs); }
                catch (NumberFormatException ex) { showWarn("Uang bayar tidak valid!"); return; }
                if (bayarFinal < (long) grandTotal) {
                    showWarn("Uang bayar kurang!\nTotal : Rp " + formatRupiah(grandTotal) +
                        "\nBayar : Rp " + formatRupiah(bayarFinal)); return; }
            } else {
                bayarFinal = (long) grandTotal; // QRIS exact
            }
            dlg.dispose();
            simpanTransaksi(metode, bayarFinal);
        });
        btnBatal.addActionListener(e -> dlg.dispose());

        btnPanelDlg.add(btnKonfirmasi);
        btnPanelDlg.add(btnBatal);

        body.add(rowTotal);
        body.add(Box.createVerticalStrut(10));
        body.add(rowMetode);
        body.add(Box.createVerticalStrut(6));
        body.add(panelCash);
        body.add(panelQris);

        dlg.add(hdrDlg,      BorderLayout.NORTH);
        dlg.add(body,         BorderLayout.CENTER);
        dlg.add(btnPanelDlg, BorderLayout.SOUTH);
        dlg.pack();
        dlg.setMinimumSize(new Dimension(480, 0));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private void simpanTransaksi(String metodeBayar, long bayar) {
        if (cmbCustomer.getSelectedIndex() == 0) {
            showWarn("Pilih Customer terlebih dahulu!"); return; }
        if (keranjang.isEmpty()) {
            showWarn("Keranjang masih kosong!"); return; }

        String namaCustomer = ((String) cmbCustomer.getSelectedItem()).split(" - ")[1].trim();
        String idCustomer   = ((String) cmbCustomer.getSelectedItem()).split(" - ")[0].trim();

        StringBuilder sb = new StringBuilder("Konfirmasi Transaksi:\n");
        sb.append("Customer   : ").append(namaCustomer).append("\n");
        sb.append("Metode     : ").append(metodeBayar).append("\n\n");
        for (Map<String, Object> item : keranjang) {
            sb.append("- ").append(item.get("nama"))
              .append(" x").append(item.get("jumlah"))
              .append(" = Rp ").append(formatRupiah((double) item.get("subtotal")))
              .append("\n");
        }
        sb.append("\nTotal     : Rp ").append(formatRupiah(grandTotal));
        sb.append("\nBayar     : Rp ").append(formatRupiah(bayar));
        sb.append("\nKembalian : Rp ").append(formatRupiah(bayar - (long) grandTotal));
        sb.append("\n\nSimpan transaksi ini?");

        int opt = JOptionPane.showConfirmDialog(this, sb.toString(),
            "Konfirmasi", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opt != JOptionPane.YES_OPTION) return;

        Connection conn = null;
        try {
            conn = Koneksi.getConnection();
            conn.setAutoCommit(false);
            for (Map<String, Object> item : keranjang) {
                PreparedStatement psJual = conn.prepareStatement(
                    "INSERT INTO tb_penjualan " +
                    "(tgl_transaksi, id_customer, id_barang, jumlah_beli, total_bayar, id_user)" +
                    " VALUES (CURDATE(), ?, ?, ?, ?, ?)");
                psJual.setString(1, idCustomer);
                psJual.setString(2, (String) item.get("idBarang"));
                psJual.setInt(3,    (int)    item.get("jumlah"));
                psJual.setDouble(4, (double) item.get("subtotal"));
                psJual.setInt(5,    idUser);
                psJual.executeUpdate();
                psJual.close();

                PreparedStatement psStok = conn.prepareStatement(
                    "UPDATE tb_barang SET stok = stok - ? WHERE id_barang = ?");
                psStok.setInt(1,    (int)    item.get("jumlah"));
                psStok.setString(2, (String) item.get("idBarang"));
                psStok.executeUpdate();
                psStok.close();
            }
            conn.commit();
            tampilkanStruk(namaCustomer, bayar, metodeBayar);
            resetForm();
            loadBarang();
            loadRiwayat();
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            showError("Gagal menyimpan transaksi:\n" + e.getMessage());
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }

    private void hitungKembalian() {
        try {
            String bayarStr = txtBayar.getText().trim().replace(".", "");
            if (bayarStr.isEmpty()) {
                lblKembalian.setText("Rp 0");
                lblKembalian.setForeground(GREEN);
                return;
            }
            long kembalian = Long.parseLong(bayarStr) - (long) grandTotal;
            if (kembalian < 0) {
                lblKembalian.setText("Kurang Rp " + formatRupiah(Math.abs(kembalian)));
                lblKembalian.setForeground(RED);
            } else {
                lblKembalian.setText("Rp " + formatRupiah(kembalian));
                lblKembalian.setForeground(GREEN);
            }
        } catch (NumberFormatException e) {
            lblKembalian.setText("Input tidak valid");
            lblKembalian.setForeground(RED);
        }
    }

    private void tampilkanStruk(String namaCustomer, long bayar, String metodeBayar) {
        long kembalian = bayar - (long) grandTotal;
        StringBuilder struk = new StringBuilder();
        struk.append("======================================\n");
        struk.append("         TOKO BERKAH JAYA\n");
        struk.append("      Sistem Informasi Penjualan\n");
        struk.append("======================================\n");
        struk.append(String.format("  No    : %s\n", txtNoTransaksi.getText()));
        struk.append(String.format("  Tgl   : %s\n", new SimpleDateFormat(
            "dd/MM/yyyy HH:mm").format(new java.util.Date())));
        struk.append(String.format("  Kasir : %s\n", namaUser));
        struk.append(String.format("  Cust  : %s\n", namaCustomer));
        struk.append("--------------------------------------\n");
        for (Map<String, Object> item : keranjang) {
            String nama = item.get("nama").toString();
            if (nama.length() > 22) nama = nama.substring(0, 22);
            struk.append(String.format("  %-22s\n", nama));
            struk.append(String.format("  %d x Rp %-10s = Rp %s\n",
                item.get("jumlah"),
                formatRupiah((double) item.get("harga")),
                formatRupiah((double) item.get("subtotal"))));
        }
        struk.append("--------------------------------------\n");
        struk.append(String.format("  Total     : Rp %s\n", formatRupiah(grandTotal)));
        struk.append(String.format("  Metode    : %s\n", metodeBayar));
        struk.append(String.format("  Bayar     : Rp %s\n", formatRupiah(bayar)));
        struk.append(String.format("  Kembalian : Rp %s\n", formatRupiah(kembalian)));
        struk.append("======================================\n");
        struk.append("   Terima kasih atas kunjungan Anda!\n");
        struk.append("======================================");

        JTextArea txtStruk = new JTextArea(struk.toString());
        txtStruk.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtStruk.setEditable(false);
        txtStruk.setBackground(new Color(250, 250, 245));
        txtStruk.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollStruk = new JScrollPane(txtStruk);
        scrollStruk.setPreferredSize(new Dimension(420, 400));
        JOptionPane.showMessageDialog(this, scrollStruk,
            "Struk - " + txtNoTransaksi.getText(), JOptionPane.PLAIN_MESSAGE);
    }

    private void loadRiwayat() {
        modelRiwayat.setRowCount(0);
        try {
            Connection conn = Koneksi.getConnection();
            String sql =
                "SELECT p.id_jual, p.tgl_transaksi, c.nama_customer, " +
                "b.nama_barang, p.jumlah_beli, p.total_bayar, u.nama_lengkap " +
                "FROM tb_penjualan p " +
                "JOIN tb_customer c ON p.id_customer = c.id_customer " +
                "JOIN tb_barang   b ON p.id_barang   = b.id_barang " +
                "JOIN tb_user     u ON p.id_user     = u.id_user " +
                "WHERE DATE(p.tgl_transaksi) = CURDATE() " +
                "ORDER BY p.id_jual DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            int no = 1;
            String tgl = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
            while (rs.next()) {
                modelRiwayat.addRow(new Object[]{
                    no++,
                    String.format("TRX-%s-%03d", tgl, rs.getInt("id_jual")),
                    rs.getString("nama_customer"),
                    rs.getString("nama_barang"),
                    rs.getInt("jumlah_beli"),
                    "Rp " + formatRupiah(rs.getDouble("total_bayar")),
                    rs.getString("nama_lengkap")
                });
            }
            rs.close(); ps.close();
        } catch (SQLException e) { showError("Gagal load riwayat: " + e.getMessage()); }
    }

    private void generateNoTransaksi() {
        String tgl = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
        try {
            Connection conn = Koneksi.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM tb_penjualan WHERE DATE(tgl_transaksi) = CURDATE()");
            ResultSet rs = ps.executeQuery();
            int urutan = rs.next() ? rs.getInt(1) + 1 : 1;
            txtNoTransaksi.setText(String.format("TRX-%s-%03d", tgl, urutan));
            rs.close(); ps.close();
        } catch (SQLException e) {
            txtNoTransaksi.setText("TRX-" + tgl + "-001");
        }
    }

    private void resetForm() {
        keranjang.clear();
        modelKeranjang.setRowCount(0);
        cmbCustomer.setSelectedIndex(0);
        cmbBarang.setSelectedIndex(0);
        spnJumlah.setValue(1);
        lblStokInfo.setText("Pilih barang terlebih dahulu");
        lblStokInfo.setForeground(TEXT_MUTED);
        lblTotal.setText("Rp 0");
        lblJumlahItem.setText("  (0 item)");
        txtBayar.setText("");
        lblKembalian.setText("Rp 0");
        lblKembalian.setForeground(GREEN);
        grandTotal = 0; hargaSatuan = 0; stokTersedia = 0;
        ((SpinnerNumberModel) spnJumlah.getModel()).setMaximum(9999);
        generateNoTransaksi();
    }

    // ═══════════════════════════════════════════════
    //  BUTTON RENDERER & EDITOR
    // ═══════════════════════════════════════════════
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.BOLD, 10));
            setBackground(RED); setForeground(Color.WHITE);
            setBorderPainted(false); setFocusPainted(false);
        }
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean foc, int row, int col) {
            setText("Hapus"); return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton btn;
        private int     editRow;
        public ButtonEditor(JCheckBox cb) {
            super(cb);
            btn = new JButton("Hapus");
            btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
            btn.setBackground(RED); btn.setForeground(Color.WHITE);
            btn.setBorderPainted(false); btn.setFocusPainted(false);
            btn.setOpaque(true);
            btn.addActionListener(e -> { fireEditingStopped(); hapusDariKeranjang(editRow); });
        }
        public Component getTableCellEditorComponent(JTable t, Object val,
                boolean sel, int row, int col) {
            editRow = row; return btn;
        }
        public Object getCellEditorValue() { return "Hapus"; }
    }

    // ═══════════════════════════════════════════════
    //  UI HELPERS
    // ═══════════════════════════════════════════════
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

    private JTextField buatFieldReadOnly() {
        JTextField f = new JTextField();
        f.setEditable(false);
        f.setBackground(new Color(247, 250, 252));
        f.setForeground(TEXT_MUTED);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setPreferredSize(new Dimension(0, 30));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(3, 10, 3, 10)));
        return f;
    }

    private JLabel buatLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(TEXT_LABEL);
        return lbl;
    }

    private JPanel wrapField(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(CARD_BG);
        p.add(buatLabel(label), BorderLayout.NORTH);
        p.add(field,            BorderLayout.CENTER);
        return p;
    }

    private JPanel wrapCombo(String label, JComboBox<String> combo) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(CARD_BG);
        p.add(buatLabel(label), BorderLayout.NORTH);
        p.add(combo,            BorderLayout.CENTER);
        return p;
    }

    private JPanel wrapSpinner(String label, JSpinner spinner) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(CARD_BG);
        p.add(buatLabel(label), BorderLayout.NORTH);
        p.add(spinner,          BorderLayout.CENTER);
        return p;
    }

    private JButton buatBtn(String text, Color bg, Color fg, int width) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg); btn.setForeground(fg);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(width, 34));
        return btn;
    }

    private String getSelectedIdBarang() {
        return ((String) cmbBarang.getSelectedItem()).split(" - ")[0].trim();
    }

    private String getSelectedNamaBarang() {
        String[] parts = ((String) cmbBarang.getSelectedItem()).split(" - ", 2);
        return parts.length > 1 ? parts[1].trim() : "";
    }

    private String formatRupiah(double angka) {
        return String.format("%,.0f", angka).replace(",", ".");
    }

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}