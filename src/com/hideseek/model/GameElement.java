package com.hideseek.model;

import java.awt.Image;
import java.awt.Rectangle;

/**
 * Abstract Class yang menjadi induk semua objek visual di game.
 */
public abstract class GameElement {
    // Atribut dasar (Protected agar bisa diakses oleh kelas anak/turunan)
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected Image image;

    public GameElement(int x, int y, int width, int height, Image image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
    }

    // --- Getter dan Setter ---
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public Image getImage() { return image; }
    public void setImage(Image image) { this.image = image; }

    // Metode bantuan untuk deteksi tabrakan (Collision Detection)
    // Mengembalikan kotak pembatas objek ini
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}