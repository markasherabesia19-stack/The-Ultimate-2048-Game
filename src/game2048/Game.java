package game2048;

import javax.swing.*;
import java.awt.*;

public class Game extends JFrame {
    private SplashScreen splashScreen;
    private NameInputPanel nameInputPanel;
    private GameplayScreen gameplayScreen;
    private Board board;
    private ImprovedExpectimax improvedEX; 
    private int score;
    private long startTime;
    private boolean gameStarted;
    private String playerName = "Player";
    private MusicPlayer musicPlayer;
    
    // Auto-suggest mode variables
    private boolean autoSuggestMode = false;
    private int remainingSuggestions = 0;
    private static final int TOTAL_AUTO_SUGGESTIONS = 8;
    
    public Game() {
        setTitle("THE ULTIMATE 2048 GAME");
        setSize(1120, 630);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        
        score = 0;
        gameStarted = false;
        
        // Initialize music player
        musicPlayer = new MusicPlayer();
        
        showSplashScreen();
    }
    
    private void showSplashScreen() {
        splashScreen = new SplashScreen(this);
        setContentPane(splashScreen);
        revalidate();
        repaint();
        
        // Play menu music
        musicPlayer.playMenuMusic();
    }
    
    public void showInstructions() {
        Instructions instructions = new Instructions(this);
        setContentPane(instructions);
        revalidate();
        repaint();
        
        // Keep menu music playing (no change needed)
    }

    public void showLeaderboard() {
        Leaderboard leaderboard = new Leaderboard(this);
        setContentPane(leaderboard);
        revalidate();
        repaint();
        
        // Keep menu music playing (no change needed)
    }
    
    public void showNameInput() {
        nameInputPanel = new NameInputPanel(this);
        setContentPane(nameInputPanel);
        revalidate();
        repaint();
        
        // Keep menu music playing (no change needed)
    }
    
    public void setPlayerName(String name) {
        this.playerName = name;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void startNewGame() {
        board = new Board(5);
        improvedEX = new ImprovedExpectimax(board);
        score = 0;
        startTime = System.currentTimeMillis();
        gameStarted = true;
        autoSuggestMode = false;
        remainingSuggestions = 0;
        
        board.addRandomTile();
        board.addRandomTile();
        
        showGameplayScreen();
        
        // Switch to gameplay music
        musicPlayer.playGameplayMusic();
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
        improvedEX = null; 
        score = 0;
        autoSuggestMode = false;
        remainingSuggestions = 0;
        playerName = "Player";
        
        showSplashScreen();
        
        // Switch back to menu music
        musicPlayer.playMenuMusic();
    }
    
    public boolean makeMove(int direction) {
        if (!gameStarted) return false;
        
        boolean moved = board.move(direction);
        
        if (moved) {
            board.addRandomTile();
            updateScore();
            
            // If auto-suggest mode is active, decrease counter
            if (autoSuggestMode && remainingSuggestions > 0) {
                remainingSuggestions--;
                
                // If still have remaining suggestions, auto-update
                if (remainingSuggestions > 0) {
                    // Trigger auto-suggestion update in the UI
                    if (gameplayScreen != null) {
                        gameplayScreen.updateAutoSuggestion();
                    }
                } else {
                    // Auto-suggest mode completed
                    autoSuggestMode = false;
                    if (gameplayScreen != null) {
                        gameplayScreen.showCompletionMessage();
                    }
                }
            }
            
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
    
    // Activates auto-suggest mode for 8 consecutive moves
    public void activateAutoSuggestMode() {
        if (!gameStarted || board == null) return;
        
        autoSuggestMode = true;
        remainingSuggestions = TOTAL_AUTO_SUGGESTIONS;
        
        // Trigger first suggestion immediately
        if (gameplayScreen != null) {
            gameplayScreen.updateAutoSuggestion();
        }
    }
    
    // Gets current suggestion (simplified and clean)
    public String getSuggestion() {
        if (!gameStarted || board == null) {
            return "No suggestion available";
        }
        
        // Create fresh Suggestion instance with current board state
        improvedEX = new ImprovedExpectimax(board);
        
        if (autoSuggestMode) {
            // In auto-suggest mode, show simple move suggestion
            var topMoves = improvedEX.getTopMoves();
            
            if (topMoves.isEmpty()) {
                autoSuggestMode = false;
                return "No valid moves available!";
            }
            
            var bestMove = topMoves.get(0);
            
            // Simple format - Just the move number and direction
            StringBuilder sb = new StringBuilder();
            sb.append("SUGGESTION\n\n");
            sb.append("Move ").append(TOTAL_AUTO_SUGGESTIONS - remainingSuggestions + 1);
            sb.append(" of ").append(TOTAL_AUTO_SUGGESTIONS).append("\n\n");
            sb.append("SUGGESTED MOVE:\n");
            sb.append("👉 ").append(bestMove.directionName).append("\n\n");
            sb.append(remainingSuggestions).append(" suggestions remaining");
            
            return sb.toString();
        } else {
            // Normal mode - show activation message
            return "CLICK TO ACTIVATE\n\n" +
                   "Auto-Suggest Mode\n\n" +
                   "Get 8 consecutive smart\n" +
                   "move suggestions!\n\n" +
                   "The Algo will guide you\n" +
                   "through 8 moves.\n\n" +
                   "Click to start!";
        }
    }
    
    // Check if auto-suggest mode is currently active
    public boolean isAutoSuggestActive() {
        return autoSuggestMode;
    }
    
    // Manually deactivate auto-suggest mode
    public void deactivateAutoSuggestMode() {
        autoSuggestMode = false;
        remainingSuggestions = 0;
    }
    
    private void gameOver() {
        autoSuggestMode = false;
        JOptionPane.showMessageDialog(this, 
            "Game Over, " + playerName + "!\n\nYour score: " + score, 
            "Game Over", 
            JOptionPane.INFORMATION_MESSAGE);
        returnToMainMenu();
    }
    
    private void victory() {
        autoSuggestMode = false;
        
        // Show congratulations message
        JOptionPane.showMessageDialog(this,
            "-- CONGRATULATIONS " + playerName.toUpperCase() + "! --\n\n" +
            "You reached 2048!\n" +
            "Final Score: " + score + "\n\n" +
            "You are a 2048 Master!",
            "-- VICTORY! --",
            JOptionPane.INFORMATION_MESSAGE);
        
        // Return to main menu after victory
        returnToMainMenu();
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
    
    // Getter for music player
    public MusicPlayer getMusicPlayer() {
        return musicPlayer;
    }
}