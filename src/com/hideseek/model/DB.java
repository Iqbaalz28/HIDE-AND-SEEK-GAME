package com.hideseek.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Kelas untuk menangani koneksi ke Database MySQL.
 */
public class DB {
    // Konfigurasi koneksi database
    private String dbUrl = "jdbc:mysql://localhost:3306/hide_seek_db?user=root&password=";
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    /**
     * Konstruktor: Melakukan koneksi ke database saat objek dibuat.
     */
    public DB() throws Exception {
        try {
            // Memuat driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Membuat koneksi
            conn = DriverManager.getConnection(dbUrl);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        } catch (SQLException e) {
            // Melempar error agar bisa ditangani di layer lain
            throw e;
        }
    }

    /**
     * Mengeksekusi query SELECT (mengambil data).
     * @param query String SQL query
     */
    public void createQuery(String query) throws SQLException {
        stmt = conn.createStatement();
        // Eksekusi query dan simpan hasilnya di ResultSet
        rs = stmt.executeQuery(query);
    }

    /**
     * Mengeksekusi query INSERT, UPDATE, atau DELETE.
     * @param query String SQL query
     * @return int jumlah baris yang terpengaruh
     */
    public int createUpdate(String query) throws SQLException {
        stmt = conn.createStatement();
        // Eksekusi update dan kembalikan jumlah baris yang berubah
        return stmt.executeUpdate(query);
    }

    /**
     * Mengambil hasil query (ResultSet).
     */
    public ResultSet getResult() {
        return rs;
    }

    /**
     * Menutup koneksi dan resource database untuk mencegah memory leak.
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

    // Getter untuk Connection jika kita butuh PreparedStatement nanti
    public Connection getConnection() {
        return conn;
    }
}