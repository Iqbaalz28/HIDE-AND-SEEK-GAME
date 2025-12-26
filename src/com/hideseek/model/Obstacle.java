package com.hideseek.model;

import java.awt.Image;

/**
 * Representasi rintangan lingkungan (Batu Meteor).
 * * Berbeda dengan Alien yang bergerak, objek ini diam.
 * Ia memiliki "Health Point" (HP) yang membuatnya bisa hancur perlahan
 * jika terus-menerus ditembaki.
 */
public class Obstacle extends GameElement {

    // Ketahanan batu. Private agar tidak bisa diubah sembarangan dari luar.
    private int hp;

    public Obstacle(int x, int y, int width, int height, Image image) {
        super(x, y, width, height, image);
        this.hp = 25; // Batu hancur setelah kena 25 hit
    }

    /**
     * Dipanggil saat peluru mengenai batu ini.
     * Mengurangi satu poin ketahanan.
     */
    public void hit() {
        this.hp--;
    }

    public int getHp() {
        return hp;
    }

    /**
     * Status kehancuran.
     * ViewModel akan menghapus objek ini dari permainan jika metode ini mengembalikan true.
     */
    public boolean isDestroyed() {
        return hp <= 0;
    }
}