package com.hideseek.viewmodel;

import com.hideseek.model.*;
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

    public GameViewModel(GameEventListener listener) {
        this.eventListener = listener;
        initGame();
    }

    private void initGame() {
        player = new Player(screenWidth / 2 - 25, screenHeight / 2 - 25, 50, 50, null);
        aliens = new ArrayList<>();
        bullets = new ArrayList<>();
        obstacles = new ArrayList<>(); 
        
        generateObstacles(); // Panggil fungsi buat batu
    }

    // Fungsi untuk generate batu acak
    private void generateObstacles() {
        // Buat misal 5 batu acak
        for (int i = 0; i < 5; i++) {
            int x = random.nextInt(screenWidth - 60);
            int y = random.nextInt(screenHeight - 200) + 50; // Jangan terlalu dekat batas atas/bawah
            
            // Cek agar tidak menumpuk di posisi spawn player
            Rectangle obstacleRect = new Rectangle(x, y, 60, 60);
            if (!obstacleRect.intersects(player.getBounds())) {
                obstacles.add(new Obstacle(x, y, 60, 60, null));
            }
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
        for (Obstacle obs : obstacles) {
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
        aliens.add(new Alien(randomX, screenHeight, 40, 40, null));
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
            for (Obstacle obs : obstacles) {
                if (alien.getBounds().intersects(obs.getBounds())) {
                    // Jika nabrak batu, batalkan gerakan (Alien tertahan di balik batu)
                    alien.setY(oldY);
                    hitRock = true;
                    break; 
                }
            }

            // 2. Cek Tabrakan dengan Player (Game Over)
            // Hanya cek jika alien berhasil bergerak (tidak tertahan batu)
            // Atau tetap cek, karena jika player mepet batu, alien di seberang batu tidak boleh membunuh player
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
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet bullet = it.next();
            boolean removeBullet = false;

            // Gerakan Peluru
            if (bullet.isEnemyBullet()) {
                bullet.setY(bullet.getY() - 7);
                if (bullet.getBounds().intersects(player.getBounds())) {
                    isRunning = false;
                    if (eventListener != null) eventListener.onGameOver(player.getScore());
                }
                if (bullet.getY() + bullet.getHeight() < 0) {
                    player.addAmmo(1);
                    player.addAmmoMissed();
                    removeBullet = true;
                }
            } else {
                bullet.setY(bullet.getY() + 7);
                Iterator<Alien> alienIt = aliens.iterator();
                while (alienIt.hasNext()) {
                    Alien alien = alienIt.next();
                    if (bullet.getBounds().intersects(alien.getBounds())) {
                        alienIt.remove();
                        player.addScore(10);
                        removeBullet = true;
                        break;
                    }
                }
                if (bullet.getY() > screenHeight) removeBullet = true;
            }

            // Cek Peluru kena Batu (Hancur)
            for (Obstacle obs : obstacles) {
                if (bullet.getBounds().intersects(obs.getBounds())) {
                    removeBullet = true; // Peluru hancur kena batu
                    break;
                }
            }

            if (removeBullet) {
                it.remove();
            }
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