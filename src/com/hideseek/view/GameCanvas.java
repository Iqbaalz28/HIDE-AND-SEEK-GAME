package com.hideseek.view;

import com.hideseek.model.Alien;
import com.hideseek.model.Bullet;
import com.hideseek.model.Obstacle;
import com.hideseek.model.Player;
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
 * Kelas View yang bertanggung jawab untuk seluruh aspek visual permainan.
 * * Update: Sekarang mengimplementasikan MouseListener untuk mendukung fitur
 * membidik dan menembak menggunakan mouse (Mouse Aiming).
 */
public class GameCanvas extends JPanel implements GameEventListener, KeyListener, MouseListener {

    private GameViewModel viewModel;
    private Image backgroundImage;
    private List<Image> alienImages;
    private List<Image> meteorImages;
    private Image playerImage;
    private String currentUsername;
    private GameWindow parentWindow;

    /**
     * Konstruktor Canvas.
     * Mendaftarkan listener mouse dan keyboard agar permainan responsif terhadap
     * segala jenis input pengguna.
     */
    public GameCanvas(GameWindow parentWindow) {
        this.parentWindow = parentWindow;

        // Mengaktifkan fokus input.
        this.setFocusable(true);
        this.addKeyListener(this);

        // MENAMBAHKAN LISTENER MOUSE (Fitur Baru)
        // Ini memungkinkan panel mendeteksi klik mouse untuk menembak.
        this.addMouseListener(this);

        // --- Proses Pemuatan Aset Gambar ---
        try {
            backgroundImage = ImageIO.read(new File("assets/Backgrounds/blue.png"));
        } catch (IOException e) {
            setBackground(Color.BLACK);
        }

        alienImages = new ArrayList<>();
        String[] alienFiles = {
                "shipBeige_manned.png", "shipBlue_manned.png",
                "shipGreen_manned.png", "shipPink_manned.png",
                "shipYellow_manned.png"
        };
        for (String f : alienFiles) {
            try { alienImages.add(ImageIO.read(new File("assets/Alien/" + f))); } catch (Exception e) {}
        }

        meteorImages = new ArrayList<>();
        String[] meteorFiles = {
                "meteorBrown_big1.png", "meteorBrown_big2.png",
                "meteorBrown_big3.png", "meteorBrown_big4.png",
                "meteorGrey_big1.png", "meteorGrey_big2.png",
                "meteorGrey_big3.png", "meteorGrey_big4.png"
        };
        for (String f : meteorFiles) {
            try { meteorImages.add(ImageIO.read(new File("assets/Meteors/" + f))); } catch (Exception e) {}
        }

        try {
            playerImage = ImageIO.read(new File("assets/Player/Player.png"));
        } catch (Exception e) {}
    }

    public void startNewGame(String username) {
        this.currentUsername = username;
        MenuViewModel menuVM = new MenuViewModel();
        com.hideseek.model.UserStats userStats = menuVM.getUserStats(username);
        this.viewModel = new GameViewModel(this, alienImages, meteorImages, playerImage, userStats);
        this.viewModel.startGame();
        this.requestFocusInWindow();
    }

    public void stopGame() {
        if(viewModel != null) {
            viewModel.stopGame();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (viewModel == null) return;

        // 1. Gambar Background
        if (backgroundImage != null) g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        else { g.setColor(Color.BLACK); g.fillRect(0, 0, getWidth(), getHeight()); }

        // 2. Gambar Batu
        for (Obstacle obs : viewModel.getObstacles()) {
            if (obs.getImage() != null) g.drawImage(obs.getImage(), obs.getX(), obs.getY(), obs.getWidth(), obs.getHeight(), this);
            else { g.setColor(Color.GRAY); g.fillRect(obs.getX(), obs.getY(), obs.getWidth(), obs.getHeight()); }

            g.setColor(Color.WHITE); g.setFont(new Font("Arial", Font.BOLD, 12));
            int textX = obs.getX() + (obs.getWidth()/2) - 5;
            int textY = obs.getY() + (obs.getHeight()/2) + 5;
            g.drawString(String.valueOf(obs.getHp()), textX, textY);
        }

        // 3. Gambar Player
        Player p = viewModel.getPlayer();
        if (p.getImage() != null) g.drawImage(p.getImage(), p.getX(), p.getY(), p.getWidth(), p.getHeight(), this);
        else { g.setColor(Color.YELLOW); g.fillOval(p.getX(), p.getY(), p.getWidth(), p.getHeight()); }

        // 4. Gambar Alien
        for (Alien a : viewModel.getAliens()) {
            if (a.getImage() != null) g.drawImage(a.getImage(), a.getX(), a.getY(), a.getWidth(), a.getHeight(), this);
            else { g.setColor(Color.RED); g.fillOval(a.getX(), a.getY(), a.getWidth(), a.getHeight()); }
        }

        // 5. Gambar Peluru
        for (Bullet b : viewModel.getBullets()) {
            if (b.isEnemyBullet()) g.setColor(Color.ORANGE); else g.setColor(Color.CYAN);

            // Menggambar peluru sebagai oval kecil agar terlihat lebih dinamis saat bergerak miring
            g.fillOval(b.getX(), b.getY(), 10, 10);
        }

        // 6. HUD
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Player: " + (currentUsername != null ? currentUsername : "Guest"), 10, 20);
        g.drawString("Skor: " + p.getScore(), 10, 40);
        g.drawString("Sisa Peluru: " + p.getAmmo(), 10, 60);
        g.drawString("Peluru Meleset: " + p.getAmmoMissed(), 10, 80);
        g.drawString("[ESC] Menyerah/Keluar", 600, 20);
        g.drawString("[KLIK KIRI] Tembak", 600, 40); // Instruksi baru
    }

    @Override
    public void onGameUpdate() { repaint(); }

    @Override
    public void onGameOver(int finalScore) {
        MenuViewModel menuVM = new MenuViewModel();
        Player p = viewModel.getPlayer();
        menuVM.updateScore(currentUsername, p.getScore(), p.getAmmoMissed(), p.getAmmo());
        JOptionPane.showMessageDialog(this, "Game Over!\nSkor Akhir: " + finalScore + "\nData telah disimpan.");
        parentWindow.showMenu();
    }

    // --- Input Keyboard (Movement) ---
    @Override
    public void keyPressed(KeyEvent e) {
        if (viewModel == null) return;
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) viewModel.setMoveLeft(true);
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) viewModel.setMoveRight(true);
        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) viewModel.setMoveUp(true);
        if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) viewModel.setMoveDown(true);

        // Tombol Spasi sekarang opsional, tapi bisa diarahkan ke tengah atas sebagai default
        if (key == KeyEvent.VK_SPACE) viewModel.playerShoot(viewModel.getPlayer().getX() + 25, 0);

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

    @Override public void keyTyped(KeyEvent e) {}

    // --- Input Mouse (Shooting) ---
    /**
     * Menangani aksi klik mouse.
     * Saat pemain mengklik area layar, koordinat klik (X, Y) dikirim ke ViewModel
     * untuk memicu tembakan ke arah tersebut.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (viewModel != null) {
            // Mengirim koordinat kursor mouse ke logika penembakan
            viewModel.playerShoot(e.getX(), e.getY());
        }
    }

    // Metode MouseListener lain yang tidak digunakan tetap harus ada (kosong)
    // untuk memenuhi kontrak Interface.
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}