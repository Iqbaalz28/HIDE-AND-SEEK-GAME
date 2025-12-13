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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Kelas View yang bertanggung jawab untuk seluruh aspek visual permainan.
 * * GameCanvas mewarisi JPanel agar dapat digambar ulang (repaint) secara terus-menerus.
 * * Ia mengimplementasikan KeyListener untuk menangkap input keyboard (gerak & tembak).
 * * Ia juga mengimplementasikan GameEventListener untuk merespons sinyal dari ViewModel
 * (seperti saat posisi alien berubah atau game over).
 */
public class GameCanvas extends JPanel implements GameEventListener, KeyListener {

    // ViewModel yang berisi seluruh logika dan data permainan saat ini.
    // View hanya bertugas menampilkan data yang ada di sini, tidak mengubahnya secara langsung.
    private GameViewModel viewModel;

    // Aset-aset gambar (Resource) yang dimuat ke dalam memori.
    private Image backgroundImage;
    private List<Image> alienImages;
    private List<Image> meteorImages;
    private Image playerImage;

    // Variabel pendukung integrasi dengan Main Menu.
    private String currentUsername;  // Menyimpan siapa yang sedang bermain.
    private GameWindow parentWindow; // Referensi ke jendela utama untuk navigasi halaman.

    /**
     * Konstruktor Canvas.
     * Tugas utamanya hanya memuat aset gambar agar siap digunakan.
     *
     * Catatan Penting: Konstruktor ini TIDAK memulai permainan. Hal ini disengaja
     * agar permainan tidak berjalan di latar belakang saat pengguna masih di Menu Utama.
     * Permainan baru benar-benar dimulai saat metode startNewGame() dipanggil.
     *
     * @param parentWindow Jendela utama yang menampung canvas ini.
     */
    public GameCanvas(GameWindow parentWindow) {
        this.parentWindow = parentWindow;

        // Mengaktifkan fitur fokus agar panel ini bisa mendeteksi tekanan tombol keyboard.
        this.setFocusable(true);
        this.addKeyListener(this);

        // --- Proses Pemuatan Aset Gambar (Resource Loading) ---
        // Gambar dimuat sekali saja di awal untuk menjaga performa rendering.
        try {
            backgroundImage = ImageIO.read(new File("assets/Backgrounds/blue.png"));
        } catch (IOException e) {
            // Fallback: Jika gambar gagal dimuat, gunakan warna latar hitam.
            setBackground(Color.BLACK);
        }

        // Memuat variasi gambar Alien
        alienImages = new ArrayList<>();
        String[] alienFiles = {
                "shipBeige_manned.png", "shipBlue_manned.png",
                "shipGreen_manned.png", "shipPink_manned.png",
                "shipYellow_manned.png"
        };
        for (String f : alienFiles) {
            try { alienImages.add(ImageIO.read(new File("assets/Alien/" + f))); } catch (Exception e) {}
        }

        // Memuat variasi gambar Meteor/Batu
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

        // Memuat gambar Pemain
        try {
            playerImage = ImageIO.read(new File("assets/Player/Player.png"));
        } catch (Exception e) {}
    }

    /**
     * Metode Inisialisasi Permainan (Game Start).
     * Dipanggil secara eksplisit oleh GameWindow ketika tombol "PLAY" ditekan.
     *
     * Alur prosesnya:
     * 1. Mencatat username pemain.
     * 2. Mengambil data statistik terakhir (Load Game) dari database.
     * 3. Membuat ViewModel baru dengan data tersebut.
     * 4. Memulai loop permainan (Thread).
     * 5. Memaksa fokus input ke layar permainan agar keyboard berfungsi.
     */
    public void startNewGame(String username) {
        this.currentUsername = username;

        // Langkah 1 & 2: Ambil data lama dari DB
        MenuViewModel menuVM = new MenuViewModel();
        com.hideseek.model.UserStats userStats = menuVM.getUserStats(username);

        // Langkah 3: Suntikkan data lama ke ViewModel baru
        this.viewModel = new GameViewModel(this, alienImages, meteorImages, playerImage, userStats);

        // Langkah 4: Jalankan game loop
        this.viewModel.startGame();

        // Langkah 5: Rebut fokus keyboard dari tombol menu
        this.requestFocusInWindow();
    }

    /**
     * Metode Penghentian Permainan.
     * Dipanggil saat pengguna menekan tombol ESC atau kembali ke menu.
     * Memastikan thread permainan berhenti total agar tidak memakan memori.
     */
    public void stopGame() {
        if(viewModel != null) {
            viewModel.stopGame();
        }
    }

    /**
     * Metode Rendering Utama (Drawing Loop).
     * Metode ini dipanggil berulang-ulang (sekitar 60 kali per detik) oleh sistem.
     * Tugasnya adalah menggambar ulang seluruh objek game berdasarkan posisi terkini
     * yang ada di ViewModel.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Mencegah error null pointer jika metode ini dipanggil sebelum game dimulai.
        if (viewModel == null) return;

        // 1. Gambar Latar Belakang
        if (backgroundImage != null) g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        else { g.setColor(Color.BLACK); g.fillRect(0, 0, getWidth(), getHeight()); }

        // 2. Gambar Rintangan (Batu)
        for (Obstacle obs : viewModel.getObstacles()) {
            if (obs.getImage() != null) g.drawImage(obs.getImage(), obs.getX(), obs.getY(), obs.getWidth(), obs.getHeight(), this);
            else { g.setColor(Color.GRAY); g.fillRect(obs.getX(), obs.getY(), obs.getWidth(), obs.getHeight()); }

            // Menampilkan indikator HP di atas batu
            g.setColor(Color.WHITE); g.setFont(new Font("Arial", Font.BOLD, 12));
            int textX = obs.getX() + (obs.getWidth()/2) - 5;
            int textY = obs.getY() + (obs.getHeight()/2) + 5;
            g.drawString(String.valueOf(obs.getHp()), textX, textY);
        }

        // 3. Gambar Pemain
        Player p = viewModel.getPlayer();
        if (p.getImage() != null) g.drawImage(p.getImage(), p.getX(), p.getY(), p.getWidth(), p.getHeight(), this);
        else { g.setColor(Color.YELLOW); g.fillOval(p.getX(), p.getY(), p.getWidth(), p.getHeight()); }

        // 4. Gambar Musuh (Alien)
        for (Alien a : viewModel.getAliens()) {
            if (a.getImage() != null) g.drawImage(a.getImage(), a.getX(), a.getY(), a.getWidth(), a.getHeight(), this);
            else { g.setColor(Color.RED); g.fillOval(a.getX(), a.getY(), a.getWidth(), a.getHeight()); }
        }

        // 5. Gambar Peluru
        for (Bullet b : viewModel.getBullets()) {
            if (b.isEnemyBullet()) g.setColor(Color.ORANGE); else g.setColor(Color.CYAN);
            g.fillRect(b.getX(), b.getY(), b.getWidth(), b.getHeight());
        }

        // 6. Gambar HUD (Heads-Up Display) / Informasi Status
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Player: " + (currentUsername != null ? currentUsername : "Guest"), 10, 20);
        g.drawString("Skor: " + p.getScore(), 10, 40);
        g.drawString("Sisa Peluru: " + p.getAmmo(), 10, 60);
        g.drawString("Peluru Meleset: " + p.getAmmoMissed(), 10, 80);
        g.drawString("[ESC] Menyerah/Keluar", 600, 20);
    }

    /**
     * Implementasi dari GameEventListener.
     * Saat logika game di ViewModel selesai menghitung satu frame, ia memanggil ini.
     * Perintah repaint() memberitahu Java Swing untuk segera menjalankan paintComponent().
     */
    @Override
    public void onGameUpdate() { repaint(); }

    /**
     * Implementasi dari GameEventListener.
     * Dipanggil saat kondisi kalah terpenuhi (misal: tertabrak alien).
     *
     * Logika:
     * 1. Simpan data terakhir ke database.
     * 2. Tampilkan pesan Game Over.
     * 3. Kembalikan pengguna ke Menu Utama.
     */
    @Override
    public void onGameOver(int finalScore) {
        MenuViewModel menuVM = new MenuViewModel();
        Player p = viewModel.getPlayer();

        // Simpan progres ke database (Update/Save)
        menuVM.updateScore(currentUsername, p.getScore(), p.getAmmoMissed(), p.getAmmo());

        JOptionPane.showMessageDialog(this, "Game Over!\nSkor Akhir: " + finalScore + "\nData telah disimpan.");

        // Navigasi balik ke menu
        parentWindow.showMenu();
    }

    // --- Penanganan Input Keyboard (Input Handling) ---
    @Override
    public void keyPressed(KeyEvent e) {
        if (viewModel == null) return;
        int key = e.getKeyCode();

        // Mengubah status gerak di ViewModel menjadi true saat tombol ditekan
        if (key == KeyEvent.VK_LEFT) viewModel.setMoveLeft(true);
        if (key == KeyEvent.VK_RIGHT) viewModel.setMoveRight(true);
        if (key == KeyEvent.VK_UP) viewModel.setMoveUp(true);
        if (key == KeyEvent.VK_DOWN) viewModel.setMoveDown(true);
        if (key == KeyEvent.VK_SPACE) viewModel.playerShoot();

        // Tombol Darurat / Keluar
        if (key == KeyEvent.VK_ESCAPE) {
            viewModel.stopGame();
            parentWindow.showMenu();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (viewModel == null) return;
        int key = e.getKeyCode();

        // Mengubah status gerak kembali menjadi false saat tombol dilepas
        if (key == KeyEvent.VK_LEFT) viewModel.setMoveLeft(false);
        if (key == KeyEvent.VK_RIGHT) viewModel.setMoveRight(false);
        if (key == KeyEvent.VK_UP) viewModel.setMoveUp(false);
        if (key == KeyEvent.VK_DOWN) viewModel.setMoveDown(false);
    }

    @Override public void keyTyped(KeyEvent e) {}
}