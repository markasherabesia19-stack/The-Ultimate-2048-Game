package game2048;

import javax.swing.*;
import java.awt.*;

public class Game extends JFrame {
    private SplashScreen splashScreen;
    private GameplayScreen gameplayScreen;
    private Board board;
    private Expictimax ex;
    private int score;
    private long startTime;
    private boolean gameStarted;
    
    public Game() {
        setTitle("THE ULTIMATE 2048 GAME");
        setSize(1120, 630);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        
        score = 0;
        gameStarted = false;
        
        showSplashScreen();
    }
    
    private void showSplashScreen() {
        splashScreen = new SplashScreen(this);
        setContentPane(splashScreen);
        revalidate();
        repaint();
    }
    
    public void startNewGame() {
        board = new Board(5);
        ex = new Expictimax(board);
        score = 0;
        startTime = System.currentTimeMillis();
        gameStarted = true;
        
        board.addRandomTile();
        board.addRandomTile();
        
        showGameplayScreen();
    }
    
    private void showGameplayScreen() {
        gameplayScreen = new GameplayScreen(this, board);
        setContentPane(gameplayScreen);
        revalidate();
        repaint();
        gameplayScreen.requestFocusInWindow();
    }
    
    public void returnToMainMenu() {
        gameStarted = false;
        board = null;
        ex = null;
        score = 0;
        showSplashScreen();
    }
    
    public boolean makeMove(int direction) {
        if (!gameStarted) return false;
        
        boolean moved = board.move(direction);
        
        if (moved) {
            board.addRandomTile();
            updateScore();
            
            if (board.isGameOver()) {
                gameOver();
            } else if (board.hasWon()) {
                victory();
            }
        }
        
        return moved;
    }
    
    private void updateScore() {
        score = board.getScore();
        if (gameplayScreen != null) {
            gameplayScreen.updateDisplay();
        }
    }
    
    public String getSuggestion() {
        if (!gameStarted || board == null) {
            return "No suggestion available";
        }
        
        ex = new Expictimax(board);
        
        return ex.getBestMoveSequence(8);
    }
    
    private void gameOver() {
        JOptionPane.showMessageDialog(this, 
            "Game Over! Your score: " + score, 
            "Game Over", 
            JOptionPane.INFORMATION_MESSAGE);
        returnToMainMenu();
    }
    
    private void victory() {
        int choice = JOptionPane.showConfirmDialog(this,
            "You reached 2048! Continue playing?",
            "Victory!",
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.NO_OPTION) {
            returnToMainMenu();
        }
    }
    
    public int getScore() {
        return score;
    }
    
    public long getElapsedTime() {
        if (!gameStarted) return 0;
        return (System.currentTimeMillis() - startTime) / 1000;
    }
    
    public Board getBoard() {
        return board;
    }
}