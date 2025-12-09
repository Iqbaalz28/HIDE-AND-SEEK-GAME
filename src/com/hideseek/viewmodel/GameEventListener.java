package com.hideseek.viewmodel;

/**
 * Interface untuk komunikasi dari ViewModel ke View.
 * View akan mengimplementasikan ini untuk merespons perubahan data game.
 */
public interface GameEventListener {
    void onGameUpdate(); // Dipanggil setiap frame (tick) untuk repaint layar
    void onGameOver(int finalScore); // Dipanggil saat game berakhir
}