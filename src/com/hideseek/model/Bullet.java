package com.hideseek.model;

import java.awt.Image;

/**
 * Kelas ini merepresentasikan objek peluru atau proyektil yang ditembakkan di dalam permainan.
 * * Sama seperti Alien, kelas ini mewarisi GameElement untuk properti fisiknya.
 * Namun, Bullet memiliki logika tambahan untuk membedakan apakah peluru tersebut
 * berbahaya bagi pemain (ditembakkan musuh) atau berbahaya bagi musuh (ditembakkan pemain).
 */
public class Bullet extends GameElement {

    // Variabel penanda (flag) untuk mengidentifikasi pemilik peluru.
    // Jika bernilai true, maka ini adalah peluru musuh. Jika false, ini peluru pemain.
    private boolean isEnemyBullet;

    // Variabel untuk menyimpan kecepatan vektor (X dan Y)
    // Menggunakan double agar pergerakan miring lebih presisi dan halus
    private double velocityX;
    private double velocityY;

    // Variabel bantu untuk menyimpan posisi presisi (karena koordinat layar int)
    private double preciseX;
    private double preciseY;

    /**
     * Konstruktor Bullet.
     * menerima parameter arah kecepatan (dx, dy).
     *
     * @param velocityX Kecepatan gerak horizontal per frame.
     * @param velocityY Kecepatan gerak vertikal per frame.
     */
    public Bullet(int x, int y, int width, int height, Image image, boolean isEnemyBullet, double velocityX, double velocityY) {
        super(x, y, width, height, image);
        this.isEnemyBullet = isEnemyBullet;
        this.velocityX = velocityX;
        this.velocityY = velocityY;

        // Inisialisasi posisi presisi sama dengan posisi awal
        this.preciseX = x;
        this.preciseY = y;
    }

    /**
     * Metode untuk menggerakkan peluru berdasarkan kecepatannya saat ini.
     * Dipanggil setiap frame oleh ViewModel.
     */
    public void move() {
        // Update posisi presisi (double)
        preciseX += velocityX;
        preciseY += velocityY;

        // Update posisi aktual (int) untuk rendering di layar
        this.x = (int) preciseX;
        this.y = (int) preciseY;
    }

    public boolean isEnemyBullet() {
        return isEnemyBullet;
    }
}