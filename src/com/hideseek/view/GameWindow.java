package com.hideseek.view;

import com.hideseek.viewmodel.MenuViewModel;
import javax.swing.*;
import java.awt.*;

/**
 * Kelas ini berfungsi sebagai Jendela Utama (Main Frame) aplikasi.
 * GameWindow bertindak sebagai pengelola tampilan (View Manager) yang tugas utamanya
 * adalah menampung dan menukar-nukar tampilan antara "Menu Utama" dan "Area Permainan".
 *
 * Konsep yang digunakan mirip dengan sistem halaman pada situs web: satu jendela,
 * namun isinya bisa berubah-ubah tergantung interaksi pengguna.
 */
public class GameWindow extends JFrame {

    // Komponen manajemen tata letak.
    // CardLayout dipilih karena memungkinkan kita menumpuk beberapa panel (Menu & Game)
    // di tempat yang sama, dan hanya menampilkan satu panel dalam satu waktu.
    private CardLayout cardLayout;
    private JPanel mainContainer;

    // Dua halaman utama dalam aplikasi ini.
    private MainMenuPanel menuPanel;
    private GameCanvas gameCanvas;

    /**
     * Konstruktor Utama.
     * Di sini kita membangun struktur dasar jendela, mengatur dimensi, dan
     * menghubungkan logika navigasi antar halaman.
     */
    public GameWindow() {
        // Pengaturan properti dasar jendela aplikasi (Judul, perilaku tutup, ukuran).
        this.setTitle("Hide and Seek: The Challenge");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setResizable(false); // Ukuran dikunci agar tata letak game konsisten.

        // Inisialisasi CardLayout dan wadah panel utama.
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // --- Inisialisasi Halaman 1: Menu Utama ---
        // Di sini kita mendefinisikan apa yang harus dilakukan ketika tombol "START GAME" ditekan.
        // Logika ini (Lambda Expression) dikirimkan ke dalam MainMenuPanel sebagai aksi.
        menuPanel = new MainMenuPanel(e -> {
            // Langkah 1: Ambil input nama dari text field.
            String username = menuPanel.getUsername();

            // Langkah 2: Validasi input (tidak boleh kosong).
            if (!username.isEmpty()) {
                // Langkah 3: Daftarkan pengguna ke database (atau load jika sudah ada).
                new MenuViewModel().registerUser(username);

                // Langkah 4: Perintahkan jendela ini untuk beralih ke tampilan Game.
                showGame(username);
            } else {
                // Memberikan umpan balik jika user lupa mengisi nama.
                JOptionPane.showMessageDialog(this, "Masukkan Username terlebih dahulu!");
            }
        });

        // --- Inisialisasi Halaman 2: Area Permainan (Canvas) ---
        // Kita mengirimkan referensi 'this' (GameWindow) ke dalam GameCanvas.
        // Tujuannya agar nanti GameCanvas bisa memerintahkan jendela ini untuk
        // kembali ke menu saat permainan berakhir (Game Over).
        gameCanvas = new GameCanvas(this);

        // --- Penyusunan Halaman ---
        // Menambahkan kedua panel ke dalam wadah utama dan memberikan "nama panggilan" (key).
        // Nama "MENU" dan "GAME" ini nanti digunakan sebagai alamat untuk berpindah halaman.
        mainContainer.add(menuPanel, "MENU");
        mainContainer.add(gameCanvas, "GAME");

        // Menambahkan wadah utama ke dalam frame jendela.
        this.add(mainContainer);

        this.setLocationRelativeTo(null); // Menempatkan jendela tepat di tengah layar monitor.
        this.setVisible(true); // Memunculkan jendela ke layar.

        // Memastikan aplikasi dimulai dengan menampilkan Menu Utama terlebih dahulu.
        showMenu();
    }

    /**
     * Metode Navigasi: Beralih ke Mode Permainan.
     * Metode ini mengatur transisi dari Menu ke Game, termasuk inisialisasi logika
     * dan penanganan fokus input.
     *
     * @param username Nama pemain yang akan digunakan untuk sesi permainan ini.
     */
    public void showGame(String username) {
        // 1. Memerintahkan Canvas untuk mereset kondisi dan memuat data pemain dari DB.
        gameCanvas.startNewGame(username);

        // 2. Menginstruksikan CardLayout untuk membalik tampilan ke panel "GAME".
        cardLayout.show(mainContainer, "GAME");

        // 3. Solusi Teknis untuk Fokus Keyboard (PENTING):
        // Saat tampilan berpindah, fokus input seringkali tertinggal di tombol menu.
        // SwingUtilities.invokeLater memastikan perintah 'minta fokus' dieksekusi
        // SETELAH proses penggambaran tata letak selesai sepenuhnya.
        // Tanpa ini, tombol keyboard (panah/spasi) seringkali tidak merespons di awal game.
        SwingUtilities.invokeLater(() -> {
            gameCanvas.requestFocusInWindow();
        });
    }

    /**
     * Metode Navigasi: Beralih ke Menu Utama.
     * Metode ini menangani proses pembersihan sesi permainan dan kembali ke tampilan awal.
     */
    public void showMenu() {
        // 1. Menghentikan loop permainan agar tidak memakan sumber daya di latar belakang.
        gameCanvas.stopGame();

        // 2. Memperbarui tabel skor di menu agar menampilkan poin terbaru yang baru saja didapat.
        menuPanel.refreshTable();

        // 3. Menginstruksikan CardLayout untuk membalik tampilan kembali ke panel "MENU".
        cardLayout.show(mainContainer, "MENU");
    }
}