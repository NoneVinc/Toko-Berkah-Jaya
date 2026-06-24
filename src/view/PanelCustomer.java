package view;

import koneksi.Koneksi;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.text.*;

public class PanelCustomer extends JPanel {

    private final Color PRIMARY      = new Color(33, 97, 140);
    private final Color HEADER_TBL   = new Color(26, 58, 92);
    private final Color BG           = new Color(240, 244, 248);
    private final Color CARD_BG      = Color.WHITE;
    private final Color BORDER_COLOR = new Color(226, 232, 240);
    private final Color TEXT_LABEL   = new Color(74, 85, 104);
    private final Color ORANGE       = new Color(243, 156, 18);
    private final Color RED          = new Color(192, 57, 43);

    private JTextField txtIdCustomer, txtNamaCustomer, txtTelepon;
    private JTextArea  txtAlamat;
    private JButton    btnSimpan, btnUpdate, btnHapus, btnReset;
    private JTextField txtCari;
    private JTable     tabel;
    private DefaultTableModel modelTabel;
    private boolean modeEdit = false;

    public PanelCustomer() {
        setLayout(new BorderLayout(0, 12));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(buildTopBar(),      BorderLayout.NORTH);
        add(buildFormCard(),    BorderLayout.CENTER);
        add(buildTableScroll(), BorderLayout.SOUTH);
        loadDataCustomer("");
        setModeDefault();
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG);
        bar.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));

        JLabel title = new JLabel("Data Customer");
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
            BorderFactory.createEmptyBorder(3, 10, 3, 10)));
        txtCari.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                loadDataCustomer(txtCari.getText().trim());
            }
        });

        searchPanel.add(lblCari);
        searchPanel.add(txtCari);
        bar.add(title, BorderLayout.WEST);
        bar.add(searchPanel, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildFormCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        JPanel grid = new JPanel(new GridLayout(2, 2, 12, 8));
        grid.setBackground(CARD_BG);
        grid.setBorder(BorderFactory.createEmptyBorder(14, 16, 10, 16));

        txtIdCustomer   = buatTextField();
        txtNamaCustomer = buatTextField();
        txtTelepon      = buatTextField();

        // Nama: hanya huruf & spasi
//        ((AbstractDocument) txtNamaCustomer.getDocument())
//            .setDocumentFilter(new DocumentFilter() {
//                @Override
//                public void insertString(FilterBypass fb, int off, String str,
//                        AttributeSet a) throws BadLocationException {
//                    if (str != null && str.matches("[a-zA-Z ]+")) super.insertString(fb, off, str, a);
//                }
//                @Override
//                public void replace(FilterBypass fb, int off, int len, String str,
//                        AttributeSet a) throws BadLocationException {
//                    if (str != null && str.matches("[a-zA-Z ]*")) super.replace(fb, off, len, str, a);
//                }
//            });

        // Telepon: hanya angka
//        ((AbstractDocument) txtTelepon.getDocument())
//            .setDocumentFilter(new DocumentFilter() {
//                @Override
//                public void insertString(FilterBypass fb, int off, String str,
//                        AttributeSet a) throws BadLocationException {
//                    if (str != null && str.matches("[0-9]+")) super.insertString(fb, off, str, a);
//                }
//                @Override
//                public void replace(FilterBypass fb, int off, int len, String str,
//                        AttributeSet a) throws BadLocationException {
//                    if (str != null && str.matches("[0-9]*")) super.replace(fb, off, len, str, a);
//                }
//            });

        txtAlamat = new JTextArea(3, 20);
        txtAlamat.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);
        txtAlamat.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));

        JPanel alamatWrapper = new JPanel(new BorderLayout(0, 4));
        alamatWrapper.setBackground(CARD_BG);
        alamatWrapper.add(buatLabel("ALAMAT"), BorderLayout.NORTH);
        JScrollPane alamatScroll = new JScrollPane(txtAlamat);
        alamatScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        alamatWrapper.add(alamatScroll, BorderLayout.CENTER);

        grid.add(wrapField("ID CUSTOMER",   txtIdCustomer));
        grid.add(wrapField("NAMA CUSTOMER", txtNamaCustomer));
        grid.add(wrapField("NO. TELEPON",   txtTelepon));
        grid.add(alamatWrapper);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(new Color(247, 250, 252));
        btnRow.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        btnSimpan = buatBtn("Simpan", PRIMARY, Color.WHITE);
        btnUpdate = buatBtn("Update", ORANGE,  Color.WHITE);
        btnHapus  = buatBtn("Hapus",  RED,     Color.WHITE);
        btnReset  = buatBtn("Reset",  Color.WHITE, new Color(113, 128, 150));
        btnReset.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        btnSimpan.addActionListener(e -> simpanCustomer());
        btnUpdate.addActionListener(e -> updateCustomer());
        btnHapus.addActionListener(e  -> hapusCustomer());
        btnReset.addActionListener(e  -> resetForm());

        btnRow.add(btnSimpan);
        btnRow.add(btnUpdate);
        btnRow.add(btnHapus);
        btnRow.add(btnReset);

        card.add(grid,   BorderLayout.CENTER);
        card.add(btnRow, BorderLayout.SOUTH);
        return card;
    }

    private JScrollPane buildTableScroll() {
        String[] kolom = {"#", "ID Customer", "Nama Customer", "No. Telepon", "Alamat"};
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
        tabel.setFocusable(false);

        JTableHeader header = tabel.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(HEADER_TBL);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);

        int[] widths = {40, 100, 200, 130, 300};
        for (int i = 0; i < widths.length; i++)
            tabel.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        tabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { isiFormDariTabel(); }
        });

        JScrollPane scroll = new JScrollPane(tabel);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scroll.setPreferredSize(new Dimension(0, 220));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private void loadDataCustomer(String keyword) {
        modelTabel.setRowCount(0);
        try {
            Connection conn = Koneksi.getConnection();
            String sql = "SELECT * FROM tb_customer " +
                         "WHERE nama_customer LIKE ? OR id_customer LIKE ? " +
                         "ORDER BY id_customer";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            int no = 1;
            while (rs.next()) {
                modelTabel.addRow(new Object[]{
                    no++,
                    rs.getString("id_customer"),
                    rs.getString("nama_customer"),
                    rs.getString("telepon"),
                    rs.getString("alamat")
                });
            }
            rs.close(); ps.close();
        } catch (SQLException e) {
            showError("Gagal memuat data: " + e.getMessage());
        }
    }

    private void simpanCustomer() {
        if (!validasiForm()) return;
        try {
            Connection conn = Koneksi.getConnection();
            PreparedStatement cek = conn.prepareStatement(
                "SELECT id_customer FROM tb_customer WHERE id_customer = ?");
            cek.setString(1, txtIdCustomer.getText().trim());
            if (cek.executeQuery().next()) {
                showWarn("ID Customer sudah ada! Gunakan ID lain."); return;
            }
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO tb_customer (id_customer, nama_customer, alamat, telepon)" +
                " VALUES (?, ?, ?, ?)");
            ps.setString(1, txtIdCustomer.getText().trim());
            ps.setString(2, txtNamaCustomer.getText().trim());
            ps.setString(3, txtAlamat.getText().trim());
            ps.setString(4, txtTelepon.getText().trim());
            ps.executeUpdate();
            ps.close();
            JOptionPane.showMessageDialog(this, "✅ Data customer berhasil disimpan!",
                "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            loadDataCustomer("");
        } catch (SQLException e) {
            showError("Gagal menyimpan: " + e.getMessage());
        }
    }

    private void updateCustomer() {
        if (!modeEdit) { showWarn("Pilih baris pada tabel terlebih dahulu!"); return; }
        if (!validasiForm()) return;
        try {
            Connection conn = Koneksi.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE tb_customer SET nama_customer=?, alamat=?, telepon=?" +
                " WHERE id_customer=?");
            ps.setString(1, txtNamaCustomer.getText().trim());
            ps.setString(2, txtAlamat.getText().trim());
            ps.setString(3, txtTelepon.getText().trim());
            ps.setString(4, txtIdCustomer.getText().trim());
            ps.executeUpdate();
            ps.close();
            JOptionPane.showMessageDialog(this, "✅ Data customer berhasil diupdate!",
                "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            loadDataCustomer("");
        } catch (SQLException e) {
            showError("Gagal update: " + e.getMessage());
        }
    }

    private void hapusCustomer() {
        if (!modeEdit) { showWarn("Pilih baris pada tabel terlebih dahulu!"); return; }
        int opt = JOptionPane.showConfirmDialog(this,
            "Hapus customer \"" + txtNamaCustomer.getText() + "\"?",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opt != JOptionPane.YES_OPTION) return;
        try {
            Connection conn = Koneksi.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM tb_customer WHERE id_customer = ?");
            ps.setString(1, txtIdCustomer.getText().trim());
            ps.executeUpdate();
            ps.close();
            JOptionPane.showMessageDialog(this, "✅ Data customer berhasil dihapus!",
                "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            loadDataCustomer("");
        } catch (SQLException e) {
            showError("Gagal hapus (pastikan tidak ada transaksi terkait): " + e.getMessage());
        }
    }

    private void isiFormDariTabel() {
        int row = tabel.getSelectedRow();
        if (row < 0) return;
        txtIdCustomer.setText(modelTabel.getValueAt(row, 1).toString());
        txtNamaCustomer.setText(modelTabel.getValueAt(row, 2).toString());
        txtTelepon.setText(modelTabel.getValueAt(row, 3).toString());
        txtAlamat.setText(modelTabel.getValueAt(row, 4).toString());
        txtIdCustomer.setEditable(false);
        modeEdit = true;
        setModeEdit();
    }

    private void resetForm() {
        txtIdCustomer.setText("");
        txtNamaCustomer.setText("");
        txtTelepon.setText("");
        txtAlamat.setText("");
        txtIdCustomer.setEditable(true);
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

    private boolean validasiForm() {
        if (txtIdCustomer.getText().trim().isEmpty()) {
            showWarn("ID Customer tidak boleh kosong!"); return false; }
        if (txtNamaCustomer.getText().trim().isEmpty()) {
            showWarn("Nama Customer tidak boleh kosong!"); return false; }
        if (!txtNamaCustomer.getText().trim().matches("[a-zA-Z ]+")) {
            showWarn("Nama Customer hanya boleh berisi huruf!"); return false; }
        if (txtTelepon.getText().trim().isEmpty()) {
            showWarn("No. Telepon tidak boleh kosong!"); return false; }
        if (!txtTelepon.getText().trim().matches("[0-9]+")) {
            showWarn("No. Telepon hanya boleh berisi angka!"); return false; }
        return true;
    }

    private JTextField buatTextField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        return f;
    }

    private JLabel buatLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(TEXT_LABEL);
        return lbl;
    }

    private JPanel wrapField(String labelText, JTextField field) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 4));
        wrapper.setBackground(CARD_BG);
        wrapper.add(buatLabel(labelText), BorderLayout.NORTH);
        wrapper.add(field, BorderLayout.CENTER);
        return wrapper;
    }

    private JButton buatBtn(String text, Color bg, Color fg) {
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