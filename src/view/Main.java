package view;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main - Entry point aplikasi Sistem Penjualan
 *
 * Cara menjalankan:
 *   - Di NetBeans: klik kanan project → Run
 *   - Via terminal: java -cp . view.Main
 *
 * Pastikan:
 *   - MySQL sudah berjalan
 *   - Database & tabel sudah dibuat (lihat Koneksi.java)
 *   - Driver mysql-connector-java sudah ada di Libraries
 */
public class Main {

    public static void main(String[] args) {

        // Gunakan Look & Feel sistem operasi agar tampilan lebih native
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback ke default Swing L&F jika gagal
            System.err.println("Gagal set LookAndFeel: " + e.getMessage());
        }

        // Jalankan aplikasi di Event Dispatch Thread (EDT) — wajib untuk Swing
        SwingUtilities.invokeLater(() -> {
            FormLogin formLogin = new FormLogin();
            formLogin.setVisible(true);
        });
    }
}
