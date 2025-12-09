package com.hideseek.view;


import com.hideseek.model.Alien;
import com.hideseek.model.Bullet;
import com.hideseek.model.Obstacle;
import com.hideseek.model.Player;
import com.hideseek.viewmodel.GameEventListener;
import com.hideseek.viewmodel.GameViewModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * View: Menangani tampilan grafis dan input user.
 */
public class GameCanvas extends JPanel implements GameEventListener, KeyListener {
    private GameViewModel viewModel;
    private Image backgroundImage;
    private List<Image> alienImages;
    private List<Image> meteorImages;

    public GameCanvas() {

        // Setup Panel
        this.setFocusable(true); // Agar bisa baca input keyboard
        this.addKeyListener(this); // Daftarkan listener keyboard

        // Load Gambar Background
        try {
            // Membaca file dari folder assets di root project
            backgroundImage = ImageIO.read(new File("assets/Backgrounds/blue.png"));
        } catch (IOException e) {
            System.out.println("Gagal memuat gambar background: " + e.getMessage());
            e.printStackTrace();
            // Fallback jika gambar gagal dimuat, pakai warna hitam
            this.setBackground(Color.BLACK);
        }

        // 2. Load Alien Images
        alienImages = new ArrayList<>();
        String[] alienFiles = {
                "shipBeige_manned.png",
                "shipBlue_manned.png",
                "shipGreen_manned.png",
                "shipPink_manned.png",
                "shipYellow_manned.png"
        };

        for (String fileName : alienFiles) {
            try {
                // Load dari folder assets/Alien/
                Image img = ImageIO.read(new File("assets/Alien/" + fileName));
                alienImages.add(img);
            } catch (IOException e) {
                System.out.println("Gagal memuat gambar alien " + fileName + ": " + e.getMessage());
            }
        }

        // 3. Load Meteor Images
        meteorImages = new ArrayList<>();
        String[] meteorFiles = {
                "meteorBrown_big1.png", "meteorBrown_big2.png", "meteorBrown_big3.png", "meteorBrown_big4.png",
                "meteorGrey_big1.png", "meteorGrey_big2.png", "meteorGrey_big3.png", "meteorGrey_big4.png"
        };

        for (String fileName : meteorFiles) {
            try {
                // Path ke folder assets/Meteors/
                meteorImages.add(ImageIO.read(new File("assets/Meteors/" + fileName)));
            } catch (IOException e) { System.out.println("Gagal load meteor: " + fileName); }
        }

        // 4. Inisialisasi ViewModel dengan membawa list gambar
        this.viewModel = new GameViewModel(this, alienImages, meteorImages);

        // Mulai Game Loop di ViewModel
        this.viewModel.startGame();
    }

    /**
     * Metode inti untuk menggambar.
     * Dipanggil otomatis saat repaint().
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Gambar Background
        if (backgroundImage != null) {
            // Menggambar image memenuhi ukuran panel (stretch)
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        } else {
            // Jika gambar gagal dimuat, pakai layar hitam
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }

        // 1. Gambar Batu (Abu)
        for (Obstacle obs : viewModel.getObstacles()) {
            if (obs.getImage() != null) {
                // Gambar Meteor sesuai image aslinya
                g.drawImage(obs.getImage(), obs.getX(), obs.getY(), obs.getWidth(), obs.getHeight(), this);
            } else {
                // Fallback jika gambar null (Kotak abu)
                g.setColor(Color.GRAY);
                g.fillRect(obs.getX(), obs.getY(), obs.getWidth(), obs.getHeight());
            }

            // Tampilkan HP di tengah meteor agar pemain tahu kapan hancur
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            // Logika centering text
            String hpText = String.valueOf(obs.getHp());
            int textX = obs.getX() + (obs.getWidth() / 2) - 5;
            int textY = obs.getY() + (obs.getHeight() / 2) + 5;
            g.drawString(hpText, textX, textY);
        }

        // 2. Gambar Player (Kuning)
        Player p = viewModel.getPlayer();
        g.setColor(Color.YELLOW);
        // Jika ada gambar aset, gunakan g.drawImage(...). Untuk sekarang pakai kotak/lingkaran.
        g.fillOval(p.getX(), p.getY(), p.getWidth(), p.getHeight());

        // 3. Gambar Alien (BARU: Menggunakan Image)
        for (Alien a : viewModel.getAliens()) {
            if (a.getImage() != null) {
                // Gambar alien sesuai image yang sudah di-assign di ViewModel
                g.drawImage(a.getImage(), a.getX(), a.getY(), a.getWidth(), a.getHeight(), this);
            } else {
                // Fallback jika gambar gagal load
                g.setColor(Color.RED);
                g.fillOval(a.getX(), a.getY(), a.getWidth(), a.getHeight());
            }
        }

        // 4. Gambar Bullets
        for (Bullet b : viewModel.getBullets()) {
            if (b.isEnemyBullet()) {
                g.setColor(Color.ORANGE); // Peluru musuh
            } else {
                g.setColor(Color.CYAN); // Peluru pemain
            }
            g.fillRect(b.getX(), b.getY(), b.getWidth(), b.getHeight());
        }

        // 5. Gambar HUD (Skor & Status)
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Skor: " + p.getScore(), 10, 20);
        g.drawString("Sisa Peluru: " + p.getAmmo(), 10, 40);
        g.drawString("Peluru Meleset (Dihindari): " + p.getAmmoMissed(), 10, 60);
    }

    // --- Implementasi GameEventListener (Dari ViewModel) ---
    @Override
    public void onGameUpdate() {
        // ViewModel meminta View untuk menggambar ulang frame ini
        repaint();
    }

    @Override
    public void onGameOver(int finalScore) {
        // Tampilkan pesan Game Over
        JOptionPane.showMessageDialog(this, "Game Over! Skor Akhir: " + finalScore);
        System.exit(0); // Keluar atau kembali ke menu
    }

    // --- Implementasi KeyListener (Input Keyboard) ---
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) viewModel.setMoveLeft(true);
        if (key == KeyEvent.VK_RIGHT) viewModel.setMoveRight(true);
        if (key == KeyEvent.VK_UP) viewModel.setMoveUp(true);
        if (key == KeyEvent.VK_DOWN) viewModel.setMoveDown(true);
        if (key == KeyEvent.VK_SPACE) viewModel.playerShoot(); // Spasi untuk menembak
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) viewModel.setMoveLeft(false);
        if (key == KeyEvent.VK_RIGHT) viewModel.setMoveRight(false);
        if (key == KeyEvent.VK_UP) viewModel.setMoveUp(false);
        if (key == KeyEvent.VK_DOWN) viewModel.setMoveDown(false);
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}