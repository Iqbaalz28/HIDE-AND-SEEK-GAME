package com.hideseek.model;

import java.awt.Image;
import java.awt.Rectangle;

/**
 * Kelas abstrak yang berfungsi sebagai cetak biru (blueprint) utama bagi seluruh objek visual
 * yang ada di dalam permainan, seperti Pemain, Alien, Batu, dan Peluru.
 *
 * Penerapan konsep Abstraksi di sini memastikan bahwa kita tidak perlu menulis ulang
 * kode untuk atribut umum (seperti posisi dan gambar) di setiap kelas turunan.
 * Kelas ini tidak dapat diinstansiasi secara langsung, melainkan harus diturunkan
 * ke kelas yang lebih spesifik.
 */
public abstract class GameElement {

    /*
     * Atribut-atribut dasar yang pasti dimiliki oleh setiap objek game.
     * Penggunaan akses modifier 'protected' memberikan izin kepada kelas anak
     * (subclass) untuk mengakses dan memanipulasi variabel ini secara langsung,
     * yang memudahkan logika pergerakan pada kelas turunan.
     */
    protected int x;        // Koordinat posisi horizontal
    protected int y;        // Koordinat posisi vertikal
    protected int width;    // Lebar objek (untuk hitungan tabrakan dan gambar)
    protected int height;   // Tinggi objek
    protected Image image;  // Aset gambar visual yang mewakili objek ini

    /**
     * Konstruktor dasar yang wajib dipanggil oleh setiap kelas anak melalui perintah 'super'.
     * Metode ini memastikan bahwa setiap objek game yang lahir ke dalam memori
     * sudah memiliki posisi awal, ukuran, dan tampilan visual yang valid.
     *
     * @param x Posisi awal sumbu X.
     * @param y Posisi awal sumbu Y.
     * @param width Lebar objek.
     * @param height Tinggi objek.
     * @param image Gambar yang akan dirender oleh View.
     */
    public GameElement(int x, int y, int width, int height, Image image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
    }

    // --- Metode Akses (Getter dan Setter) ---
    // Metode-metode di bawah ini menerapkan prinsip Enkapsulasi, memberikan cara
    // yang aman bagi komponen luar (seperti ViewModel atau View) untuk membaca
    // atau mengubah status posisi dan tampilan objek.

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public Image getImage() { return image; }
    public void setImage(Image image) { this.image = image; }

    /**
     * Metode utilitas krusial untuk mekanisme deteksi tabrakan (Collision Detection).
     *
     * Fungsi ini menerjemahkan posisi dan ukuran objek menjadi sebuah bentuk
     * persegi panjang (Rectangle) virtual. ViewModel akan menggunakan persegi panjang ini
     * untuk memeriksa apakah ia bersinggungan (intersect) dengan persegi panjang milik
     * objek lain (misalnya: apakah kotak Peluru bersentuhan dengan kotak Alien?).
     *
     * @return Objek Rectangle yang merepresentasikan area fisik (hitbox) dari elemen ini.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}