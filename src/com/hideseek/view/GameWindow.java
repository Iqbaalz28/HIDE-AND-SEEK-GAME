package com.hideseek.view;

import javax.swing.JFrame;

public class GameWindow extends JFrame {

    public GameWindow() {
        this.setTitle("Hide and Seek: The Challenge");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setResizable(false);

        // Masukkan Kanvas Game ke dalam Window
        GameCanvas canvas = new GameCanvas();
        this.add(canvas);

        this.setLocationRelativeTo(null); // Tengah layar
        this.setVisible(true);
    }
}