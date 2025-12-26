package com.hideseek.viewmodel;

import com.hideseek.model.UserRepository; // Import Repository
import com.hideseek.model.UserStats;
import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 *
 * ViewModel hanya meminta data ke Model (Repository).
 */
public class MenuViewModel {

    // Instansiasi Repository
    private UserRepository userRepo;

    public MenuViewModel() {
        this.userRepo = new UserRepository();
    }

    public DefaultTableModel getTableData() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Username");
        model.addColumn("Skor Total");
        model.addColumn("Peluru Meleset");
        model.addColumn("Sisa Peluru Terakhir");

        // PANGGIL MODEL (Repository), bukan DB langsung
        List<UserStats> users = userRepo.getAllUsers();

        // Mapping dari List Object ke Baris Tabel
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

    public UserStats getUserStats(String username) {
        // Pendelegasian tugas ke Repository
        return userRepo.getUserByUsername(username);
    }

    public void registerUser(String username) {
        // Pendelegasian tugas ke Repository
        userRepo.registerUser(username);
    }

    public void updateScore(String username, int totalScore, int totalMissed, int totalAmmo) {
        // Pendelegasian tugas ke Repository
        userRepo.updateUserStats(username, totalScore, totalMissed, totalAmmo);
    }
}