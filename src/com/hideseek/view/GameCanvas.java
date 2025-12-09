package com.hideseek.view;

import com.hideseek.model.Alien;
import com.hideseek.model.Bullet;
import com.hideseek.model.Player;
import com.hideseek.viewmodel.GameEventListener;
import com.hideseek.viewmodel.GameViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * View: Menangani tampilan grafis dan input user.
 */
public class GameCanvas extends JPanel implements GameEventListener, KeyListener {
    private GameViewModel viewModel;

    public GameCanvas() {
        // Inisialisasi ViewModel dan hubungkan dengan View ini (this)
        this.viewModel = new GameViewModel(this);

        // Setup Panel
        this.setFocusable(true); // Agar bisa baca input keyboard
        this.setBackground(Color.BLACK); // Latar belakang angkasa
        this.addKeyListener(this); // Daftarkan listener keyboard

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

        // 1. Gambar Batu (Abu)
        g.setColor(Color.GRAY);
        for (com.hideseek.model.Obstacle obs : viewModel.getObstacles()) {
            g.fillRect(obs.getX(), obs.getY(), obs.getWidth(), obs.getHeight());
        }
        
        // 2. Gambar Player (Kuning)
        Player p = viewModel.getPlayer();
        g.setColor(Color.YELLOW);
        // Jika ada gambar aset, gunakan g.drawImage(...). Untuk sekarang pakai kotak/lingkaran.
        g.fillOval(p.getX(), p.getY(), p.getWidth(), p.getHeight());

        // 3. Gambar Aliens (Merah)
        g.setColor(Color.RED);
        for (Alien a : viewModel.getAliens()) {
            g.fillOval(a.getX(), a.getY(), a.getWidth(), a.getHeight());
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