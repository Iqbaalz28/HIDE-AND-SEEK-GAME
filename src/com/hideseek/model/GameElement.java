package com.hideseek.model;

import java.awt.Image;
import java.awt.Rectangle;

/**
 * Kelas Abstrak yang menjadi "Cetak Biru" (Blueprint) bagi semua objek visual di dalam game.
 *
 * Mengapa kelas ini ada?
 * Agar tidak perlu menulis ulang kode untuk posisi (x, y) dan gambar di setiap objek
 * seperti Player, Alien, atau Batu. Semua objek tersebut mewarisi sifat dasar dari kelas ini.
 */
public abstract class GameElement {

    // Properti dasar yang pasti dimiliki oleh benda apa pun di layar.
    // Kita gunakan akses 'protected' agar kelas anak (Subclass) bisa mengaksesnya langsung.
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected Image image;

    public GameElement(int x, int y, int width, int height, Image image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
    }

    /**
     * Logika Deteksi Tabrakan (Collision Detection).
     *
     * Sesuai prinsip Strict OOP "Tell, Don't Ask", objek inilah yang seharusnya tahu
     * cara memeriksa apakah dia menabrak objek lain atau tidak.
     * ViewModel cukup memanggil metode ini tanpa perlu menghitung manual.
     *
     * @param other Objek lain yang ingin dicek (misal: apakah Alien menabrak Player?)
     * @return True jika kotak pembatas (hitbox) mereka saling bersinggungan.
     */
    public boolean checkCollision(GameElement other) {
        return this.getBounds().intersects(other.getBounds());
    }

    // --- Metode Akses Data (Getters & Setters) ---
    // Digunakan oleh View untuk mengambil data posisi saat proses penggambaran (rendering).

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public Image getImage() { return image; }
    public void setImage(Image image) { this.image = image; }

    /**
     * Membentuk kotak imajiner (Hitbox) di sekitar objek.
     * Kotak inilah yang sebenarnya digunakan untuk perhitungan tabrakan.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}