package com.hideseek.view;

import com.hideseek.model.Alien;
import com.hideseek.model.Bullet;
import com.hideseek.model.Obstacle;
import com.hideseek.model.Player;
import com.hideseek.model.UserStats;
import com.hideseek.model.ResourceManager;
import com.hideseek.viewmodel.GameEventListener;
import com.hideseek.viewmodel.GameViewModel;
import com.hideseek.viewmodel.MenuViewModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Kelas View yang bertanggung jawab untuk rendering visual dan menangkap input.
 */
public class GameCanvas extends JPanel implements GameEventListener, KeyListener, MouseListener {

    private GameViewModel viewModel;
    private GameWindow parentWindow;

    // Aset Gambar
    private Image backgroundImage;
    private List<Image> alienImages;
    private List<Image> meteorImages;
    private Image playerImage;

    // Data UI
    private String currentUsername;

    public GameCanvas(GameWindow parentWindow) {
        this.parentWindow = parentWindow;
        this.setFocusable(true);
        this.setBackground(Color.BLACK);

        // Mendaftarkan Listeners
        this.addKeyListener(this);
        this.addMouseListener(this); // Wajib ada untuk klik menembak

        loadAssets();
    }

    private void loadAssets() {
        // Menggunakan ResourceManager (Lebih Rapi & Strict OOP)
        this.backgroundImage = ResourceManager.loadBackgroundImage();
        this.playerImage = ResourceManager.loadPlayerImage();
        this.alienImages = ResourceManager.loadAlienImages();
        this.meteorImages = ResourceManager.loadMeteorImages();
    }

    /**
     * Memulai sesi permainan baru.
     */
    public void startNewGame(String username) {
        this.currentUsername = username;

        // 1. Ambil Stats Awal (Hanya untuk inisialisasi ViewModel)
        // MenuViewModel di sini hanya dipakai sebagai 'Data Fetcher', bukan untuk logika game.
        MenuViewModel menuVM = new MenuViewModel();
        UserStats userStats = menuVM.getUserStats(username);

        // 2. Buat ViewModel Baru
        // Kita kirim 'username' agar ViewModel bisa melakukan Auto-Save saat Game Over.
        this.viewModel = new GameViewModel(this, alienImages, meteorImages, playerImage, userStats, username);

        // 3. Start
        this.viewModel.startGame();
        this.requestFocusInWindow();
    }

    public void stopGame() {
        if (viewModel != null) {
            viewModel.stopGame();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (viewModel == null) return;

        // --- LAYER 1: Background ---
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // --- LAYER 2: Objek Game ---
        // Batu
        for (Obstacle obs : viewModel.getObstacles()) {
            if (obs.getImage() != null) g.drawImage(obs.getImage(), obs.getX(), obs.getY(), obs.getWidth(), obs.getHeight(), this);
            else { g.setColor(Color.GRAY); g.fillRect(obs.getX(), obs.getY(), obs.getWidth(), obs.getHeight()); }

            // Text HP Batu
            g.setColor(Color.WHITE); g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString(String.valueOf(obs.getHp()), obs.getX() + 20, obs.getY() + 35);
        }

        // Alien
        for (Alien a : viewModel.getAliens()) {
            if (a.getImage() != null) g.drawImage(a.getImage(), a.getX(), a.getY(), a.getWidth(), a.getHeight(), this);
            else { g.setColor(Color.RED); g.fillOval(a.getX(), a.getY(), a.getWidth(), a.getHeight()); }
        }

        // Player
        Player p = viewModel.getPlayer();
        if (p.getImage() != null) g.drawImage(p.getImage(), p.getX(), p.getY(), p.getWidth(), p.getHeight(), this);
        else { g.setColor(Color.YELLOW); g.fillOval(p.getX(), p.getY(), p.getWidth(), p.getHeight()); }

        // Peluru
        for (Bullet b : viewModel.getBullets()) {
            if (b.isEnemyBullet()) g.setColor(Color.ORANGE);
            else g.setColor(Color.CYAN);
            g.fillOval(b.getX(), b.getY(), 10, 10);
        }

        // --- LAYER 3: HUD (Interface) ---
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g.drawString("Player: " + (currentUsername != null ? currentUsername : "Guest"), 15, 25);
        g.drawString("Skor: " + p.getScore(), 15, 45);
        g.drawString("Peluru: " + p.getAmmo(), 15, 65);

        // Instruksi
        g.drawString("[WASD / Panah] Bergerak", 580, 25);
        g.drawString("[KLIK KIRI] Tembak", 580, 45);
        g.drawString("[ESC] Keluar", 580, 65);
    }

    // --- GAME EVENT LISTENER IMPL ---

    @Override
    public void onGameUpdate() {
        repaint(); // Render ulang layar setiap frame update
    }

    @Override
    public void onGameOver(int finalScore) {
        // BERSIH: Tidak ada logika database di sini.
        // ViewModel sudah melakukan penyimpanan data di belakang layar.
        // View hanya bertugas menampilkan notifikasi.

        JOptionPane.showMessageDialog(this,
                "Game Over!\nSkor Akhir: " + finalScore + "\nProgres Anda telah disimpan otomatis.");

        parentWindow.showMenu();
    }

    // --- INPUT HANDLERS ---

    @Override
    public void keyPressed(KeyEvent e) {
        if (viewModel == null) return;
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) viewModel.setMoveLeft(true);
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) viewModel.setMoveRight(true);
        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) viewModel.setMoveUp(true);
        if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) viewModel.setMoveDown(true);

        if (key == KeyEvent.VK_ESCAPE) {
            viewModel.stopGame();
            parentWindow.showMenu();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (viewModel == null) return;
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) viewModel.setMoveLeft(false);
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) viewModel.setMoveRight(false);
        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) viewModel.setMoveUp(false);
        if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) viewModel.setMoveDown(false);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (viewModel != null) {
            // Mengirim koordinat kursor ke ViewModel
            viewModel.playerShoot(e.getX(), e.getY());
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}