package com.hideseek.view;

import com.hideseek.viewmodel.MenuViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Kelas ini merepresentasikan tampilan "Menu Utama" (Dashboard) dari aplikasi.
 * Panel ini adalah hal pertama yang dilihat pengguna saat aplikasi dibuka.
 *
 * Fungsinya terbagi menjadi dua tujuan utama:
 * 1. Menampilkan informasi papan peringkat (High Score) kepada pengguna.
 * 2. Menyediakan formulir login sederhana (input username) untuk memulai permainan.
 */
public class MainMenuPanel extends JPanel {

    // Komponen-komponen antarmuka pengguna (UI Components)
    private JTextField usernameField; // Kotak input untuk mengetik nama
    private JButton playButton;       // Tombol aksi untuk memulai
    private JTable scoreTable;        // Tabel untuk menampilkan data statistik

    // ViewModel khusus menu yang menangani pengambilan data dari database
    private MenuViewModel viewModel;

    /**
     * Konstruktor Panel Menu.
     * Di sini kita menyusun tata letak visual menggunakan strategi 'BorderLayout'.
     * Layout ini membagi layar menjadi 5 area (Atas, Bawah, Kiri, Kanan, Tengah),
     * yang sangat cocok untuk struktur menu standar.
     *
     * @param playAction Aksi (Logika) yang akan dijalankan saat tombol START ditekan.
     * Logika ini dikirim dari GameWindow, sehingga panel ini tidak perlu
     * tahu detail teknis perpindahan halaman.
     */
    public MainMenuPanel(ActionListener playAction) {
        // Inisialisasi ViewModel untuk komunikasi database
        this.viewModel = new MenuViewModel();

        // Mengatur tata letak utama dan warna latar belakang
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(30, 30, 30)); // Warna abu-abu gelap agar nyaman di mata

        // --- BAGIAN 1: Judul (BorderLayout.NORTH) ---
        // Menempatkan teks judul besar di bagian paling atas panel.
        JLabel titleLabel = new JLabel("HIDE AND SEEK: THE CHALLENGE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 24));
        titleLabel.setForeground(Color.CYAN); // Warna teks kontras (Cyan)

        // Memberikan jarak (padding) visual di sekitar judul agar tidak terlalu mepet.
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        this.add(titleLabel, BorderLayout.NORTH);

        // --- BAGIAN 2: Tabel Skor (BorderLayout.CENTER) ---
        // Menempatkan tabel skor di area tengah yang akan mengisi sisa ruang yang tersedia.
        scoreTable = new JTable();
        scoreTable.setFillsViewportHeight(true); // Memastikan tabel mengisi tinggi area

        // Memuat data awal dari database agar tabel langsung terisi saat aplikasi dibuka.
        refreshTable();

        // Membungkus tabel dengan ScrollPane agar bisa digulir (scroll) jika datanya banyak.
        JScrollPane scrollPane = new JScrollPane(scoreTable);
        this.add(scrollPane, BorderLayout.CENTER);

        // --- BAGIAN 3: Panel Input (BorderLayout.SOUTH) ---
        // Area bawah membutuhkan tata letak sendiri karena berisi beberapa komponen
        // (Label + Input + Tombol) yang berjejer ke samping.
        // Kita menggunakan sub-panel dengan 'FlowLayout' untuk keperluan ini.
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        inputPanel.setBackground(new Color(50, 50, 50)); // Sedikit lebih terang dari background utama

        // Label instruksi
        JLabel userLabel = new JLabel("Username: ");
        userLabel.setForeground(Color.WHITE);
        inputPanel.add(userLabel);

        // Kotak input teks
        usernameField = new JTextField(15);
        inputPanel.add(usernameField);

        // Tombol Mulai
        playButton = new JButton("START GAME");
        playButton.setBackground(Color.GREEN);
        playButton.setFocusable(false); // Mematikan fokus agar tidak mengganggu navigasi keyboard

        // Menghubungkan tombol dengan aksi yang dikirim dari GameWindow
        playButton.addActionListener(playAction);
        inputPanel.add(playButton);

        // Menambahkan sub-panel input ke bagian bawah panel utama
        this.add(inputPanel, BorderLayout.SOUTH);
    }

    /**
     * Metode akses untuk mengambil teks yang diketik pengguna di kotak username.
     * Digunakan oleh GameWindow untuk mendaftarkan pemain ke database sebelum game dimulai.
     *
     * @return String nama pengguna (tanpa spasi berlebih di awal/akhir).
     */
    public String getUsername() {
        return usernameField.getText().trim();
    }

    /**
     * Metode untuk memperbarui tampilan tabel.
     * Metode ini meminta ViewModel untuk mengambil data terbaru dari database,
     * lalu memasangkannya ke tabel. Sangat berguna dipanggil setelah permainan selesai
     * untuk menampilkan skor terbaru.
     */
    public void refreshTable() {
        scoreTable.setModel(viewModel.getTableData());
    }
}