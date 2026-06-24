package view;

import koneksi.Koneksi;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.sql.*;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class PanelLaporan extends JPanel {

    // ── Warna tema ─────────────────────────────────────────────
    private final Color PRIMARY      = new Color(33, 97, 140);
    private final Color DARK         = new Color(26, 58, 92);
    private final Color GREEN        = new Color(39, 174, 96);
    private final Color BG           = new Color(240, 244, 248);
    private final Color CARD_BG      = Color.WHITE;
    private final Color BORDER_COLOR = new Color(226, 232, 240);
    private final Color TEXT_LABEL   = new Color(74, 85, 104);
    private final Color TEXT_MUTED   = new Color(113, 128, 150);

    // ── Komponen filter ────────────────────────────────────────
    private JTextField     txtDari, txtSampai;
    private JComboBox<String> cmbCustomer;
    private JButton        btnTampilkan, btnReset, btnCetak;

    // ── Summary labels ─────────────────────────────────────────
    private JLabel lblTotalTrx, lblTotalItem, lblRataRata, lblTotalPendapatan;

    // ── Tabel ──────────────────────────────────────────────────
    private JTable            tabel;
    private DefaultTableModel modelTabel;

    public PanelLaporan() {
        setLayout(new BorderLayout(0, 12));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(buildTopBar(),    BorderLayout.NORTH);

        JPanel tengah = new JPanel(new BorderLayout(0, 12));
        tengah.setBackground(BG);
        tengah.add(buildFilterCard(), BorderLayout.NORTH);
        tengah.add(buildTableCard(),  BorderLayout.CENTER);
        add(tengah, BorderLayout.CENTER);

        loadCustomer();
        setTanggalDefault();
        tampilkanLaporan();
    }

    // ══════════════════════════════════════════════════════════
    //  TOP BAR
    // ══════════════════════════════════════════════════════════
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG);
        bar.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
        JLabel title = new JLabel("Laporan Penjualan");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(DARK);
        bar.add(title, BorderLayout.WEST);
        return bar;
    }

    // ══════════════════════════════════════════════════════════
    //  FILTER CARD
    // ══════════════════════════════════════════════════════════
    private JPanel buildFilterCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        // Header
        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        hdr.setBackground(DARK);
        hdr.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        JLabel lblH = new JLabel("Filter Laporan");
        lblH.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblH.setForeground(Color.WHITE);
        hdr.add(lblH);

        // Filter row
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setBackground(CARD_BG);
        filterRow.setBorder(BorderFactory.createEmptyBorder(12, 16, 10, 16));

        txtDari   = buatDateField();
        txtSampai = buatDateField();

        cmbCustomer = new JComboBox<>();
        cmbCustomer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbCustomer.setPreferredSize(new Dimension(200, 30));

        btnTampilkan = buatBtn("Tampilkan", PRIMARY, Color.WHITE, 130);
        btnReset     = buatBtn("Reset",      Color.WHITE,
            new Color(113,128,150), 90);
        btnReset.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        btnCetak     = buatBtn("Cetak",      GREEN,  Color.WHITE, 100);

        btnTampilkan.addActionListener(e -> tampilkanLaporan());
        btnReset.addActionListener(e     -> resetFilter());
        btnCetak.addActionListener(e     -> cetakLaporan());

        filterRow.add(wrapField("DARI TANGGAL",    txtDari));
        filterRow.add(wrapField("SAMPAI TANGGAL",  txtSampai));
        filterRow.add(wrapCombo("CUSTOMER",         cmbCustomer));
        filterRow.add(Box.createHorizontalStrut(4));
        filterRow.add(btnTampilkan);
        filterRow.add(btnReset);
        filterRow.add(btnCetak);

        // Summary cards
        JPanel summary = new JPanel(new GridLayout(1, 4, 10, 0));
        summary.setBackground(CARD_BG);
        summary.setBorder(BorderFactory.createEmptyBorder(0, 16, 14, 16));

        lblTotalTrx        = new JLabel("0");
        lblTotalItem       = new JLabel("0");
        lblRataRata        = new JLabel("Rp 0");
        lblTotalPendapatan = new JLabel("Rp 0");
        lblTotalPendapatan.setForeground(GREEN);

        summary.add(buildSumCard("TOTAL TRANSAKSI",   lblTotalTrx,        DARK));
        summary.add(buildSumCard("ITEM TERJUAL",       lblTotalItem,       DARK));
        summary.add(buildSumCard("RATA-RATA/TRANSAKSI",lblRataRata,        DARK));
        summary.add(buildSumCard("TOTAL PENDAPATAN",   lblTotalPendapatan, GREEN));

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(CARD_BG);
        body.add(filterRow, BorderLayout.NORTH);
        body.add(summary,   BorderLayout.CENTER);

        card.add(hdr,  BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildSumCard(String label, JLabel valLabel, Color valColor) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(new Color(247, 250, 252));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(TEXT_MUTED);

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valLabel.setForeground(valColor);
        valLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(lbl,      BorderLayout.NORTH);
        card.add(valLabel, BorderLayout.CENTER);
        return card;
    }

    // ══════════════════════════════════════════════════════════
    //  TABEL CARD
    // ══════════════════════════════════════════════════════════
    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        hdr.setBackground(DARK);
        hdr.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        JLabel lblH = new JLabel("Detail Transaksi");
        lblH.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblH.setForeground(Color.WHITE);
        hdr.add(lblH);

        String[] kolom = {"#", "Tanggal", "No. Transaksi", "Customer",
                          "Barang", "Jml", "Harga Satuan", "Total Bayar", "Kasir"};
        modelTabel = new DefaultTableModel(kolom, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tabel = new JTable(modelTabel);
        tabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabel.setRowHeight(32);
        tabel.setShowHorizontalLines(true);
        tabel.setGridColor(BORDER_COLOR);
        tabel.setSelectionBackground(new Color(235, 245, 255));
        tabel.setFocusable(false);

        // Warna baris alternating
        tabel.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
                    setForeground(new Color(45, 55, 72));
                }
                // Highlight kolom total
                if (col == 7 && !sel) {
                    setForeground(GREEN);
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                }
                return this;
            }
        });

        JTableHeader header = tabel.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(new Color(44, 80, 115));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 34));
        header.setReorderingAllowed(false);

        int[] widths = {35, 90, 150, 130, 140, 45, 100, 110, 90};
        for (int i = 0; i < widths.length; i++)
            tabel.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(tabel);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(null);

        card.add(hdr,    BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    // ══════════════════════════════════════════════════════════
    //  DATABASE OPERATIONS
    // ══════════════════════════════════════════════════════════
    private void loadCustomer() {
        cmbCustomer.removeAllItems();
        cmbCustomer.addItem("-- Semua Customer --");
        try {
            Connection conn = Koneksi.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT id_customer, nama_customer FROM tb_customer ORDER BY nama_customer");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                cmbCustomer.addItem(rs.getString("id_customer") +
                    " - " + rs.getString("nama_customer"));
            rs.close(); ps.close();
        } catch (SQLException e) {
            showError("Gagal memuat customer: " + e.getMessage());
        }
    }

    private void tampilkanLaporan() {
        modelTabel.setRowCount(0);

        String dari   = txtDari.getText().trim();
        String sampai = txtSampai.getText().trim();

        if (dari.isEmpty() || sampai.isEmpty()) {
            showWarn("Isi tanggal dari dan sampai terlebih dahulu!"); return;
        }

        String idCustomer = null;
        if (cmbCustomer.getSelectedIndex() > 0) {
            idCustomer = ((String) cmbCustomer.getSelectedItem())
                .split(" - ")[0].trim();
        }

        try {
            Connection conn = Koneksi.getConnection();

            StringBuilder sql = new StringBuilder(
                "SELECT p.id_jual, p.tgl_transaksi, c.nama_customer, " +
                "b.nama_barang, b.harga_jual, p.jumlah_beli, p.total_bayar, " +
                "u.nama_lengkap " +
                "FROM tb_penjualan p " +
                "JOIN tb_customer c ON p.id_customer = c.id_customer " +
                "JOIN tb_barang   b ON p.id_barang   = b.id_barang " +
                "JOIN tb_user     u ON p.id_user     = u.id_user " +
                "WHERE DATE(p.tgl_transaksi) BETWEEN ? AND ? ");

            if (idCustomer != null) sql.append("AND p.id_customer = ? ");
            sql.append("ORDER BY p.tgl_transaksi ASC, p.id_jual ASC");

            PreparedStatement ps = conn.prepareStatement(sql.toString());
            ps.setString(1, dari);
            ps.setString(2, sampai);
            if (idCustomer != null) ps.setString(3, idCustomer);

            ResultSet rs = ps.executeQuery();

            int    no         = 1;
            int    totalTrx   = 0;
            int    totalItem  = 0;
            double totalUang  = 0;

            while (rs.next()) {
                String tgl    = rs.getString("tgl_transaksi");
                String noTrx  = String.format("TRX-%s-%03d",
                    tgl.replace("-",""), rs.getInt("id_jual"));
                int    jml    = rs.getInt("jumlah_beli");
                double harga  = rs.getDouble("harga_jual");
                double total  = rs.getDouble("total_bayar");

                modelTabel.addRow(new Object[]{
                    no++,
                    tgl,
                    noTrx,
                    rs.getString("nama_customer"),
                    rs.getString("nama_barang"),
                    jml,
                    "Rp " + formatRupiah(harga),
                    "Rp " + formatRupiah(total),
                    rs.getString("nama_lengkap")
                });

                totalTrx++;
                totalItem += jml;
                totalUang += total;
            }
            rs.close(); ps.close();

            // Update summary
            double rata = totalTrx > 0 ? totalUang / totalTrx : 0;
            lblTotalTrx.setText(String.valueOf(totalTrx));
            lblTotalItem.setText(String.valueOf(totalItem));
            lblRataRata.setText("Rp " + formatRupiah(rata));
            lblTotalPendapatan.setText("Rp " + formatRupiah(totalUang));

        } catch (SQLException e) {
            showError("Gagal memuat laporan: " + e.getMessage());
        }
    }

    private void cetakLaporan() {
        if (modelTabel.getRowCount() == 0) {
            showWarn("Tidak ada data untuk dicetak!"); return;
        }
        try {
            MessageFormat header = new MessageFormat(
                "Laporan Penjualan Toko Berkah Jaya\n" +
                "Periode: " + txtDari.getText() + " s/d " + txtSampai.getText());
            MessageFormat footer = new MessageFormat(
                "Halaman {0}  |  Dicetak: " +
                new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()));
            tabel.print(JTable.PrintMode.FIT_WIDTH, header, footer);
        } catch (java.awt.print.PrinterException e) {
            showError("Gagal mencetak: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    //  HELPER
    // ══════════════════════════════════════════════════════════
    private void setTanggalDefault() {
        // Default: bulan ini
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        txtDari.setText(sdf.format(cal.getTime()));
        txtSampai.setText(sdf.format(new Date()));
    }

    private void resetFilter() {
        setTanggalDefault();
        cmbCustomer.setSelectedIndex(0);
        tampilkanLaporan();
    }

    private String formatRupiah(double angka) {
        return String.format("%,.0f", angka).replace(",", ".");
    }

    private JTextField buatDateField() {
        JTextField f = new JTextField(10);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setPreferredSize(new Dimension(120, 30));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(3, 10, 3, 10)));
        f.setToolTipText("Format: YYYY-MM-DD");
        return f;
    }

    private JButton buatBtn(String text, Color bg, Color fg, int width) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(width, 30));
        return btn;
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

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
