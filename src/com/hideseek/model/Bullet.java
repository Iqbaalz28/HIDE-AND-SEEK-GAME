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

    /**
     * Konstruktor untuk menciptakan objek peluru.
     * * Selain mengatur posisi dan bentuk fisik melalui 'super', konstruktor ini
     * juga menetapkan status kepemilikan peluru. Status ini krusial untuk
     * menentukan arah gerak peluru dan deteksi tabrakan di ViewModel nantinya.
     *
     * @param isEnemyBullet Status apakah peluru ini milik musuh.
     */
    public Bullet(int x, int y, int width, int height, Image image, boolean isEnemyBullet) {
        super(x, y, width, height, image);
        this.isEnemyBullet = isEnemyBullet;
    }

    /**
     * Metode akses (getter) untuk memeriksa status kepemilikan peluru.
     * Digunakan oleh logika permainan untuk membedakan perlakuan antara
     * peluru kawan dan lawan.
     *
     * @return true jika peluru berasal dari Alien, false jika dari Player.
     */
    public boolean isEnemyBullet() {
        return isEnemyBullet;
    }
}