package com.hideseek.model;

import java.awt.Image;

/**
 * Kelas ini merepresentasikan karakter utama (Pemain) yang dikendalikan pengguna.
 * Sebagai turunan dari GameElement, Player mewarisi properti fisik (posisi dan gambar),
 * namun memiliki tanggung jawab tambahan untuk mengelola data statistik permainan secara real-time.
 */
public class Player extends GameElement {

    // Variabel status permainan yang bersifat dinamis (berubah terus-menerus).
    private int ammo;       // Jumlah stok peluru yang dimiliki pemain.
    private int score;      // Total skor yang telah dikumpulkan.
    private int ammoMissed; // Statistik jumlah peluru musuh yang berhasil dihindari.

    /**
     * Konstruktor untuk inisialisasi awal pemain.
     * Saat permainan baru dimulai dari nol, seluruh statistik diatur ke nilai default (0).
     * Namun, jika fitur 'Load Game' aktif, nilai-nilai ini nanti akan ditimpa
     * menggunakan metode Setter.
     *
     * @param x Posisi awal horizontal.
     * @param y Posisi awal vertikal.
     * @param width Lebar karakter.
     * @param height Tinggi karakter.
     * @param image Aset gambar visual karakter.
     */
    public Player(int x, int y, int width, int height, Image image) {
        super(x, y, width, height, image);
        this.ammo = 0;
        this.score = 0;
        this.ammoMissed = 0;
    }

    // --- Logika Manipulasi Permainan (Gameplay Logic) ---

    /**
     * Menambahkan stok peluru pemain.
     * Metode ini biasanya dipanggil sebagai bentuk 'hadiah' (reward) ketika
     * pemain berhasil melakukan manuver tertentu (misalnya menghindari peluru musuh).
     */
    public void addAmmo(int amount) {
        this.ammo += amount;
    }

    /**
     * Mengurangi satu unit peluru saat pemain melakukan tembakan.
     * Terdapat mekanisme pengecekan (validasi) untuk memastikan jumlah peluru
     * tidak pernah bernilai negatif.
     */
    public void decreaseAmmo() {
        if (this.ammo > 0) {
            this.ammo--;
        }
    }

    /**
     * Menambahkan poin ke dalam skor total pemain.
     * Dipanggil oleh sistem ketika pemain berhasil menghancurkan target (Alien).
     */
    public void addScore(int points) {
        this.score += points;
    }

    /**
     * Mencatat statistik ketangkasan pemain.
     * Setiap kali peluru musuh lewat tanpa mengenai pemain, angka ini bertambah.
     * Data ini penting untuk perhitungan bonus di akhir permainan.
     */
    public void addAmmoMissed() {
        this.ammoMissed++;
    }

    // --- Metode Pengaturan Data (Setters untuk Fitur Load Game) ---
    /*
     * Saat pemain melanjutkan permainan (Continue), ViewModel akan mengambil data lama
     * dari database dan memasukkannya ke objek Player menggunakan setter ini.
     * Tanpa metode ini, status pemain akan selalu reset ke 0.
     */

    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setAmmoMissed(int ammoMissed) {
        this.ammoMissed = ammoMissed;
    }

    // --- Metode Akses Data (Getters) ---
    // Memberikan akses baca kepada komponen View (GameCanvas) untuk menampilkan
    // status terkini di layar (Heads-Up Display / HUD).

    public int getAmmo() { return ammo; }
    public int getScore() { return score; }
    public int getAmmoMissed() { return ammoMissed; }
}