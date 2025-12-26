# Hide and Seek: The Challenge

## Janji
Saya Iqbal Rizky Maulana dengan NIM 2408622 mengerjakan TMD (Tugas Masa Depan) dalam mata kuliah Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.

## 1. Pendahuluan

**Hide and Seek: The Challenge** adalah sebuah permainan *2D Top-Down Shooter* berbasis Java yang dikembangkan dengan paradigma Pemrograman Berorientasi Objek (OOP) murni dan arsitektur MVVM (*Model-View-ViewModel*).

Dalam permainan ini, pemain mengendalikan sebuah pesawat luar angkasa untuk bertahan hidup dari serangan alien, menghindari rintangan meteor, dan mengumpulkan skor tertinggi. Proyek ini dirancang tidak hanya sebagai permainan, tetapi juga sebagai demonstrasi implementasi arsitektur perangkat lunak yang bersih (*Clean Code*), pemisahan tanggung jawab (*Separation of Concerns*), dan manajemen data persisten menggunakan database MySQL.

### Teknologi yang Digunakan
* **Bahasa Pemrograman:** Java (JDK 17+)
* **GUI Library:** Java Swing & AWT
* **Database:** MySQL
* **Arsitektur:** MVVM (Model-View-ViewModel)
* **Tools:** IntelliJ IDEA, Git

---

## 2. Arsitektur Sistem (MVVM)

Proyek ini menerapkan pola desain **MVVM** secara ketat untuk memisahkan logika aplikasi dari antarmuka pengguna.

### Diagram Konsep
`View (UI)` <--> `ViewModel (Logic & State)` <--> `Model (Data & Behavior)`

1.  **Model (Data & Business Logic)**
    * Merepresentasikan objek nyata (Pemain, Alien, Peluru) dan aturan bisnisnya.
    * Memiliki logika internal ("Smart Model"), seperti menghitung pergerakan sendiri atau mendeteksi tabrakan, tanpa bergantung pada UI.
    * Bertanggung jawab atas akses data ke database (Repository).

2.  **View (User Interface)**
    * Hanya bertugas menampilkan visual ke layar dan menangkap input pengguna (Keyboard/Mouse).
    * Bersifat "Pasif" atau "Bodoh" (*Dumb View*); tidak memiliki logika bisnis atau logika database.
    * Mengamati perubahan pada ViewModel melalui *Interface Callback* (`GameEventListener`).

3.  **ViewModel (Connector)**
    * Bertindak sebagai "Konduktor".
    * Menerima input dari View dan memerintahkan Model untuk berubah.
    * Mengelola *Game Loop* dan sinkronisasi antara data Model dan tampilan View.

---

## 3. Desain OOP & Class Diagram

Sistem dibangun di atas prinsip OOP yang kuat seperti *Inheritance*, *Encapsulation*, dan *Polymorphism*.

### A. Package `com.hideseek.model` (Entity & Logic)

| Nama Class | Tipe | Keterangan & Tanggung Jawab |
| :--- | :--- | :--- |
| **`GameElement`** | *Abstract Class* | **Parent Class**. Menyediakan atribut dasar (x, y, width, height, image) dan logika tabrakan (`checkCollision`) untuk semua objek game. |
| **`Player`** | *Entity* | Turunan `GameElement`. Mengelola logika gerak pemain, statistik (skor/ammo), dan perhitungan vektor tembakan (*Mouse Aiming*). |
| **`Alien`** | *Entity* | Turunan `GameElement`. Mengelola pergerakan musuh dan logika *Auto-Aim* untuk menembak ke arah pemain. |
| **`Bullet`** | *Entity* | Turunan `GameElement`. Mengelola pergerakan proyektil dengan perhitungan presisi (*double coordinate*) untuk dukungan vektor 360 derajat. |
| **`Obstacle`** | *Entity* | Turunan `GameElement`. Objek statis dengan atribut `HP` (Health Point) yang bisa dihancurkan. |
| **`UserStats`** | *Data Transfer Object* | Wadah data murni untuk mentransfer informasi pemain (username, skor, dll) antara DB dan Aplikasi. |
| **`UserRepository`** | *Repository* | Menangani seluruh operasi CRUD SQL (`SELECT`, `INSERT`, `UPDATE`) agar ViewModel bersih dari sintaks database. |
| **`DB`** | *Utility* | Menangani koneksi teknis JDBC ke MySQL. |
| **`ResourceManager`** | *Utility* | Menangani pemuatan aset gambar (*Image Loading*) dari penyimpanan fisik. |
| **`Sound`** | *Utility* | Menangani pemutaran efek suara (*SFX*). |

### B. Package `com.hideseek.viewmodel` (Controller/Presenter)

| Nama Class | Keterangan & Tanggung Jawab |
| :--- | :--- |
| **`GameViewModel`** | **Game Engine**. Mengelola *Thread* utama permainan, mengatur spawn musuh, meneruskan input ke Model, dan memicu penyimpanan data saat *Game Over*. |
| **`MenuViewModel`** | Menyediakan data untuk *Leaderboard* di menu utama. |
| **`GameEventListener`** | *Interface*. Kontrak komunikasi agar ViewModel dapat mengirim sinyal `onGameUpdate` atau `onGameOver` ke View tanpa ketergantungan langsung. |

### C. Package `com.hideseek.view` (UI)

| Nama Class | Keterangan & Tanggung Jawab |
| :--- | :--- |
| **`GameWindow`** | *JFrame*. Jendela utama yang mengatur navigasi halaman (CardLayout) antara Menu dan Game. |
| **`MainMenuPanel`** | *JPanel*. Tampilan awal, papan peringkat, dan input login. |
| **`GameCanvas`** | *JPanel*. Area rendering permainan. Bertugas menggambar objek dan menangkap input Mouse/Keyboard. |

### Relasi Utama
* **Inheritance:** `Player`, `Alien`, `Bullet`, `Obstacle` **extends** `GameElement`.
* **Composition:** `GameViewModel` **memiliki** `Player`, `List<Alien>`, `UserRepository`.
* **Dependency:** `GameCanvas` **mengimplementasikan** `GameEventListener`.

---

## 4. Struktur Database

Aplikasi menggunakan database MySQL dengan nama **`hide_seek_db`**.

### Tabel: `tbenefit`
Menyimpan data statistik dan progres pemain.

| Kolom | Tipe Data | Keterangan |
| :--- | :--- | :--- |
| `username` | VARCHAR (PK) | Identitas unik pemain (Primary Key). |
| `skor` | INT | Skor tertinggi yang pernah dicapai. |
| `peluru_meleset` | INT | Statistik total peluru musuh yang berhasil dihindari. |
| `sisa_peluru` | INT | Jumlah amunisi terakhir saat permainan disimpan. |

**Catatan:**
* Aplikasi menggunakan logika `INSERT IGNORE` atau pengecekan `SELECT` sebelum `INSERT` untuk mencegah duplikasi user.
* Data di-update secara otomatis saat *Game Over*.

---

## 5. Alur Program (Flow Aplikasi)

### A. Memulai Permainan
1.  Aplikasi dijalankan melalui `Main.java`.
2.  `GameWindow` memuat `MainMenuPanel`.
3.  `MainMenuPanel` meminta `MenuViewModel` mengambil data *Highscore* dari `UserRepository`.
4.  User memasukkan username dan menekan tombol **START**.
5.  `MenuViewModel` mendaftarkan user ke DB (jika baru) atau memuat data (jika lama).
6.  `GameWindow` menukar tampilan ke `GameCanvas`.

### B. Gameplay Loop (GameViewModel)
1.  **Input:** `GameCanvas` menangkap tombol WASD (gerak) atau Klik Mouse (tembak) dan mengirimnya ke `GameViewModel`.
2.  **Update:**
    * `GameViewModel` memerintahkan `Player.move()`.
    * `GameViewModel` memerintahkan setiap `Alien.move()`.
    * `GameViewModel` memanggil `checkCollision()` pada setiap entitas.
3.  **Render:** `GameViewModel` memanggil `listener.onGameUpdate()`, memicu `GameCanvas` untuk menggambar ulang layar (*repaint*).

### C. Mekanisme Menembak (Mouse Aiming)
1.  User mengklik mouse pada koordinat (x, y).
2.  `Player` menghitung sudut (*arctangent*) antara posisi diri sendiri dengan kursor mouse.
3.  `Player` membuat objek `Bullet` dengan vektor kecepatan hasil kalkulasi trigonometri tersebut.

### D. Game Over
1.  Terdeteksi tabrakan antara `Player` dan `Alien` (atau peluru musuh).
2.  `GameViewModel` menghentikan *loop*.
3.  `GameViewModel` memanggil `UserRepository` untuk menyimpan skor terbaru ke database.
4.  `GameViewModel` mengirim sinyal `onGameOver` ke View.
5.  View menampilkan *Pop-up* skor akhir dan kembali ke Menu Utama.

---

## 6. Struktur Folder

```
E:.
├───.idea
├───assets
│   ├───Alien
│   ├───Backgrounds
│   ├───Meteors
│   ├───Player
│   └───Sounds
├───db
├───lib
└───src
    └───com
        └───hideseek
            ├───main
            ├───model
            ├───view
            └───viewmodel
```
---
---

## 7. Cara Menjalankan Aplikasi

### Prasyarat
1.  Java Development Kit (JDK) versi 8 atau lebih baru.
2.  MySQL Server (XAMPP/WAMP/MAMP).
3.  IDE (IntelliJ IDEA / NetBeans / Eclipse).

### Langkah Konfigurasi
1.  **Persiapan Database:**
    * Buka phpMyAdmin atau MySQL Client.
    * Buat database baru bernama `hide_seek_db`.
    * Jalankan query SQL berikut untuk membuat tabel:
        ```sql
        CREATE TABLE tbenefit (
            username VARCHAR(50) PRIMARY KEY,
            skor INT DEFAULT 0,
            peluru_meleset INT DEFAULT 0,
            sisa_peluru INT DEFAULT 0
        );
        ```

2.  **Konfigurasi Koneksi:**
    * Buka file `src/com/hideseek/model/DB.java`.
    * Sesuaikan `dbUrl`, `user`, dan `password` dengan konfigurasi MySQL lokal Anda.

3.  **Menjalankan Aplikasi:**
    * Buka project di IDE.
    * Jalankan file `src/com/hideseek/main/Main.java`.
    * Atau compile secara manual di CMD dengan perintah: (dapat disesuaikan dengan path direktori project disimpan)
      ```
      if not exist bin mkdir bin && javac -d bin -cp "lib\*" src\com\hideseek\main\*.java src\com\hideseek\model\*.java src\com\hideseek\view\*.java src\com\hideseek\viewmodel\*.java && java -cp "bin;lib\*" com.hideseek.main.Main
      ```

