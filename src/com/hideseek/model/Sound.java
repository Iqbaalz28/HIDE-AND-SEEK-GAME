package com.hideseek.model;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Utilitas Pemutar Suara.
 * * Kelas sederhana untuk menangani efek suara (SFX) dalam game.
 * Menggunakan library bawaan Java Sound API.
 */
public class Sound {

    public Sound() {
    }

    /**
     * Memainkan klip audio satu kali (One-shot).
     * @param filename Nama file di folder assets/Sounds.
     */
    public void play(String filename) {
        try {
            File soundFile = new File("assets/Sounds/" + filename);

            if (soundFile.exists()) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
            } else {
                System.out.println("File suara hilang: " + filename);
            }
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Format audio tidak didukung. Gunakan .WAV");
        } catch (IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}