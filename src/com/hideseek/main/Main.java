package com.hideseek.main;

import com.hideseek.view.GameWindow;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Menjalankan GUI di Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new GameWindow();
        });
    }
}