package com.hideseek.main;

import com.hideseek.view.GameWindow;
import javax.swing.SwingUtilities;

/**
 * Kelas ini berfungsi sebagai gerbang utama (Entry Point) untuk menjalankan aplikasi.
 * * Dalam struktur proyek ini, Main sengaja dibuat seminimal mungkin. Ia tidak menangani
 * logika permainan maupun data, melainkan hanya bertugas untuk menyalakan mesin
 * antarmuka grafis (View) agar aplikasi mulai tampil di layar pengguna.
 */
public class Main {

    /**
     * Fungsi utama yang akan dipanggil pertama kali oleh Java Virtual Machine (JVM)
     * saat program dijalankan. Di sinilah siklus hidup aplikasi dimulai.
     *
     * @param args Parameter bawaan Java untuk menerima input baris perintah (tidak digunakan di sini).
     */
    public static void main(String[] args) {
        /*
         * Membungkus proses inisialisasi UI di dalam blok SwingUtilities.invokeLater.
         *
         * Penjelasan teknisnya: Java Swing memiliki aturan ketat bahwa segala sesuatu yang
         * berhubungan dengan tampilan (UI) harus dijalankan di satu jalur antrian khusus
         * yang disebut Event Dispatch Thread (EDT).
         *
         * Jika membuat jendela langsung tanpa pembungkus ini, ada risiko terjadinya
         * konflik memori (race condition) yang bisa menyebabkan aplikasi macet atau
         * error visual saat baru dibuka.
         */
        SwingUtilities.invokeLater(() -> {
            // Menciptakan objek jendela utama (GameWindow).
            // Ini adalah langkah pertama untuk memuat lapisan View. Setelah baris ini dieksekusi,
            // kendali tampilan akan sepenuhnya dipegang oleh GameWindow dan CardLayout-nya.
            new GameWindow();
        });
    }
}