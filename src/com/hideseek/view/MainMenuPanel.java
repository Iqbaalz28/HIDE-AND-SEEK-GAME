package com.hideseek.view;

import com.hideseek.viewmodel.MenuViewModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
    private MenuViewModel viewModel;  // ViewModel khusus menu yang menangani pengambilan data dari database

    /**
     * Konstruktor Panel Menu.
     * @param playAction Aksi (Logika) yang akan dijalankan saat tombol START ditekan.
     * Logika ini dikirim dari GameWindow, sehingga panel ini tidak perlu
     * tahu detail teknis perpindahan halaman.
     */
    public MainMenuPanel(ActionListener playAction) {
        this.viewModel = new MenuViewModel();               // Inisialisasi ViewModel untuk komunikasi database
        this.setLayout(new BorderLayout());                 // Mengatur tata letak utama dan warna latar belakang
        this.setBackground(new Color(33, 37, 41)); // Warna abu-abu gelap agar nyaman di mata

        // --- BAGIAN 1: Judul ---
        // Menempatkan teks judul besar di bagian paling atas panel.
        JLabel titleLabel = new JLabel("HIDE AND SEEK: THE CHALLENGE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(13, 202, 240));

        // Memberikan jarak (padding) visual di sekitar judul agar tidak terlalu mepet.
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        this.add(titleLabel, BorderLayout.NORTH);

        // --- 2. TABEL SKOR (Center) ---
        // Kustomisasi Tabel agar terlihat modern (Flat Design)
        scoreTable = new JTable();
        scoreTable.setRowHeight(30); // Baris lebih tinggi agar tidak sempit
        scoreTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        scoreTable.setSelectionBackground(new Color(13, 202, 240)); // Warna saat diklik
        scoreTable.setSelectionForeground(Color.BLACK);
        scoreTable.setShowVerticalLines(false); // Menghilangkan garis vertikal (lebih bersih)

        // Kustomisasi Header Tabel
        JTableHeader header = scoreTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(50, 50, 50));
        header.setForeground(Color.WHITE);
        header.setOpaque(true);

        // Mengatur perataan teks di tengah (Center Alignment) untuk sel tabel
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        scoreTable.setDefaultRenderer(Object.class, centerRenderer);

        refreshTable(); // Muat data

        // FITUR BARU: Mouse Listener untuk Klik Tabel
        // Saat pengguna mengklik baris tabel, username akan diambil dan dimasukkan ke TextField.
        scoreTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = scoreTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Mengambil data dari kolom ke-0 (Username)
                    String selectedUser = scoreTable.getValueAt(selectedRow, 0).toString();
                    usernameField.setText(selectedUser);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(scoreTable);
        scrollPane.getViewport().setBackground(new Color(33, 37, 41)); // Menyamakan warna background
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50)); // Margin kiri-kanan
        this.add(scrollPane, BorderLayout.CENTER);

        // --- 3. PANEL INPUT (South) ---
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        inputPanel.setBackground(new Color(40, 44, 52)); // Sedikit lebih terang dari background utama
        inputPanel.setPreferredSize(new Dimension(800, 100));

        // Label
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userLabel.setForeground(Color.WHITE);
        inputPanel.add(userLabel);

        // Input Field dengan Padding
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5) // Padding dalam text field
        ));
        inputPanel.add(usernameField);

        // Tombol Start dengan gaya Modern
        playButton = new JButton("START GAME");
        playButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        playButton.setBackground(new Color(25, 135, 84)); // Hijau modern
        playButton.setForeground(Color.WHITE);
        playButton.setFocusable(false);
        playButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25)); // Padding tombol
        playButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Ubah kursor jadi tangan saat hover
        playButton.addActionListener(playAction);

        inputPanel.add(playButton);

        this.add(inputPanel, BorderLayout.SOUTH);
    }

    public String getUsername() {
        return usernameField.getText().trim();
    }

    public void refreshTable() {
        scoreTable.setModel(viewModel.getTableData());
    }
}