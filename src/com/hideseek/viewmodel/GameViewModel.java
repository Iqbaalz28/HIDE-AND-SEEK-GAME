package com.hideseek.viewmodel;

import com.hideseek.model.*;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Kelas ViewModel ini berfungsi sebagai pusat pengendali logika permainan (Game Engine).
 * * Dalam pola MVVM, kelas ini memisahkan logika bisnis dari tampilan (View).
 * * Ia bertanggung jawab untuk menyimpan status permainan (posisi pemain, musuh, peluru),
 * menjalankan simulasi fisika (gerakan, tabrakan), dan mengatur jalannya waktu permainan
 * menggunakan mekanisme Threading.
 */
public class GameViewModel implements Runnable {

    // --- Bagian Data Model (State of the Game) ---
    // Variabel-variabel ini menyimpan seluruh kondisi permainan saat ini.
    // View akan membaca data ini secara terus-menerus untuk menggambar layar.
    private Player player;             // Objek karakter utama
    private List<Alien> aliens;        // Daftar musuh yang aktif di layar
    private List<Bullet> bullets;      // Daftar peluru yang sedang melayang
    private List<Obstacle> obstacles;  // Daftar rintangan (batu/meteor)

    // --- Bagian Sumber Daya (Assets) ---
    // Referensi ke gambar-gambar yang dimuat satu kali di awal oleh View.
    // Disimpan di sini agar ViewModel bisa menyematkannya saat membuat objek baru (misal: spawn alien baru).
    private List<Image> alienImages;
    private List<Image> meteorImages;
    private Image playerImage;
    private Sound soundEffect = new Sound();

    // --- Pengaturan Game & Threading ---
    private int screenWidth = 800;
    private int screenHeight = 600;

    // Flag kontrol untuk menghidupkan atau mematikan loop permainan.
    private boolean isRunning = false;

    // Thread terpisah untuk menjalankan logika permainan secara paralel agar UI tidak macet (freeze).
    private Thread gameThread;

    // Antarmuka untuk mengirim sinyal balik ke View (misal: minta repaint atau game over).
    private GameEventListener eventListener;

    // Generator angka acak untuk posisi spawn dan variasi musuh.
    private Random random = new Random();

    // --- Status Input Pemain ---
    // Variabel ini menyimpan status tombol keyboard yang sedang ditekan.
    // Digunakan untuk menggerakkan pemain secara halus dalam game loop.
    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;
    private boolean isMovingUp = false;
    private boolean isMovingDown = false;

    // Wadah sementara untuk menyimpan data statistik yang dibawa dari Menu (untuk fitur Load Game).
    private UserStats initialStats;

    /**
     * Konstruktor ViewModel.
     * Tugas utamanya adalah menerima seluruh ketergantungan (dependencies) yang dibutuhkan
     * dari luar, seperti aset gambar dan data pemain, lalu mempersiapkan permainan.
     *
     * @param listener Objek penghubung untuk komunikasi ke View.
     * @param alienImages Koleksi gambar variasi alien.
     * @param meteorImages Koleksi gambar variasi meteor.
     * @param playerImage Gambar karakter pemain.
     * @param initialStats Data skor dan peluru terakhir dari database (untuk melanjutkan permainan).
     */
    public GameViewModel(GameEventListener listener, List<Image> alienImages, List<Image> meteorImages, Image playerImage, UserStats initialStats) {
        this.eventListener = listener;
        this.alienImages = alienImages;
        this.meteorImages = meteorImages;
        this.playerImage = playerImage;
        this.initialStats = initialStats;

        // Memanggil metode inisialisasi untuk menyusun objek-objek game awal.
        initGame();
    }

    /**
     * Metode inisialisasi untuk menyetel kondisi awal permainan.
     * Di sinilah objek pemain dibuat, data lama dimuat (jika ada), dan rintangan awal disebar.
     */
    private void initGame() {
        // 1. Menciptakan objek pemain di posisi tengah layar.
        player = new Player(screenWidth / 2 - 25, screenHeight / 2 - 25, 50, 50, playerImage);

        // 2. Logika Load Game (Melanjutkan Progres).
        // Jika ada data statistik yang dikirim dari menu, kita menyalinnya ke objek pemain.
        // Ini memastikan skor dan peluru berlanjut, tidak reset ke nol.
        if (initialStats != null) {
            player.setScore(initialStats.getSkor());
            player.setAmmo(initialStats.getSisaPeluru());
            player.setAmmoMissed(initialStats.getPeluruMeleset());
        }

        // Menyiapkan daftar kosong untuk entitas game lainnya.
        aliens = new ArrayList<>();
        bullets = new ArrayList<>();
        obstacles = new ArrayList<>();

        // 3. Menyebar 5 batu rintangan secara acak sebagai tantangan awal.
        for (int i = 0; i < 5; i++) {
            spawnSingleObstacle();
        }
    }

    /**
     * Inti dari Game Loop (Siklus Permainan).
     * Metode run() ini dijalankan oleh Thread terpisah. Selama isRunning bernilai true,
     * metode ini akan terus berputar untuk:
     * 1. Memperbarui logika (fisika, gerakan).
     * 2. Meminta View menggambar ulang layar.
     * 3. Istirahat sejenak (sleep) untuk menjaga kecepatan game tetap stabil (sekitar 60 FPS).
     */
    @Override
    public void run() {
        while (isRunning) {
            updateGame(); // Langkah Update Logika

            // Langkah Render: Memberitahu View bahwa data telah berubah dan layar perlu diperbarui.
            if (eventListener != null) {
                eventListener.onGameUpdate();
            }

            // Langkah Sinkronisasi Waktu (Frame Limiter).
            // Menahan eksekusi selama ~16ms agar game berjalan mulus di 60 frame per detik.
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Metode untuk memulai eksekusi permainan.
     * Membuat thread baru dan menjalankannya, memicu metode run().
     */
    public void startGame() {
        if (isRunning) return; // Mencegah pembuatan thread ganda.
        isRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Metode untuk menghentikan permainan secara aman.
     * Dipanggil saat pemain kalah, keluar ke menu, atau menutup aplikasi.
     * Mengubah flag isRunning menjadi false akan memutus siklus while di dalam run().
     */
    public void stopGame() {
        isRunning = false;
        try {
            // Menunggu thread benar-benar berhenti sebelum melanjutkan (Graceful Shutdown).
            if (gameThread != null && gameThread.isAlive()) {
                gameThread.join(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pusat pembaruan logika per frame (Frame Update).
     * Metode ini mengorkestrasi seluruh perubahan yang terjadi dalam satu detak waktu permainan:
     * - Menggerakkan pemain.
     * - Memunculkan musuh baru.
     * - Menggerakkan musuh dan peluru.
     * - Mendeteksi tabrakan.
     */
    private void updateGame() {
        movePlayer();

        // Logika Probabilitas Spawn Alien.
        // Setiap frame memiliki peluang 2% untuk memunculkan alien baru.
        if (random.nextInt(300) < 2) {
            spawnAlien();
        }

        updateAliens();
        updateBullets();
    }

    // --- LOGIKA PERGERAKAN PLAYER ---
    /**
     * Mengatur perpindahan posisi pemain berdasarkan input keyboard.
     * Termasuk di dalamnya adalah deteksi tabrakan dengan rintangan (Batu),
     * sehingga pemain tidak bisa menembus objek padat.
     */
    private void movePlayer() {
        int speed = 5; // Kecepatan gerak piksel per frame.

        // Menyimpan posisi lama untuk keperluan pembatalan gerak (rollback) jika menabrak.
        int oldX = player.getX();
        int oldY = player.getY();

        // Mengubah koordinat berdasarkan status tombol arah.
        if (isMovingLeft && player.getX() > 0) player.setX(player.getX() - speed);
        if (isMovingRight && player.getX() < screenWidth - player.getWidth()) player.setX(player.getX() + speed);
        if (isMovingUp && player.getY() > 0) player.setY(player.getY() - speed);
        if (isMovingDown && player.getY() < screenHeight - player.getHeight()) player.setY(player.getY() + speed);

        // Deteksi Tabrakan dengan Batu (Wall Collision).
        Iterator<Obstacle> it = obstacles.iterator();
        while (it.hasNext()) {
            Obstacle obs = it.next();
            // Jika kotak pemain bersinggungan dengan kotak batu...
            if (player.getBounds().intersects(obs.getBounds())) {
                // ...kembalikan pemain ke posisi sebelumnya (efek tertahan dinding).
                player.setX(oldX);
                player.setY(oldY);
                break;
            }
        }
    }

    // --- LOGIKA GENERATOR RINTANGAN ---
    /**
     * Mencari posisi acak yang aman untuk meletakkan batu baru.
     * Memastikan batu tidak muncul menimpa posisi pemain.
     */
    private void spawnSingleObstacle() {
        boolean validPosition = false;
        int maxAttempts = 10; // Batas percobaan mencari posisi kosong agar tidak infinite loop.
        int attempts = 0;

        while (!validPosition && attempts < maxAttempts) {
            int x = random.nextInt(screenWidth - 60);
            int y = random.nextInt(screenHeight - 200) + 50;

            Rectangle newRect = new Rectangle(x, y, 60, 60);

            // Validasi: Pastikan area ini tidak sedang diduduki pemain.
            if (!newRect.intersects(player.getBounds())) {
                // Memilih gambar batu secara acak untuk variasi visual.
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

    // --- LOGIKA GENERATOR MUSUH ---
    /**
     * Memunculkan alien baru di posisi acak di bagian bawah layar.
     */
    private void spawnAlien() {
        int randomX = random.nextInt(screenWidth - 40);
        Image randomImage = null;
        if (alienImages != null && !alienImages.isEmpty()) {
            randomImage = alienImages.get(random.nextInt(alienImages.size()));
        }
        // Alien muncul dari bawah layar (screenHeight) dan akan bergerak ke atas.
        aliens.add(new Alien(randomX, screenHeight, 40, 40, randomImage));
    }

    // --- LOGIKA PEMBARUAN MUSUH ---
    /**
     * Mengelola perilaku setiap alien yang ada di layar:
     * - Bergerak maju (ke atas).
     * - Mengecek tabrakan dengan batu atau pemain.
     * - Menembak secara acak.
     * - Menghapus alien yang sudah lewat layar.
     */
    private void updateAliens() {
        Iterator<Alien> it = aliens.iterator();
        while (it.hasNext()) {
            Alien alien = it.next();
            int oldY = alien.getY();

            // Gerakkan alien ke atas.
            alien.setY(alien.getY() - 3);

            // 1. Deteksi Tabrakan dengan Batu.
            boolean hitRock = false;
            Iterator<Obstacle> obsIt = obstacles.iterator();
            while (obsIt.hasNext()) {
                Obstacle obs = obsIt.next();
                if (alien.getBounds().intersects(obs.getBounds())) {
                    // Jika menabrak batu, alien tertahan (tidak maju).
                    alien.setY(oldY);
                    hitRock = true;
                    break;
                }
            }

            // 2. Deteksi Tabrakan dengan Pemain (Game Over).
            if (!hitRock && alien.getBounds().intersects(player.getBounds())) {
                isRunning = false; // Hentikan game loop.
                soundEffect.play("sfx_lose.wav");
                if (eventListener != null) eventListener.onGameOver(player.getScore());
            }

            // 3. Pembersihan Memori: Hapus alien jika sudah keluar layar atas.
            if (alien.getY() + alien.getHeight() < 0) {
                it.remove();
                continue;
            }

            // 4. Mekanisme Menembak: Alien memiliki peluang kecil untuk menembak balik.
            if (random.nextInt(200) < 1) { // 3% kemungkinan per frame.

                // A. Tentukan titik asal peluru (tengah alien)
                double startX = alien.getX() + 20;
                double startY = alien.getY() + 20;

                // B. Tentukan titik target (tengah player)
                double targetX = player.getX() + 25;
                double targetY = player.getY() + 25;

                // C. Hitung selisih jarak (Delta)
                double deltaX = targetX - startX;
                double deltaY = targetY - startY;

                // D. Hitung Sudut menggunakan Trigonometri (Arc Tangent)
                double angle = Math.atan2(deltaY, deltaX);

                // E. Tentukan Kecepatan Peluru Musuh
                double bulletSpeed = 7.0;

                // F. Pecah kecepatan menjadi komponen X dan Y berdasarkan sudut
                double velX = bulletSpeed * Math.cos(angle);
                double velY = bulletSpeed * Math.sin(angle);

                // G. Buat peluru dengan vektor kecepatan tersebut
                bullets.add(new Bullet((int)startX, (int)startY, 10, 20, null, true, velX, velY));
                soundEffect.play("sfx_laser2.wav");
            }
        }
    }

    // --- LOGIKA PEMBARUAN PELURU ---
    /**
     * Mengelola seluruh proyektil (baik milik teman maupun musuh):
     * - Menggerakkan peluru sesuai arahnya.
     * - Mendeteksi kena sasaran (Alien/Pemain).
     * - Mendeteksi kena batu (Menghancurkan batu).
     * - Memberikan reward jika peluru musuh berhasil dihindari.
     */
    private void updateBullets() {
        int obstaclesDestroyedCount = 0;

        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet bullet = it.next();
            boolean isBulletDead = false;

            // 1. GERAKKAN PELURU (Logika Baru)
            // Metode ini akan mengupdate X dan Y peluru sesuai arah tembakan
            bullet.move();

            // --- Logika Peluru Musuh ---
            if (bullet.isEnemyBullet()) {
                // Hapus jika keluar layar (Kiri, Kanan, atau Bawah)
                // Kita beri sedikit toleransi (margin) agar tidak hilang mendadak
                if (bullet.getY() > screenHeight + 50 || bullet.getX() < -50 || bullet.getX() > screenWidth + 50) {
                    // Jika peluru musuh berhasil lolos (keluar layar manapun), beri poin
                    player.addAmmo(1);
                    player.addAmmoMissed();
                    it.remove();
                    continue;
                }

                // Cek Kena Player
                if (bullet.getBounds().intersects(player.getBounds())) {
                    isRunning = false;
                    soundEffect.play("sfx_lose.wav");
                    if (eventListener != null) eventListener.onGameOver(player.getScore());
                }
            }
            // --- Logika Peluru Player ---
            else {
                // Cek Kena Alien
                Iterator<Alien> alienIt = aliens.iterator();
                while (alienIt.hasNext()) {
                    Alien alien = alienIt.next();
                    if (bullet.getBounds().intersects(alien.getBounds())) {
                        alienIt.remove();
                        player.addScore(10);
                        isBulletDead = true;
                        soundEffect.play("sfx_twoTone.wav");
                        break;
                    }
                }
                // Hapus jika keluar layar atas
                if (bullet.getY() < -50) isBulletDead = true;
            }

            // --- Logika Kena Batu (Sama seperti sebelumnya) ---
            if (!isBulletDead) {
                Iterator<Obstacle> obsIt = obstacles.iterator();
                while (obsIt.hasNext()) {
                    Obstacle obs = obsIt.next();
                    if (bullet.getBounds().intersects(obs.getBounds())) {
                        obs.hit();
                        isBulletDead = true;
                        if (obs.isDestroyed()) {
                            obsIt.remove();
                            obstaclesDestroyedCount++;
                        }
                        break;
                    }
                }
            }

            if (isBulletDead) {
                it.remove();
            }
        }

        // Respawn batu... (kode lama tetap sama)
        for (int i = 0; i < obstaclesDestroyedCount; i++) {
            spawnSingleObstacle();
        }
    }

    // --- PENANGANAN INPUT (Input Handlers) ---
    // Metode-metode ini dipanggil oleh GameCanvas saat tombol ditekan/dilepas.

    public void setMoveLeft(boolean move) { isMovingLeft = move; }
    public void setMoveRight(boolean move) { isMovingRight = move; }
    public void setMoveUp(boolean move) { isMovingUp = move; }
    public void setMoveDown(boolean move) { isMovingDown = move; }

    /**
     * Logika menembak bagi pemain.
     * Hanya bisa menembak jika stok peluru tersedia.
     */
    public void playerShoot(int targetX, int targetY) {
        if (player.getAmmo() > 0) {
            // 1. Menentukan titik pusat pemain sebagai asal tembakan.
            // Ditambah 25 (setengah ukuran player 50x50) agar peluru keluar tepat dari tengah.
            double startX = player.getX() + 25;
            double startY = player.getY() + 25;

            // 2. Menghitung selisih jarak antara target (mouse) dan pemain.
            double deltaX = targetX - startX;
            double deltaY = targetY - startY;

            // 3. Menghitung sudut tembakan menggunakan Arc Tangent (Trigonometri).
            double angle = Math.atan2(deltaY, deltaX);

            // 4. Menentukan kecepatan peluru pemain.
            double bulletSpeed = 10.0; // Lebih cepat dari peluru musuh agar responsif.

            // 5. Memecah kecepatan menjadi komponen vektor X dan Y.
            double velX = bulletSpeed * Math.cos(angle);
            double velY = bulletSpeed * Math.sin(angle);

            // 6. Membuat objek peluru dengan arah vektor yang telah dihitung.
            bullets.add(new Bullet((int)startX, (int)startY, 10, 20, null, false, velX, velY));
            soundEffect.play("sfx_laser1.wav");

            // 7. Mengurangi amunisi.
            player.decreaseAmmo();
        }
    }

    // --- AKSES DATA UNTUK VIEW (Getters) ---
    // Memberikan akses baca (Read-Only) ke daftar objek game agar bisa digambar oleh View.

    public Player getPlayer() { return player; }
    public List<Alien> getAliens() { return aliens; }
    public List<Bullet> getBullets() { return bullets; }
    public List<Obstacle> getObstacles() { return obstacles; }
}