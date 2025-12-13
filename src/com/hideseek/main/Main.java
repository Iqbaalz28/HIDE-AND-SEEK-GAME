package com.hideseek.main;

import com.hideseek.view.GameWindow;
import javax.swing.SwingUtilities;

/**
 * Kelas ini berfungsi sebagai titik masuk utama (entry point) untuk aplikasi permainan
 * "Hide and Seek: The Challenge". Di sinilah eksekusi program dimulai.
 */
public class Main {

    /**
     * Metode utama yang akan dijalankan pertama kali saat aplikasi dibuka.
     * Tugas utamanya adalah mempersiapkan dan menampilkan antarmuka pengguna (GUI).
     *
     * @param args Argumen baris perintah yang mungkin diberikan saat peluncuran (tidak digunakan dalam konteks ini).
     */
    public static void main(String[] args) {
        /*
         * Mekanisme SwingUtilities.invokeLater digunakan di sini untuk menjadwalkan
         * pembuatan antarmuka grafis agar dieksekusi di dalam Event Dispatch Thread (EDT).
         *
         * Dalam arsitektur Java Swing, komponen GUI tidak bersifat thread-safe. Oleh karena itu,
         * praktik ini sangat krusial untuk memastikan bahwa seluruh manipulasi tampilan
         * dilakukan secara berurutan dan aman, mencegah potensi konflik data (race condition)
         * atau pembekuan aplikasi (freezing) saat inisialisasi awal.
         */
        SwingUtilities.invokeLater(() -> {
            // Menciptakan instansi baru dari jendela utama permainan.
            // Langkah ini akan memicu konstruktor GameWindow yang kemudian memuat
            // seluruh komponen visual, menu utama, dan logika dasar aplikasi.
            new GameWindow();
        });
    }
}