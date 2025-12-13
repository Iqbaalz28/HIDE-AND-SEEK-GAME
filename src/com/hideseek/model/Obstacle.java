package com.hideseek.model;

import java.awt.Image;

/**
 * Kelas ini merepresentasikan objek penghalang (Batu atau Meteor) dalam area permainan.
 * * Obstacle merupakan turunan dari GameElement, sehingga ia memiliki posisi fisik dan gambar.
 * Namun, pembeda utamanya adalah adanya atribut 'daya tahan' (HP). Objek ini tidak
 * bergerak, tetapi berfungsi sebagai pelindung bagi pemain sekaligus rintangan yang
 * bisa dihancurkan secara perlahan.
 */
public class Obstacle extends GameElement {

    // Variabel untuk menyimpan "Health Points" atau sisa kekuatan batu.
    // Dibuat private untuk menjaga integritas data, hanya bisa diubah melalui metode hit().
    private int hp;

    /**
     * Konstruktor untuk menciptakan objek batu baru.
     * * Selain mengatur posisi dan gambar melalui konstruktor induk (super),
     * di sini kita menetapkan nilai awal ketahanan batu. Nilai 25 berarti
     * batu ini harus terkena 25 kali tembakan (baik dari pemain maupun alien)
     * sebelum akhirnya hancur dan menghilang dari layar.
     *
     * @param x Posisi horizontal.
     * @param y Posisi vertikal.
     * @param width Lebar batu.
     * @param height Tinggi batu.
     * @param image Gambar visual meteor/batu.
     */
    public Obstacle(int x, int y, int width, int height, Image image) {
        super(x, y, width, height, image);
        this.hp = 25; // Menetapkan standar ketahanan awal batu.
    }

    /**
     * Metode logika yang dipanggil ketika batu terdeteksi bertabrakan dengan peluru.
     * * Fungsi ini mensimulasikan kerusakan dengan cara mengurangi nilai HP batu
     * sebanyak satu poin setiap kali metode ini dipanggil.
     */
    public void hit() {
        this.hp--;
    }

    /**
     * Metode akses untuk mendapatkan sisa nyawa batu saat ini.
     * * Data ini sangat berguna bagi View (GameCanvas) untuk menampilkan angka
     * sisa nyawa di tengah gambar batu, memberikan informasi visual kepada pemain
     * tentang seberapa kuat batu yang tersisa.
     */
    public int getHp() {
        return hp;
    }

    /**
     * Metode pemeriksaan status kehancuran.
     * * ViewModel menggunakan metode ini untuk memutuskan apakah objek batu ini
     * harus dihapus dari daftar objek permainan (List obstacles). Jika HP sudah
     * mencapai nol atau kurang, batu dianggap hancur total.
     *
     * @return true jika batu sudah hancur, false jika masih bertahan.
     */
    public boolean isDestroyed() {
        return hp <= 0;
    }
}