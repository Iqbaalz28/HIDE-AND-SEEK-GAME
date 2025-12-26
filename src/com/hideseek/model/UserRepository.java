package com.hideseek.model;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Kelas Repository (Bagian dari Layer Model).
 * Bertugas menangani operasi CRUD database secara langsung.
 */
public class UserRepository {

    // Mengambil semua data untuk tabel Highscore
    public List<UserStats> getAllUsers() {
        List<UserStats> userList = new ArrayList<>();
        DB db = null;
        try {
            db = new DB();
            String sql = "SELECT * FROM tbenefit ORDER BY skor DESC";
            ResultSet rs = db.createQuery(sql);

            while (rs.next()) {
                UserStats user = new UserStats(
                        rs.getString("username"),
                        rs.getInt("skor"),
                        rs.getInt("peluru_meleset"),
                        rs.getInt("sisa_peluru")
                );
                userList.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) db.closeConnection();
        }
        return userList;
    }

    // Mengambil data satu user
    public UserStats getUserByUsername(String username) {
        UserStats stats = new UserStats(username, 0, 0, 0);
        DB db = null;
        try {
            db = new DB();
            String sql = "SELECT * FROM tbenefit WHERE username = '" + username + "'";
            ResultSet rs = db.createQuery(sql);
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

    // Mendaftarkan user baru
    public void registerUser(String username) {
        DB db = null;
        try {
            db = new DB();
            String checkSql = "SELECT * FROM tbenefit WHERE username = '" + username + "'";
            ResultSet rs = db.createQuery(checkSql);

            if (!rs.next()) {
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

    // Update skor
    public void updateUserStats(String username, int score, int missed, int ammo) {
        DB db = null;
        try {
            db = new DB();
            String sql = "UPDATE tbenefit SET " +
                    "skor = " + score + ", " +
                    "peluru_meleset = " + missed + ", " +
                    "sisa_peluru = " + ammo + " " +
                    "WHERE username = '" + username + "'";
            db.createUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) db.closeConnection();
        }
    }
}