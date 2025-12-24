package game2048;

import javax.swing.*;

/**
 * Main entry point for The Ultimate 2048 Game.
 * Initializes and launches the game application.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Game game = new Game();
            game.setVisible(true);
        });
    }
}