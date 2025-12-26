package com.hideseek.viewmodel;

import com.hideseek.model.*;

import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Mesin Utama Permainan (Game Engine / Conductor).
 * 1. Menerima Input dari View (Tombol ditekan, Mouse diklik).
 * 2. Memerintahkan Model untuk bertindak (misal: "Player, bergeraklah!", "Alien, tembaklah!").
 * 3. Mengecek kondisi global (Game Over, Spawn Musuh).
 * 4. Menyimpan data ke Database saat permainan usai.
 */
public class GameViewModel implements Runnable {

    // --- Referensi ke Model (Aktor Game) ---
    private Player player;
    private List<Alien> aliens;
    private List<Bullet> bullets;
    private List<Obstacle> obstacles;

    // --- Infrastruktur Pendukung ---
    private UserRepository userRepo; // Akses DB untuk save game
    private UserStats initialStats;  // Data awal (jika load game)
    private String currentUsername;  // Pemilik sesi permainan ini

    // --- Aset & Tools ---
    private List<Image> alienImages;
    private List<Image> meteorImages;
    private Image playerImage;
    private Sound soundEffect;       // Pemutar suara
    private Random random;

    // --- Status Game Loop ---
    private int screenWidth = 800;
    private int screenHeight = 600;
    private boolean isRunning = false;
    private Thread gameThread;

    // Komunikasi ke View
    private GameEventListener eventListener;

    // Status Input (Flagging)
    private boolean isMovingLeft, isMovingRight, isMovingUp, isMovingDown;

    /**
     * Konstruktor ViewModel.
     * Menerima username agar ViewModel bisa melakukan penyimpanan data otomatis (Auto-Save)
     * saat Game Over, tanpa membebani View.
     */
    public GameViewModel(GameEventListener listener, List<Image> alienImages,
                         List<Image> meteorImages, Image playerImage,
                         UserStats initialStats, String username) {

        this.eventListener = listener;
        this.alienImages = alienImages;
        this.meteorImages = meteorImages;
        this.playerImage = playerImage;
        this.initialStats = initialStats;

        this.currentUsername = username;
        this.userRepo = new UserRepository();
        this.soundEffect = new Sound();
        this.random = new Random();

        initGame();
    }

    /**
     * Inisialisasi awal objek-objek permainan.
     * Menyiapkan posisi player, memuat skor lama (jika ada), dan memunculkan rintangan awal.
     */
    private void initGame() {
        // Player diletakkan di tengah layar
        player = new Player(screenWidth / 2 - 25, screenHeight / 2 - 25, 50, 50, playerImage);

        // Jika ini adalah kelanjutan game (User lama), kembalikan statusnya
        if (initialStats != null) {
            player.setScore(initialStats.getSkor());
            player.setAmmo(initialStats.getSisaPeluru());
            player.setAmmoMissed(initialStats.getPeluruMeleset());
        }

        aliens = new ArrayList<>();
        bullets = new ArrayList<>();
        obstacles = new ArrayList<>();

        // Spawn 5 batu meteor sebagai rintangan awal
        for (int i = 0; i < 5; i++) {
            spawnSingleObstacle();
        }
    }

    /**
     * GAME LOOP (Jantung Permainan).
     * Metode ini berjalan di Thread terpisah agar UI tidak macet.
     * Loop ini melakukan 3 hal berulang-ulang:
     * 1. Update Logika (Gerakkan benda, cek tabrakan).
     * 2. Render (Suruh View gambar ulang).
     * 3. Tidur (Sleep) sebentar untuk menjaga kecepatan stabil (~60 FPS).
     */
    @Override
    public void run() {
        while (isRunning) {
            updateGame(); // 1. Update

            if (eventListener != null) {
                eventListener.onGameUpdate(); // 2. Render Signal
            }

            try {
                Thread.sleep(16); // 3. Sleep (~16ms)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startGame() {
        if (isRunning) return;
        isRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void stopGame() {
        isRunning = false;
        try {
            if (gameThread != null && gameThread.isAlive()) {
                gameThread.join(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // --- LOGIKA UTAMA ---

    /**
     * Pusat penanganan Game Over.
     * * Sesuai prinsip MVVM, ViewModel-lah yang bertanggung jawab menyimpan data,
     * bukan View. View hanya diberitahu untuk menampilkan popup.
     */
    private void handleGameOver() {
        isRunning = false;
        soundEffect.play("sfx_lose.wav");

        // Simpan data ke Database
        userRepo.updateUserStats(
                currentUsername,
                player.getScore(),
                player.getAmmoMissed(),
                player.getAmmo()
        );

        // Beritahu View
        if (eventListener != null) {
            eventListener.onGameOver(player.getScore());
        }
    }

    private void updateGame() {
        // 1. Gerakkan Player
        // ViewModel hanya meneruskan status input ke Model Player.
        int oldX = player.getX();
        int oldY = player.getY();
        player.move(isMovingLeft, isMovingRight, isMovingUp, isMovingDown, screenWidth, screenHeight);

        // 2. Cek Tabrakan Player vs Dinding/Batu
        for (Obstacle obs : obstacles) {
            if (player.checkCollision(obs)) {
                player.rollback(oldX, oldY); // Batalkan gerakan jika nabrak
                break;
            }
        }

        // 3. Spawn Alien Secara Acak (Peluang 2%)
        if (random.nextInt(100) < 2) {
            spawnAlien();
        }

        updateAliens();
        updateBullets();
    }

    private void updateAliens() {
        Iterator<Alien> it = aliens.iterator();
        while (it.hasNext()) {
            Alien alien = it.next();
            int oldY = alien.getY();

            // Alien bergerak sendiri (Logic ada di class Alien)
            alien.move();

            // Cek Alien nabrak Batu
            boolean hitRock = false;
            for (Obstacle obs : obstacles) {
                if (alien.checkCollision(obs)) {
                    alien.setY(oldY); // Alien tertahan
                    hitRock = true;
                    break;
                }
            }

            // Cek Alien nabrak Player (Game Over)
            if (!hitRock && alien.checkCollision(player)) {
                handleGameOver();
                return;
            }

            // Hapus Alien jika lewat layar
            if (alien.getY() + alien.getHeight() < 0) {
                it.remove();
                continue;
            }

            // Alien Menembak (Peluang 1 banding 300)
            if (random.nextInt(300) < 1) {
                // Alien menghitung sendiri arah tembakannya ke Player
                Bullet newBullet = alien.shootAt(player);
                bullets.add(newBullet);
                soundEffect.play("sfx_laser2.wav");
            }
        }
    }

    private void updateBullets() {
        int obstaclesDestroyedCount = 0;
        Iterator<Bullet> it = bullets.iterator();

        while (it.hasNext()) {
            Bullet bullet = it.next();
            boolean isBulletDead = false;

            // Peluru bergerak sendiri (Logic di class Bullet)
            bullet.move();

            if (bullet.isEnemyBullet()) {
                // Hapus jika keluar layar
                if (bullet.getY() > screenHeight + 50 || bullet.getX() < -50 || bullet.getX() > screenWidth + 50) {
                    player.addAmmo(1);      // Reward menghindar
                    player.addAmmoMissed();
                    it.remove();
                    continue;
                }
                // Cek kena Player
                if (bullet.checkCollision(player)) {
                    handleGameOver();
                    return;
                }
            } else {
                // Peluru Player kena Alien
                Iterator<Alien> alienIt = aliens.iterator();
                while (alienIt.hasNext()) {
                    Alien alien = alienIt.next();
                    if (bullet.checkCollision(alien)) {
                        alienIt.remove();    // Alien Mati
                        player.addScore(10);
                        isBulletDead = true;
                        soundEffect.play("sfx_twoTone.wav");
                        break;
                    }
                }
                if (bullet.getY() < -50) isBulletDead = true;
            }

            // Cek Peluru kena Batu
            if (!isBulletDead) {
                Iterator<Obstacle> obsIt = obstacles.iterator();
                while (obsIt.hasNext()) {
                    Obstacle obs = obsIt.next();
                    if (bullet.checkCollision(obs)) {
                        obs.hit(); // Kurangi HP batu
                        isBulletDead = true;
                        if (obs.isDestroyed()) {
                            obsIt.remove();
                            obstaclesDestroyedCount++;
                        }
                        break;
                    }
                }
            }

            if (isBulletDead) it.remove();
        }

        // Respawn batu jika ada yang hancur
        for (int i = 0; i < obstaclesDestroyedCount; i++) {
            spawnSingleObstacle();
        }
    }

    // --- INPUT HANDLING ---

    public void setMoveLeft(boolean move) { isMovingLeft = move; }
    public void setMoveRight(boolean move) { isMovingRight = move; }
    public void setMoveUp(boolean move) { isMovingUp = move; }
    public void setMoveDown(boolean move) { isMovingDown = move; }

    /**
     * Aksi Menembak Pemain.
     * Menerima koordinat mouse, lalu meminta Player membuat peluru ke arah tersebut.
     */
    public void playerShoot(int targetX, int targetY) {
        Bullet newBullet = player.shootAt(targetX, targetY);

        if (newBullet != null) {
            bullets.add(newBullet);
            soundEffect.play("sfx_laser1.wav");
        }
    }

    // --- SPAWN HELPERS ---

    private void spawnSingleObstacle() {
        // Mencari posisi random yang aman (tidak menimpa player)
        boolean validPosition = false;
        int attempts = 0;
        while (!validPosition && attempts < 10) {
            int x = random.nextInt(screenWidth - 60);
            int y = random.nextInt(screenHeight - 200) + 50;
            Rectangle tempRect = new Rectangle(x, y, 60, 60);

            if (!tempRect.intersects(player.getBounds())) {
                Image img = (meteorImages != null && !meteorImages.isEmpty())
                        ? meteorImages.get(random.nextInt(meteorImages.size())) : null;
                obstacles.add(new Obstacle(x, y, 60, 60, img));
                validPosition = true;
            }
            attempts++;
        }
    }

    private void spawnAlien() {
        int randomX = random.nextInt(screenWidth - 40);
        Image img = (alienImages != null && !alienImages.isEmpty())
                ? alienImages.get(random.nextInt(alienImages.size())) : null;
        aliens.add(new Alien(randomX, screenHeight, 40, 40, img));
    }

    // Getters untuk View
    public Player getPlayer() { return player; }
    public List<Alien> getAliens() { return aliens; }
    public List<Bullet> getBullets() { return bullets; }
    public List<Obstacle> getObstacles() { return obstacles; }
}