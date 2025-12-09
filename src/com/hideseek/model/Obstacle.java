package com.hideseek.model;

import java.awt.Image;

public class Obstacle extends GameElement {
    private int hp; // Nyawa batu

    public Obstacle(int x, int y, int width, int height, Image image) {
        super(x, y, width, height, image);
        this.hp = 25; // Batu hancur setelah 25 kali kena peluru
    }

    public void hit() {
        this.hp--;
    }

    public int getHp() {
        return hp;
    }

    public boolean isDestroyed() {
        return hp <= 0;
    }
}