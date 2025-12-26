package com.hideseek.model;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Kelas Utilitas untuk memuat aset gambar.
 * Memisahkan logika loading file dari logika tampilan (View).
 */
public class ResourceManager {

    // Helper method untuk memuat satu gambar dengan aman
    public static Image loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.err.println("Gagal memuat gambar dari path: " + path);
            e.printStackTrace();
            return null;
        }
    }

    // Memuat daftar gambar Alien
    public static List<Image> loadAlienImages() {
        List<Image> images = new ArrayList<>();
        String[] files = {
                "shipBeige_manned.png",
                "shipBlue_manned.png",
                "shipGreen_manned.png",
                "shipPink_manned.png",
                "shipYellow_manned.png"
        };

        for (String f : files) {
            Image img = loadImage("assets/Alien/" + f);
            if (img != null) {
                images.add(img);
            }
        }
        return images;
    }

    // Memuat daftar gambar Meteor
    public static List<Image> loadMeteorImages() {
        List<Image> images = new ArrayList<>();
        String[] files = {
                "meteorBrown_big1.png", "meteorBrown_big2.png",
                "meteorBrown_big3.png", "meteorBrown_big4.png",
                "meteorGrey_big1.png", "meteorGrey_big2.png",
                "meteorGrey_big3.png", "meteorGrey_big4.png"
        };

        for (String f : files) {
            Image img = loadImage("assets/Meteors/" + f);
            if (img != null) {
                images.add(img);
            }
        }
        return images;
    }

    // Memuat gambar Player
    public static Image loadPlayerImage() {
        return loadImage("assets/Player/Player.png");
    }

    // Memuat Background
    public static Image loadBackgroundImage() {
        return loadImage("assets/Backgrounds/blue.png");
    }
}