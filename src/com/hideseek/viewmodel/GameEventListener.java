package com.hideseek.viewmodel;

/**
 * Interface Pendengar Acara Permainan (Game Event Listener).
 *
 * Mengapa kita butuh ini?
 * Dalam pola MVVM Strict, ViewModel tidak boleh "memegang" atau bergantung langsung
 * pada kelas View (GameCanvas). Jika ViewModel tahu tentang GameCanvas, itu disebut "Tight Coupling".
 *
 * Solusinya: ViewModel hanya berbicara melalui Interface ini.
 * View (GameCanvas) nanti akan "menandatangani kontrak" (implements) interface ini.
 * Jadi, ViewModel cukup berteriak "onGameUpdate!", dan siapa pun yang mendengarkan (View)
 * akan merespons.
 */
public interface GameEventListener {

    /**
     * Sinyal Detak Jantung Permainan (Tick).
     * Dipanggil oleh ViewModel setiap kali posisi objek berubah (sekitar 60 kali sedetik).
     * Memberitahu View bahwa "Data sudah berubah, tolong gambar ulang layar sekarang."
     */
    void onGameUpdate();

    /**
     * Sinyal Permainan Berakhir.
     * Dipanggil saat kondisi kalah terpenuhi (Pemain tertabrak Alien).
     *
     * @param finalScore Skor terakhir yang didapat untuk ditampilkan di pesan Game Over.
     */
    void onGameOver(int finalScore, int ammoMissed);
}