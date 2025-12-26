package com.hideseek.viewmodel;

import com.hideseek.model.UserRepository;
import com.hideseek.model.UserStats;
import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 * ViewModel untuk Menu Utama.
 *
 * Tugas utamanya adalah memisahkan logika tampilan menu dari logika pengambilan data.
 * MenuPanel (View) tidak perlu tahu cara query SQL atau cara koneksi database.
 * Ia cukup meminta "Tolong berikan saya data tabel", dan kelas inilah yang menyiapkannya.
 */
public class MenuViewModel {

    // Menggunakan Repository untuk mengakses data, bukan akses DB langsung.
    private UserRepository userRepo;

    public MenuViewModel() {
        this.userRepo = new UserRepository();
    }

    /**
     * Menyiapkan data untuk Tabel Peringkat (Highscore).
     * Metode ini mengambil daftar user dari Repository, lalu mengubahnya
     * menjadi format 'DefaultTableModel' yang bisa langsung ditempel ke JTable di Swing.
     *
     * @return Model tabel siap pakai berisi data username dan skor.
     */
    public DefaultTableModel getTableData() {
        // Membuat header kolom tabel
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Username");
        model.addColumn("Skor Total");
        model.addColumn("Peluru Meleset");
        model.addColumn("Sisa Peluru Terakhir");

        // Meminta data mentah dari Model (Repository)
        List<UserStats> users = userRepo.getAllUsers();

        // Mengonversi data objek menjadi baris-baris tabel
        for (UserStats u : users) {
            Object[] row = {
                    u.getUsername(),
                    u.getSkor(),
                    u.getPeluruMeleset(),
                    u.getSisaPeluru()
            };
            model.addRow(row);
        }

        return model;
    }

    // --- Wrapper Methods (Perantara) ---
    // Metode-metode di bawah ini meneruskan permintaan dari View ke Repository.

    public UserStats getUserStats(String username) {
        return userRepo.getUserByUsername(username);
    }

    public void registerUser(String username) {
        userRepo.registerUser(username);
    }
}