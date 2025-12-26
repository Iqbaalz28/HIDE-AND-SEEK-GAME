package com.hideseek.model;

import java.awt.Image;

/**
 * Representasi proyektil Peluru.
 * * Kelas ini unik karena menggunakan perhitungan posisi presisi tinggi (double)
 * untuk mendukung pergerakan miring (vektor) yang halus. Jika hanya menggunakan
 * integer, pergerakan peluru akan terlihat patah-patah saat bergerak diagonal.
 */
public class Bullet extends GameElement {

    // Penanda kepemilikan: True = punya musuh (melukai player), False = punya player (melukai musuh)
    private boolean isEnemyBullet;

    // Komponen kecepatan vektor (Horizontal & Vertikal)
    private double velocityX;
    private double velocityY;

    // Penyimpanan koordinat presisi tinggi (di belakang layar)
    private double preciseX;
    private double preciseY;

    public Bullet(int x, int y, int width, int height, Image image, boolean isEnemyBullet, double velocityX, double velocityY) {
        super(x, y, width, height, image);
        this.isEnemyBullet = isEnemyBullet;
        this.velocityX = velocityX;
        this.velocityY = velocityY;

        // Set posisi awal
        this.preciseX = x;
        this.preciseY = y;
    }

    /**
     * Memperbarui posisi peluru setiap frame.
     * Menambahkan kecepatan ke posisi presisi, lalu mengonversinya
     * menjadi integer agar bisa digambar di layar oleh View.
     */
    public void move() {
        preciseX += velocityX;
        preciseY += velocityY;

        this.x = (int) preciseX;
        this.y = (int) preciseY;
    }

    public boolean isEnemyBullet() {
        return isEnemyBullet;
    }
}