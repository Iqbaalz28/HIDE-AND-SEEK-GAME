package com.hideseek.model;

/**
 * Kelas Model ini berfungsi sebagai representasi objek (Entity) dari tabel 'tbenefit'
 * yang ada di dalam basis data.
 *
 * Dalam arsitektur aplikasi ini, UserStats bertindak sebagai wadah data (Data Container).
 * Saat aplikasi membaca data dari database, baris-baris data mentah akan dikonversi
 * menjadi objek UserStats agar lebih mudah dikelola, dikirim antar komponen (seperti
 * dari ViewModel ke View), dan dimanipulasi menggunakan logika Java standar.
 */
public class UserStats {

    /*
     * Atribut-atribut di bawah ini memetakan kolom-kolom yang ada pada tabel database.
     * Penggunaan akses modifier 'private' bertujuan untuk melindungi integritas data
     * (Encapsulation), sehingga nilai-nilai ini tidak bisa diubah sembarangan oleh
     * kelas lain tanpa melalui metode Setter yang resmi.
     */
    private String username;      // Identitas unik pemain (Primary Key di DB)
    private int skor;             // Akumulasi total skor pemain
    private int peluruMeleset;    // Statistik jumlah peluru musuh yang dihindari
    private int sisaPeluru;       // Stok peluru terakhir yang tersimpan

    /**
     * Konstruktor standar tanpa argumen (No-Args Constructor).
     *
     * Keberadaan konstruktor kosong ini penting untuk memberikan fleksibilitas.
     * Seringkali kita perlu membuat objek UserStats terlebih dahulu (instansiasi),
     * baru kemudian mengisi datanya satu per satu menggunakan metode Setter,
     * misalnya saat memproses hasil query dari database yang kompleks.
     */
    public UserStats() {
    }

    /**
     * Konstruktor lengkap (All-Args Constructor).
     *
     * Konstruktor ini digunakan ketika seluruh data pemain sudah tersedia dan kita
     * ingin membuat objek yang "siap pakai" dalam satu langkah eksekusi.
     * Ini sangat berguna saat mengambil data lengkap dari database dan langsung
     * mengemasnya untuk dikirim ke GameViewModel.
     *
     * @param username Nama pengguna.
     * @param skor Nilai skor total.
     * @param peluruMeleset Jumlah hindaran.
     * @param sisaPeluru Sisa amunisi.
     */
    public UserStats(String username, int skor, int peluruMeleset, int sisaPeluru) {
        this.username = username;
        this.skor = skor;
        this.peluruMeleset = peluruMeleset;
        this.sisaPeluru = sisaPeluru;
    }

    // --- Metode Akses (Getter) dan Mutasi (Setter) ---

    /*
     * Bagian ini menerapkan prinsip Enkapsulasi OOP.
     * - Getter: Memberikan akses baca kepada komponen lain untuk melihat nilai data.
     * - Setter: Memberikan akses tulis kepada komponen lain untuk mengubah nilai data.
     *
     * Dengan cara ini, struktur internal data tetap terlindungi, namun tetap bisa
     * berinteraksi dengan komponen luar seperti ViewModel atau tampilan UI.
     */

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getSkor() {
        return skor;
    }

    public void setSkor(int skor) {
        this.skor = skor;
    }

    public int getPeluruMeleset() {
        return peluruMeleset;
    }

    public void setPeluruMeleset(int peluruMeleset) {
        this.peluruMeleset = peluruMeleset;
    }

    public int getSisaPeluru() {
        return sisaPeluru;
    }

    public void setSisaPeluru(int sisaPeluru) {
        this.sisaPeluru = sisaPeluru;
    }
}