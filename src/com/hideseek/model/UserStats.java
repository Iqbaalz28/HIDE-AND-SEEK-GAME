package com.hideseek.model;

/**
 * Data Transfer Object (DTO) untuk Statistik Pengguna.
 * * Kelas ini tidak memiliki logika permainan. Tugasnya murni hanya sebagai "Amplop"
 * atau wadah untuk membawa data dari Database (tabel tbenefit) agar bisa
 * dibaca oleh bagian lain aplikasi dengan mudah.
 */
public class UserStats {

    // Representasi kolom-kolom di tabel database
    private String username;
    private int skor;
    private int peluruMeleset;
    private int sisaPeluru;

    // Konstruktor Kosong (diperlukan untuk fleksibilitas instansiasi)
    public UserStats() {
    }

    // Konstruktor Lengkap (untuk pengisian data cepat)
    public UserStats(String username, int skor, int peluruMeleset, int sisaPeluru) {
        this.username = username;
        this.skor = skor;
        this.peluruMeleset = peluruMeleset;
        this.sisaPeluru = sisaPeluru;
    }

    // --- Getter dan Setter Standar ---

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getSkor() { return skor; }
    public void setSkor(int skor) { this.skor = skor; }

    public int getPeluruMeleset() { return peluruMeleset; }
    public void setPeluruMeleset(int peluruMeleset) { this.peluruMeleset = peluruMeleset; }

    public int getSisaPeluru() { return sisaPeluru; }
    public void setSisaPeluru(int sisaPeluru) { this.sisaPeluru = sisaPeluru; }
}