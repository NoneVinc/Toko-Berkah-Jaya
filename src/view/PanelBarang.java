package view;

import koneksi.Koneksi;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class PanelBarang extends JPanel {

    // ── Warna tema ────────────────────────────────────────────
    private final Color PRIMARY      = new Color(33, 97, 140);
    private final Color HEADER_TBL   = new Color(26, 58, 92);
    private final Color BG           = new Color(240, 244, 248);
    private final Color CARD_BG      = Color.WHITE;
    private final Color BORDER_COLOR = new Color(226, 232, 240);
    private final Color TEXT_LABEL   = new Color(74, 85, 104);
    private final Color ORANGE       = new Color(243, 156, 18);
    private final Color RED          = new Color(192, 57, 43);

    // ── Komponen form ─────────────────────────────────────────
    private JTextField txtIdBarang, txtNamaBarang, txtSatuan, txtHarga, txtStok;
    private JComboBox<String> cmbKategori;
    private JButton btnSimpan, btnUpdate, btnHapus, btnReset;
    private JTextField txtCari;

    // ── Tabel ─────────────────────────────────────────────────
    private JTable tabel;
    private DefaultTableModel modelTabel;

    // ── State ─────────────────────────────────────────────────
    private boolean modeEdit = false;

    public PanelBarang() {
        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(buildTopBar(),  BorderLayout.NORTH);
        add(buildFormCard(), BorderLayout.CENTER);

        JScrollPane scroll = buildTableScroll();
        add(scroll, BorderLayout.SOUTH);

        loadKategori();
        loadDataBarang("");
        setModeDefault();
    }

    // ══════════════════════════════════════════════════════════
    //  TOP BAR
    // ══════════════════════════════════════════════════════════
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG);
        bar.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel title = new JLabel("Data Barang");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(new Color(26, 58, 92));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        searchPanel.setBackground(BG);

        JLabel lblCari = new JLabel("Cari:");
        lblCari.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCari.setForeground(TEXT_LABEL);

        txtCari = new JTextField(18);
        txtCari.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtCari.setPreferredSize(new Dimension(200, 30));
        txtCari.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(3, 10, 3, 10)
        ));
        txtCari.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                loadDataBarang(txtCari.getText().trim());
            }
        });

        searchPanel.add(lblCari);
        searchPanel.add(txtCari);

        bar.add(title, BorderLayout.WEST);
        bar.add(searchPanel, BorderLayout.EAST);
        return bar;
    }

    // ══════════════════════════════════════════════════════════
    //  FORM CARD
    // ══════════════════════════════════════════════════════════
    private JPanel buildFormCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        // Grid fields
        JPanel grid = new JPanel(new GridLayout(2, 3, 12, 8));
        grid.setBackground(CARD_BG);
        grid.setBorder(BorderFactory.createEmptyBorder(14, 16, 10, 16));

        txtIdBarang   = createField("ID Barang",   "Contoh: B005");
        txtNamaBarang = createField("Nama Barang",  "Nama produk...");
        txtSatuan     = createField("Satuan",        "Kg / Botol / Buah...");
        txtHarga      = createField("Harga Jual",    "0");
        txtStok       = createField("Stok",          "0");

        // ComboBox Kategori
        JPanel cmbWrapper = new JPanel(new BorderLayout(0, 4));
        cmbWrapper.setBackground(CARD_BG);
        JLabel lblKat = new JLabel("KATEGORI");
        lblKat.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblKat.setForeground(TEXT_LABEL);
        cmbKategori = new JComboBox<>();
        cmbKategori.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbKategori.setBackground(Color.WHITE);
        cmbWrapper.add(lblKat, BorderLayout.NORTH);
        cmbWrapper.add(cmbKategori, BorderLayout.CENTER);

        grid.add(wrapField("ID BARANG",   txtIdBarang));
        grid.add(wrapField("NAMA BARANG", txtNamaBarang));
        grid.add(cmbWrapper);
        grid.add(wrapField("SATUAN",      txtSatuan));
        grid.add(wrapField("HARGA JUAL",  txtHarga));
        grid.add(wrapField("STOK",        txtStok));

        // Tombol
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(new Color(247, 250, 252));
        btnRow.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));

        btnSimpan = createBtn("Simpan",  PRIMARY, Color.WHITE);
        btnUpdate = createBtn("Update",  ORANGE,  Color.WHITE);
        btnHapus  = createBtn("Hapus",   RED,     Color.WHITE);
        btnReset  = createBtn("Reset",
            Color.WHITE, new Color(113, 128, 150));
        btnReset.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        btnSimpan.addActionListener(e -> simpanBarang());
        btnUpdate.addActionListener(e -> updateBarang());
        btnHapus.addActionListener(e -> hapusBarang());
        btnReset.addActionListener(e -> resetForm());

        btnRow.add(btnSimpan);
        btnRow.add(btnUpdate);
        btnRow.add(btnHapus);
        btnRow.add(btnReset);

        card.add(grid,   BorderLayout.CENTER);
        card.add(btnRow, BorderLayout.SOUTH);
        return card;
    }

    // ══════════════════════════════════════════════════════════
    //  TABEL
    // ══════════════════════════════════════════════════════════
    private JScrollPane buildTableScroll() {
        String[] kolom = {"#", "ID Barang", "Nama Barang", "Kategori",
                          "Satuan", "Harga Jual", "Stok"};
        modelTabel = new DefaultTableModel(kolom, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tabel = new JTable(modelTabel);
        tabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabel.setRowHeight(32);
        tabel.setShowHorizontalLines(true);
        tabel.setGridColor(BORDER_COLOR);
        tabel.setSelectionBackground(new Color(235, 245, 255));
        tabel.setSelectionForeground(new Color(26, 58, 92));
        tabel.setIntercellSpacing(new Dimension(0, 1));
        tabel.setFocusable(false);

        // Header
        JTableHeader header = tabel.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(HEADER_TBL);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);

        // Lebar kolom
        int[] widths = {40, 80, 200, 120, 80, 110, 70};
        for (int i = 0; i < widths.length; i++) {
            tabel.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Klik baris → isi form
        tabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                isiFormDariTabel();
            }
        });

        JScrollPane scroll = new JScrollPane(tabel);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scroll.setPreferredSize(new Dimension(0, 220));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    // ══════════════════════════════════════════════════════════
    //  DATABASE OPERATIONS
    // ══════════════════════════════════════════════════════════
    private void loadKategori() {
        cmbKategori.removeAllItems();
        cmbKategori.addItem("-- Pilih Kategori --");
        try {
            Connection conn = Koneksi.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT id_kategori, nama_kategori FROM tb_kategori ORDER BY nama_kategori");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cmbKategori.addItem(rs.getInt("id_kategori") + " - " + rs.getString("nama_kategori"));
            }
            rs.close(); ps.close();
        } catch (SQLException e) {
            showError("Gagal memuat kategori: " + e.getMessage());
        }
    }

    private void loadDataBarang(String keyword) {
        modelTabel.setRowCount(0);
        try {
            Connection conn = Koneksi.getConnection();
            String sql = "SELECT b.id_barang, b.nama_barang, k.nama_kategori, " +
                         "b.satuan, b.harga_jual, b.stok " +
                         "FROM tb_barang b " +
                         "JOIN tb_kategori k ON b.id_kategori = k.id_kategori " +
                         "WHERE b.nama_barang LIKE ? OR b.id_barang LIKE ? " +
                         "ORDER BY b.id_barang";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            int no = 1;
            while (rs.next()) {
                String harga = "Rp " + String.format("%,.0f",
                    rs.getDouble("harga_jual")).replace(",", ".");
                modelTabel.addRow(new Object[]{
                    no++,
                    rs.getString("id_barang"),
                    rs.getString("nama_barang"),
                    rs.getString("nama_kategori"),
                    rs.getString("satuan"),
                    harga,
                    rs.getInt("stok")
                });
            }
            rs.close(); ps.close();
        } catch (SQLException e) {
            showError("Gagal memuat data: " + e.getMessage());
        }
    }

    private void simpanBarang() {
        if (!validasiForm()) return;
        try {
            Connection conn = Koneksi.getConnection();
            // Cek ID duplikat
            PreparedStatement cek = conn.prepareStatement(
                "SELECT id_barang FROM tb_barang WHERE id_barang = ?");
            cek.setString(1, txtIdBarang.getText().trim());
            if (cek.executeQuery().next()) {
                showWarn("ID Barang sudah ada! Gunakan ID lain.");
                return;
            }
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO tb_barang (id_barang,id_kategori,nama_barang,satuan,harga_jual,stok)" +
                " VALUES (?,?,?,?,?,?)");
            ps.setString(1, txtIdBarang.getText().trim());
            ps.setInt(2, getIdKategori());
            ps.setString(3, txtNamaBarang.getText().trim());
            ps.setString(4, txtSatuan.getText().trim());
            ps.setDouble(5, Double.parseDouble(txtHarga.getText().trim()));
            ps.setInt(6, Integer.parseInt(txtStok.getText().trim()));
            ps.executeUpdate();
            ps.close();
            JOptionPane.showMessageDialog(this, "✅ Data barang berhasil disimpan!",
                "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            loadDataBarang("");
        } catch (SQLException e) {
            showError("Gagal menyimpan: " + e.getMessage());
        }
    }

    private void updateBarang() {
        if (!modeEdit) { showWarn("Pilih baris pada tabel terlebih dahulu!"); return; }
        if (!validasiForm()) return;
        try {
            Connection conn = Koneksi.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE tb_barang SET id_kategori=?, nama_barang=?, satuan=?," +
                " harga_jual=?, stok=? WHERE id_barang=?");
            ps.setInt(1, getIdKategori());
            ps.setString(2, txtNamaBarang.getText().trim());
            ps.setString(3, txtSatuan.getText().trim());
            ps.setDouble(4, Double.parseDouble(txtHarga.getText().trim()));
            ps.setInt(5, Integer.parseInt(txtStok.getText().trim()));
            ps.setString(6, txtIdBarang.getText().trim());
            ps.executeUpdate();
            ps.close();
            JOptionPane.showMessageDialog(this, "✅ Data barang berhasil diupdate!",
                "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            loadDataBarang("");
        } catch (SQLException e) {
            showError("Gagal update: " + e.getMessage());
        }
    }

    private void hapusBarang() {
        if (!modeEdit) { showWarn("Pilih baris pada tabel terlebih dahulu!"); return; }
        int opt = JOptionPane.showConfirmDialog(this,
            "Hapus barang \"" + txtNamaBarang.getText() + "\"?",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opt != JOptionPane.YES_OPTION) return;
        try {
            Connection conn = Koneksi.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM tb_barang WHERE id_barang = ?");
            ps.setString(1, txtIdBarang.getText().trim());
            ps.executeUpdate();
            ps.close();
            JOptionPane.showMessageDialog(this, "✅ Data barang berhasil dihapus!",
                "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            loadDataBarang("");
        } catch (SQLException e) {
            showError("Gagal hapus: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    //  HELPER
    // ══════════════════════════════════════════════════════════
    private void isiFormDariTabel() {
        int row = tabel.getSelectedRow();
        if (row < 0) return;
        txtIdBarang.setText(modelTabel.getValueAt(row, 1).toString());
        txtNamaBarang.setText(modelTabel.getValueAt(row, 2).toString());
        txtSatuan.setText(modelTabel.getValueAt(row, 4).toString());
        String harga = modelTabel.getValueAt(row, 5).toString()
            .replace("Rp ", "").replace(".", "");
        txtHarga.setText(harga);
        txtStok.setText(modelTabel.getValueAt(row, 6).toString());

        // Set kategori di combobox
        String katNama = modelTabel.getValueAt(row, 3).toString();
        for (int i = 0; i < cmbKategori.getItemCount(); i++) {
            if (cmbKategori.getItemAt(i).contains(katNama)) {
                cmbKategori.setSelectedIndex(i); break;
            }
        }

        txtIdBarang.setEditable(false);
        modeEdit = true;
        setModeEdit();
    }

    private void resetForm() {
        txtIdBarang.setText("");
        txtNamaBarang.setText("");
        txtSatuan.setText("");
        txtHarga.setText("");
        txtStok.setText("");
        cmbKategori.setSelectedIndex(0);
        txtIdBarang.setEditable(true);
        modeEdit = false;
        tabel.clearSelection();
        setModeDefault();
    }

    private void setModeDefault() {
        btnSimpan.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnHapus.setEnabled(false);
        btnUpdate.setBackground(new Color(200, 200, 200));
        btnHapus.setBackground(new Color(200, 200, 200));
    }

    private void setModeEdit() {
        btnSimpan.setEnabled(false);
        btnUpdate.setEnabled(true);
        btnHapus.setEnabled(true);
        btnUpdate.setBackground(ORANGE);
        btnHapus.setBackground(RED);
    }

    private int getIdKategori() {
        String selected = (String) cmbKategori.getSelectedItem();
        if (selected == null || selected.startsWith("--")) return 0;
        return Integer.parseInt(selected.split(" - ")[0].trim());
    }

    private boolean validasiForm() {
        if (txtIdBarang.getText().trim().isEmpty()) {
            showWarn("ID Barang tidak boleh kosong!"); return false; }
        if (txtNamaBarang.getText().trim().isEmpty()) {
            showWarn("Nama Barang tidak boleh kosong!"); return false; }
        if (cmbKategori.getSelectedIndex() == 0) {
            showWarn("Pilih kategori terlebih dahulu!"); return false; }
        try { Double.parseDouble(txtHarga.getText().trim()); }
        catch (NumberFormatException e) {
            showWarn("Harga Jual harus berupa angka!"); return false; }
        try { Integer.parseInt(txtStok.getText().trim()); }
        catch (NumberFormatException e) {
            showWarn("Stok harus berupa angka!"); return false; }
        return true;
    }

    // ── UI builder helpers ────────────────────────────────────
    private JTextField createField(String label, String hint) {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        return f;
    }

    private JPanel wrapField(String labelText, JTextField field) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 4));
        wrapper.setBackground(CARD_BG);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(TEXT_LABEL);
        wrapper.add(lbl,   BorderLayout.NORTH);
        wrapper.add(field, BorderLayout.CENTER);
        return wrapper;
    }

    private JButton createBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 34));
        return btn;
    }

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
