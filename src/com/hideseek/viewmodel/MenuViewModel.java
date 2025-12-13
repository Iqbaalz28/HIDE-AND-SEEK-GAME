package com.hideseek.viewmodel;

import com.hideseek.model.DB;
import com.hideseek.model.UserStats;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;

/**
 * Kelas ViewModel ini berfungsi sebagai pengelola logika bisnis untuk Menu Utama.
 *
 * Dalam arsitektur MVVM, kelas ini memisahkan logika database yang kompleks dari
 * tampilan UI (MainMenuPanel). Tugas utamanya adalah menyediakan data siap pakai
 * untuk tabel skor, memuat status pemain lama (Load Game), serta menyimpan
 * progres permainan terakhir ke dalam basis data.
 */
public class MenuViewModel {

    /**
     * Menyusun model data untuk ditampilkan pada tabel High Score (JTable).
     *
     * Metode ini melakukan kueri ke database untuk mengambil seluruh daftar pemain,
     * mengurutkannya berdasarkan skor tertinggi, dan mengemasnya ke dalam objek
     * DefaultTableModel agar dapat langsung dirender oleh komponen Swing di View.
     *
     * @return Objek model tabel yang berisi daftar peringkat pemain.
     */
    public DefaultTableModel getTableData() {
        // Membuat struktur tabel dengan kolom-kolom yang sesuai.
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Username");
        model.addColumn("Skor Total");
        model.addColumn("Peluru Meleset");
        model.addColumn("Sisa Peluru Terakhir");

        DB db = null;
        try {
            // Membuka koneksi dan mengambil data yang sudah diurutkan (DESC).
            db = new DB();
            String sql = "SELECT * FROM tbenefit ORDER BY skor DESC";
            ResultSet rs = db.createQuery(sql);

            // Melakukan iterasi pada hasil query dan memindahkannya ke baris tabel.
            while (rs.next()) {
                Object[] row = {
                        rs.getString("username"),
                        rs.getInt("skor"),
                        rs.getInt("peluru_meleset"),
                        rs.getInt("sisa_peluru")
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Memastikan koneksi database selalu ditutup untuk mencegah kebocoran memori.
            if (db != null) db.closeConnection();
        }
        return model;
    }

    /**
     * Mengambil data statistik spesifik milik satu pengguna.
     *
     * Metode ini sangat krusial untuk fitur "Load Game" atau "Continue". Sebelum
     * permainan dimulai, sistem memanggil fungsi ini untuk mengetahui berapa sisa
     * peluru dan skor terakhir pemain, sehingga mereka tidak perlu mulai dari nol.
     *
     * @param username Nama pengguna yang akan dicari datanya.
     * @return Objek UserStats berisi data pemain, atau data kosong jika tidak ditemukan.
     */
    public UserStats getUserStats(String username) {
        // Menyiapkan objek default dengan nilai 0 untuk mengantisipasi data kosong.
        UserStats stats = new UserStats(username, 0, 0, 0);

        DB db = null;
        try {
            db = new DB();
            String sql = "SELECT * FROM tbenefit WHERE username = '" + username + "'";
            ResultSet rs = db.createQuery(sql);

            // Jika data ditemukan, isi objek stats dengan nilai dari database.
            if (rs.next()) {
                stats.setSkor(rs.getInt("skor"));
                stats.setPeluruMeleset(rs.getInt("peluru_meleset"));
                stats.setSisaPeluru(rs.getInt("sisa_peluru"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) db.closeConnection();
        }
        return stats;
    }

    /**
     * Mendaftarkan pengguna baru ke dalam sistem.
     *
     * Sebelum memulai permainan, metode ini memeriksa apakah username sudah ada.
     * Jika belum ada, sistem akan membuat baris data baru (INSERT) dengan nilai awal nol.
     * Jika sudah ada, metode ini tidak melakukan apa-apa (permainan akan melanjutkannya).
     *
     * @param username Nama yang diinputkan pengguna di menu.
     */
    public void registerUser(String username) {
        DB db = null;
        try {
            db = new DB();
            // Langkah validasi: Cek keberadaan user.
            String checkSql = "SELECT * FROM tbenefit WHERE username = '" + username + "'";
            ResultSet rs = db.createQuery(checkSql);

            if (!rs.next()) {
                // Jika user baru, buat rekor baru di database.
                String insertSql = "INSERT INTO tbenefit (username, skor, peluru_meleset, sisa_peluru) " +
                        "VALUES ('" + username + "', 0, 0, 0)";
                db.createUpdate(insertSql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) db.closeConnection();
        }
    }

    /**
     * Menyimpan hasil akhir permainan ke database.
     *
     * Metode ini dipanggil saat Game Over. Karena logika akumulasi skor (penjumlahan
     * skor lama + skor baru) sudah dilakukan di dalam objek Player selama permainan,
     * metode ini hanya perlu menimpa (UPDATE) data lama di database dengan nilai total
     * yang baru.
     *
     * @param username Identitas pemain.
     * @param totalScore Total skor akhir.
     * @param totalMissed Total statistik hindaran.
     * @param totalAmmo Sisa peluru terakhir.
     */
    public void updateScore(String username, int totalScore, int totalMissed, int totalAmmo) {
        DB db = null;
        try {
            db = new DB();
            String sql = "UPDATE tbenefit SET " +
                    "skor = " + totalScore + ", " +
                    "peluru_meleset = " + totalMissed + ", " +
                    "sisa_peluru = " + totalAmmo + " " +
                    "WHERE username = '" + username + "'";

            db.createUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) db.closeConnection();
        }
    }
}