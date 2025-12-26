package com.hideseek.viewmodel;

/**
 * Interface ini berfungsi sebagai "Kontrak Komunikasi" antara ViewModel dan View.
 *
 * Dalam pola MVVM (Model-View-ViewModel), ViewModel tidak boleh tahu secara langsung
 * siapa View-nya (decoupling). Oleh karena itu, ViewModel menggunakan interface ini
 * untuk mengirim sinyal atau notifikasi kejadian penting tanpa perlu memanggil
 * kelas GameCanvas secara langsung.
 */
public interface GameEventListener {
    /**
     * Sinyal Pembaruan Layar (Frame Update).
     *
     * Metode ini dipanggil oleh ViewModel setiap kali satu putaran logika game (tick) selesai.
     * Tujuannya adalah memberitahu View bahwa posisi pemain, musuh, atau peluru
     * telah berubah, sehingga View harus segera menggambar ulang (repaint) layar
     * agar animasi terlihat mulus.
     */

    void onGameUpdate(); // Dipanggil setiap frame (tick) untuk repaint layar

    /**
     * Sinyal Permainan Berakhir (Game Over).
     *
     * Metode ini dipanggil oleh ViewModel ketika kondisi kalah terpenuhi
     * (misalnya: Pemain bertabrakan dengan Alien).
     *
     * @param finalScore Skor terakhir yang didapat pemain saat kalah.
     * Data ini dikirim agar View bisa menampilkannya di pesan pop-up
     * dan menyimpannya ke database.
     */
    void onGameOver(int finalScore); // Dipanggil saat game berakhir
}