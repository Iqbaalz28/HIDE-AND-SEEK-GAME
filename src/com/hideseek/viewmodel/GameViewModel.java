package com.hideseek.viewmodel;

import com.hideseek.model.*;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameViewModel implements Runnable {
    // Game State Data
    private Player player;
    private List<Alien> aliens;
    private List<Bullet> bullets;
    private List<Obstacle> obstacles;

    // Resources Images
    private List<Image> alienImages;
    private List<Image> meteorImages;

    // Game Settings
    private int screenWidth = 800;
    private int screenHeight = 600;
    private boolean isRunning = false;
    private Thread gameThread;
    private GameEventListener eventListener;
    private Random random = new Random();

    // Input State
    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;
    private boolean isMovingUp = false;
    private boolean isMovingDown = false;

    // Konstruktor menerima List Gambar
    public GameViewModel(GameEventListener listener, List<Image> alienImages, List<Image> meteorImages) {
        this.eventListener = listener;
        this.alienImages = alienImages;
        this.meteorImages = meteorImages;
        initGame();
    }

    private void initGame() {
        player = new Player(screenWidth / 2 - 25, screenHeight / 2 - 25, 50, 50, null);
        aliens = new ArrayList<>();
        bullets = new ArrayList<>();
        obstacles = new ArrayList<>();

        // Generate 5 batu awal
        for (int i = 0; i < 5; i++) {
            spawnSingleObstacle();
        }
    }

    // Helper method untuk spawn 1 batu di posisi acak dengan gambar acak
    private void spawnSingleObstacle() {
        boolean validPosition = false;
        int maxAttempts = 10; // Coba 10 kali mencari posisi kosong
        int attempts = 0;

        while (!validPosition && attempts < maxAttempts) {
            int x = random.nextInt(screenWidth - 60);
            int y = random.nextInt(screenHeight - 200) + 50;

            Rectangle newRect = new Rectangle(x, y, 60, 60);

            // Cek tabrakan dengan player agar tidak spawn menimpa player
            if (!newRect.intersects(player.getBounds())) {
                // Pilih gambar acak
                Image randomImg = null;
                if (meteorImages != null && !meteorImages.isEmpty()) {
                    randomImg = meteorImages.get(random.nextInt(meteorImages.size()));
                }

                obstacles.add(new Obstacle(x, y, 60, 60, randomImg));
                validPosition = true;
            }
            attempts++;
        }
    }

    public void startGame() {
        if (isRunning) return;
        isRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        while (isRunning) {
            updateGame();
            if (eventListener != null) {
                eventListener.onGameUpdate();
            }
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateGame() {
        movePlayer();

        if (random.nextInt(100) < 2) {
            spawnAlien();
        }

        updateAliens();
        updateBullets();
    }

    private void movePlayer() {
        int speed = 5;
        // Simpan posisi lama untuk rollback jika nabrak batu
        int oldX = player.getX();
        int oldY = player.getY();

        if (isMovingLeft && player.getX() > 0) player.setX(player.getX() - speed);
        if (isMovingRight && player.getX() < screenWidth - player.getWidth()) player.setX(player.getX() + speed);
        if (isMovingUp && player.getY() > 0) player.setY(player.getY() - speed);
        if (isMovingDown && player.getY() < screenHeight - player.getHeight()) player.setY(player.getY() + speed);

        // Cek Tabrakan Player dengan Batu
        Iterator<Obstacle> it = obstacles.iterator();
        while (it.hasNext()) {
            Obstacle obs = it.next();
            if (player.getBounds().intersects(obs.getBounds())) {
                // Batalkan gerakan (tembok solid)
                player.setX(oldX);
                player.setY(oldY);
                break;
            }
        }
    }

    private void spawnAlien() {
        int randomX = random.nextInt(screenWidth - 40);

        // 1. Siapkan variabel gambar
        Image randomImage = null;

        // 2. Cek apakah list gambar ada isinya
        if (alienImages != null && !alienImages.isEmpty()) {
            // 3. Ambil acak salah satu gambar
            int randomIndex = random.nextInt(alienImages.size());
            randomImage = alienImages.get(randomIndex);
        }

        // 4. Masukkan 'randomImage' ke konstruktor Alien
        aliens.add(new Alien(randomX, screenHeight, 40, 40, randomImage));
    }

    private void updateAliens() {
        Iterator<Alien> it = aliens.iterator();
        while (it.hasNext()) {
            Alien alien = it.next();

            // Simpan posisi lama sebelum bergerak
            int oldY = alien.getY();

            // Coba gerakkan Alien ke ATAS
            alien.setY(alien.getY() - 3);

            // 1. Cek Tabrakan dengan Batu
            boolean hitRock = false;
            Iterator<Obstacle> obsIt = obstacles.iterator();
            while (obsIt.hasNext()) {
                Obstacle obs = obsIt.next();
                if (alien.getBounds().intersects(obs.getBounds())) {
                    // Jika nabrak batu, batalkan gerakan (Alien tertahan di balik batu)
                    alien.setY(oldY);
                    hitRock = true;
                    break;
                }
            }

            // 2. Cek Tabrakan dengan Player (Game Over)
            if (!hitRock && alien.getBounds().intersects(player.getBounds())) {
                isRunning = false;
                if (eventListener != null) eventListener.onGameOver(player.getScore());
            }

            // 3. Hapus alien jika keluar layar atas
            if (alien.getY() + alien.getHeight() < 0) {
                it.remove();
                continue; // Lanjut ke alien berikutnya
            }

            // 4. LOGIKA MENEMBAK: Alien menembak secara acak
            if (random.nextInt(100) < 1) { // 1% chance
                // Alien menembak lurus ke atas (ke arah player)
                bullets.add(new Bullet(alien.getX() + 15, alien.getY(), 10, 20, null, true));
            }
        }
    }

    private void updateBullets() {
        // Variabel untuk menghitung jumlah batu yang hancur di frame ini
        // Digunakan untuk me-respawn batu baru di akhir method
        int obstaclesDestroyedCount = 0;

        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet bullet = it.next();
            boolean isBulletDead = false; // Penanda apakah peluru harus dihapus

            // --- LOGIKA PELURU MUSUH (ALIEN) ---
            if (bullet.isEnemyBullet()) {
                // 1. Gerakkan ke Atas
                bullet.setY(bullet.getY() - 7);

                // 2. Cek Keluar Layar Atas (MEKANISME MENAMBAH AMMO)
                if (bullet.getY() + bullet.getHeight() < -20) {
                    player.addAmmo(1);       // Tambah Ammo
                    player.addAmmoMissed();  // Catat Statistik
                    it.remove();             // Hapus langsung dari memori
                    continue;                // Lanjut ke peluru berikutnya
                }

                // 3. Cek Kena Player
                if (bullet.getBounds().intersects(player.getBounds())) {
                    isRunning = false;
                    if (eventListener != null) eventListener.onGameOver(player.getScore());
                }
            }

            // --- LOGIKA PELURU PEMAIN ---
            else {
                // 1. Gerakkan ke Bawah
                bullet.setY(bullet.getY() + 7);

                // 2. Cek Kena Alien
                Iterator<Alien> alienIt = aliens.iterator();
                while (alienIt.hasNext()) {
                    Alien alien = alienIt.next();
                    if (bullet.getBounds().intersects(alien.getBounds())) {
                        alienIt.remove();    // Alien mati
                        player.addScore(10); // Tambah skor
                        isBulletDead = true; // Tandai peluru hancur
                        break;
                    }
                }

                // 3. Cek Keluar Layar Bawah
                if (bullet.getY() > screenHeight) {
                    isBulletDead = true;
                }
            }

            // --- LOGIKA TABRAKAN BATU (Berlaku untuk Kedua Peluru) ---
            // Hanya cek jika peluru belum mati
            if (!isBulletDead) {
                Iterator<Obstacle> obsIt = obstacles.iterator();
                while (obsIt.hasNext()) {
                    Obstacle obs = obsIt.next();
                    if (bullet.getBounds().intersects(obs.getBounds())) {
                        obs.hit();           // Batu berkurang HP
                        isBulletDead = true; // Peluru hancur

                        if (obs.isDestroyed()) {
                            obsIt.remove();  // Hapus batu lama
                            obstaclesDestroyedCount++; // Tandai untuk spawn batu baru
                        }
                        break;
                    }
                }
            }

            // --- PENGHAPUSAN AKHIR PELURU ---
            if (isBulletDead) {
                it.remove();
            }
        }

        // --- RESPAWN BATU BARU ---
        // Dilakukan di luar loop Iterator peluru untuk keamanan data
        for (int i = 0; i < obstaclesDestroyedCount; i++) {
            spawnSingleObstacle();
        }
    }

    // Input Handlers
    public void setMoveLeft(boolean move) { isMovingLeft = move; }
    public void setMoveRight(boolean move) { isMovingRight = move; }
    public void setMoveUp(boolean move) { isMovingUp = move; }
    public void setMoveDown(boolean move) { isMovingDown = move; }

    public void playerShoot() {
        if (player.getAmmo() > 0) {
            bullets.add(new Bullet(player.getX() + 20, player.getY() + player.getHeight(), 10, 20, null, false));
            player.decreaseAmmo();
        }
    }

    // Getters
    public Player getPlayer() { return player; }
    public List<Alien> getAliens() { return aliens; }
    public List<Bullet> getBullets() { return bullets; }
    public List<Obstacle> getObstacles() { return obstacles; }
}