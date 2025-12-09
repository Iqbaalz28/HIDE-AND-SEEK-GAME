package com.hideseek.model;

import java.awt.Image;

public class Bullet extends GameElement {
    private boolean isEnemyBullet; // True jika ini peluru alien, False jika peluru pemain

    public Bullet(int x, int y, int width, int height, Image image, boolean isEnemyBullet) {
        super(x, y, width, height, image);
        this.isEnemyBullet = isEnemyBullet;
    }

    public boolean isEnemyBullet() {
        return isEnemyBullet;
    }
}