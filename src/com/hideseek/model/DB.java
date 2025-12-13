package com.hideseek.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Kelas utilitas basis data yang menangani seluruh operasi koneksi ke server MySQL.
 * Kelas ini membungkus kompleksitas JDBC (Java Database Connectivity) sehingga
 * bagian lain dari aplikasi (seperti ViewModel) dapat meminta data tanpa perlu
 * mengatur detail teknis koneksi secara berulang.
 */
public class DB {

    // Konfigurasi string koneksi (Connection String) yang mendefinisikan alamat server,
    // port, nama database, serta kredensial pengguna (username dan password).
    private String dbUrl = "jdbc:mysql://localhost:3306/hide_seek_db?user=root&password=";

    // Objek-objek inti JDBC untuk mengelola sesi dan eksekusi perintah SQL.
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    /**
     * Konstruktor kelas DB.
     * * Setiap kali instansi kelas ini dibuat, ia akan mencoba memuat driver JDBC
     * dan membuka koneksi baru ke database. Proses ini dibungkus dalam blok
     * penanganan error untuk mengantisipasi kegagalan koneksi (misalnya server mati).
     */
    public DB() throws Exception {
        try {
            // Memastikan driver MySQL tersedia di library proyek.
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Membuka jalur komunikasi ke database menggunakan URL konfigurasi.
            conn = DriverManager.getConnection(dbUrl);

            // Mengatur mode isolasi transaksi untuk membaca data.
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        } catch (SQLException e) {
            // Jika terjadi kesalahan koneksi, error dilempar agar bisa diketahui oleh pemanggil.
            throw e;
        }
    }

    /**
     * Metode untuk menjalankan perintah SQL yang bersifat mengambil data (SELECT).
     * * Metode ini membuat statement baru dan langsung mengembalikan objek ResultSet
     * yang berisi tabel data hasil pencarian dari database.
     *
     * @param query String perintah SQL (contoh: "SELECT * FROM tbenefit").
     * @return ResultSet berisi data hasil query.
     */
    public ResultSet createQuery(String query) throws SQLException {
        stmt = conn.createStatement();
        // Mengeksekusi query dan mengembalikan pointer ke hasil data.
        return stmt.executeQuery(query);
    }

    /**
     * Metode untuk menjalankan perintah SQL yang bersifat mengubah data
     * (INSERT, UPDATE, DELETE).
     * * Berbeda dengan createQuery, metode ini mengembalikan angka integer yang
     * merepresentasikan jumlah baris data yang terpengaruh oleh perintah tersebut.
     *
     * @param query String perintah SQL manipulasi data.
     * @return Jumlah baris yang berhasil diubah/ditambah/dihapus.
     */
    public int createUpdate(String query) throws SQLException {
        stmt = conn.createStatement();
        return stmt.executeUpdate(query);
    }

    /**
     * Metode akses untuk mendapatkan hasil query terakhir yang disimpan di variabel lokal.
     * Ini berguna jika hasil query perlu diakses kembali tanpa eksekusi ulang,
     * meskipun pada praktik modern biasanya menggunakan return value dari createQuery.
     */
    public ResultSet getResult() {
        return rs;
    }

    /**
     * Metode pembersihan sumber daya (Resource Cleanup).
     * * Sangat penting untuk menutup ResultSet, Statement, dan Connection setelah
     * selesai digunakan agar tidak membebani memori server database dan mencegah
     * kebocoran koneksi (connection leak).
     */
    public void closeConnection() {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metode akses untuk mendapatkan objek koneksi mentah.
     */
    public Connection getConnection() {
        return conn;
    }
}