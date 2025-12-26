package com.hideseek.model;

import java.awt.Image;

/**
 * Representasi musuh (Alien).
 * * Kelas ini memiliki kecerdasan terbatas untuk bergerak lurus dan
 * kemampuan untuk membidik target (Pemain) secara otomatis.
 */
public class Alien extends GameElement {

    public Alien(int x, int y, int width, int height, Image image) {
        super(x, y, width, height, image);
    }

    /**
     * Logika pergerakan Alien.
     * Saat ini polanya sederhana: terus bergerak maju (ke arah atas layar).
     */
    public void move() {
        this.y -= 3;
    }

    /**
     * Logika Menembak Alien (Auto-Aim).
     * * Alien menerima data target (Pemain), menghitung posisinya,
     * lalu menembakkan peluru yang mengarah tepat ke posisi pemain saat itu.
     *
     * @param target Objek yang ingin ditembak adalah Player.
     * @return Objek Bullet musuh.
     */
    public Bullet shootAt(GameElement target) {
        // Titik asal (tengah alien)
        double startX = this.x + 20;
        double startY = this.y + 20;

        // Titik target (tengah player)
        double targetX = target.getX() + (double)target.getWidth()/2;
        double targetY = target.getY() + (double)target.getHeight()/2;

        // Hitung sudut tembakan
        double deltaX = targetX - startX;
        double deltaY = targetY - startY;
        double angle = Math.atan2(deltaY, deltaX);

        // Kecepatan peluru alien (sedikit lebih lambat dari player)
        double bulletSpeed = 7.0;
        double velX = bulletSpeed * Math.cos(angle);
        double velY = bulletSpeed * Math.sin(angle);

        // Buat peluru dengan flag isEnemy = true
        return new Bullet((int)startX, (int)startY, 10, 20, null, true, velX, velY);
    }
}