package com.hideseek.model;

import java.awt.Image;

/**
 * Kelas ini merepresentasikan entitas musuh (Alien) dalam permainan.
 * * Alien merupakan turunan dari GameElement, yang berarti ia secara otomatis
 * mewarisi seluruh atribut dasar seperti posisi (x, y), dimensi (lebar, tinggi),
 * dan tampilan visual (gambar). Dengan demikian, kelas ini fokus pada identitas
 * spesifik sebagai musuh tanpa perlu mendefinisikan ulang properti dasar tersebut.
 */
public class Alien extends GameElement {

    /**
     * Konstruktor untuk menciptakan objek Alien baru.
     * * Saat objek ini dibuat, ia langsung meneruskan seluruh parameter inisialisasi
     * ke konstruktor kelas induknya (GameElement) menggunakan kata kunci 'super'.
     * Ini memastikan bahwa Alien terdaftar di sistem koordinat permainan dengan
     * gambar yang sesuai.
     *
     * @param x Posisi horizontal awal.
     * @param y Posisi vertikal awal.
     * @param width Lebar objek alien.
     * @param height Tinggi objek alien.
     * @param image Gambar sprite yang memvisualisasikan alien.
     */
    public Alien(int x, int y, int width, int height, Image image) {
        super(x, y, width, height, image);
    }
}