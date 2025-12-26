package com.hideseek.model;

import java.awt.Image;

/**
 * Representasi karakter Pemain (Player).
 *
 * Dalam arsitektur Strict OOP, kelas ini adalah "Smart Model".
 * Dia tidak hanya menyimpan data (skor/nyawa), tetapi juga memiliki logika perilaku
 * seperti bagaimana cara bergerak agar tidak keluar layar dan bagaimana cara membidik musuh.
 */
public class Player extends GameElement {

    // Data statistik permainan
    private int ammo;
    private int score;
    private int ammoMissed;

    public Player(int x, int y, int width, int height, Image image) {
        super(x, y, width, height, image);
        this.ammo = 0;
        this.score = 0;
        this.ammoMissed = 0;
    }

    /**
     * Logika Pergerakan Pemain.
     * * Metode ini menerima status tombol yang ditekan, lalu menghitung posisi baru.
     * Di sini juga terdapat logika validasi (Boundary Check) untuk memastikan
     * pemain tidak berjalan menembus batas tepi layar.
     */
    public void move(boolean left, boolean right, boolean up, boolean down, int screenW, int screenH) {
        int speed = 5; // Kecepatan gerak piksel per frame

        if (left && x > 0) x -= speed;
        if (right && x < screenW - width) x += speed;
        if (up && y > 0) y -= speed;
        if (down && y < screenH - height) y += speed;
    }

    /**
     * Mekanisme pembatalan gerak (Rollback).
     * Dipanggil oleh ViewModel jika pemain terdeteksi menabrak batu/dinding.
     * Pemain akan dipaksa mundur ke posisi sebelum tabrakan terjadi.
     */
    public void rollback(int oldX, int oldY) {
        this.x = oldX;
        this.y = oldY;
    }

    /**
     * Logika Menembak Terarah (Mouse Aiming).
     * * Di sinilah perhitungan Matematika Vektor terjadi.
     * Player menghitung sudut antara posisinya sendiri dengan posisi kursor mouse,
     * lalu menciptakan objek Peluru yang bergerak ke arah sudut tersebut.
     *
     * @param targetMouseX Koordinat X kursor mouse.
     * @param targetMouseY Koordinat Y kursor mouse.
     * @return Objek Bullet baru yang siap diluncurkan, atau null jika peluru habis.
     */
    public Bullet shootAt(int targetMouseX, int targetMouseY) {
        if (ammo <= 0) return null;

        // Titik asal tembakan (dari tengah badan player)
        double startX = this.x + 25;
        double startY = this.y + 25;

        // Mencari selisih jarak
        double deltaX = targetMouseX - startX;
        double deltaY = targetMouseY - startY;

        // Menghitung sudut (Arc Tangent)
        double angle = Math.atan2(deltaY, deltaX);

        // Kecepatan peluru player
        double bulletSpeed = 10.0;

        // Memecah kecepatan menjadi komponen X dan Y
        double velX = bulletSpeed * Math.cos(angle);
        double velY = bulletSpeed * Math.sin(angle);

        this.decreaseAmmo(); // Kurangi stok peluru

        // Mengembalikan peluru baru ke ViewModel
        return new Bullet((int)startX, (int)startY, 10, 20, null, false, velX, velY);
    }

    // --- Manajemen Statistik ---

    public void addAmmo(int amount) { this.ammo += amount; }
    public void decreaseAmmo() { if (this.ammo > 0) this.ammo--; }
    public void addScore(int points) { this.score += points; }
    public void addAmmoMissed() { this.ammoMissed++; }

    public int getAmmo() { return ammo; }
    public int getScore() { return score; }
    public int getAmmoMissed() { return ammoMissed; }

    // Setter khusus untuk fitur Load Game (mengembalikan status lama)
    public void setAmmo(int ammo) { this.ammo = ammo; }
    public void setScore(int score) { this.score = score; }
    public void setAmmoMissed(int ammoMissed) { this.ammoMissed = ammoMissed; }
}