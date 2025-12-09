package com.hideseek.model;

import java.awt.Image;

public class Player extends GameElement {
    private int ammo; // Jumlah peluru pemain
    private int score; // Skor pemain saat ini
    private int ammoMissed; // Jumlah peluru alien yang berhasil dihindari (meleset)

    public Player(int x, int y, int width, int height, Image image) {
        super(x, y, width, height, image);
        this.ammo = 0; // Awalnya tidak punya peluru
        this.score = 0;
        this.ammoMissed = 0;
    }

    // --- Logika Khusus Player ---
    public void addAmmo(int amount) {
        this.ammo += amount;
    }

    public void decreaseAmmo() {
        if (this.ammo > 0) {
            this.ammo--;
        }
    }

    public void addScore(int points) {
        this.score += points;
    }

    public void addAmmoMissed() {
        this.ammoMissed++;
    }

    // Getter Setter Khusus
    public int getAmmo() { return ammo; }
    public int getScore() { return score; }
    public int getAmmoMissed() { return ammoMissed; }
}