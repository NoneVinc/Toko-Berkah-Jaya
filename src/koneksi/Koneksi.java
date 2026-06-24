package koneksi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Koneksi {

    // ======= SESUAIKAN INI =======
    private static final String HOST     = "localhost";
    private static final String PORT     = "3306";
    private static final String DATABASE = "db_toko_berkah_jaya";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";  // isi password MySQL kamu
    // ==============================

    private static final String URL =
        "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE +
        "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&connectTimeout=5000";

    private static Connection connection = null;

    // Ambil koneksi (Singleton — satu koneksi dipakai semua class)
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("DRIVER ERROR: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                "Driver MySQL tidak ditemukan!",
                "Error Koneksi", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            System.err.println("=== SQL CONNECTION ERROR ===");
            System.err.println("Message  : " + e.getMessage());
            System.err.println("SQLState : " + e.getSQLState());
            System.err.println("ErrorCode: " + e.getErrorCode());
            System.err.println("============================");
            JOptionPane.showMessageDialog(null,
                "Gagal konek!\n\n" + e.getMessage(),
                "Error Koneksi", JOptionPane.ERROR_MESSAGE);
        }
        return connection;
    }

    // Tutup koneksi
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error menutup koneksi: " + e.getMessage());
        }
    }
}