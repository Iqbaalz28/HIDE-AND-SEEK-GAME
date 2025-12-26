package com.hideseek.model;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Kelas Helper untuk memutar efek suara (SFX).
 */
public class Sound {

    public Sound() {
    }

    /**
     * Memutar file suara satu kali.
     * @param filename Nama file beserta ekstensinya (contoh: "sfx_laser1.wav")
     */
    public void play(String filename) {
        try {
            // Mengambil file dari folder assets
            // Path relatif dimulai dari root project
            File soundFile = new File("assets/Sounds/" + filename);

            if (soundFile.exists()) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start(); // Mainkan suara
            } else {
                System.out.println("File suara tidak ditemukan: " + filename);
            }
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Format audio tidak didukung. Harap gunakan .WAV");
        } catch (IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}