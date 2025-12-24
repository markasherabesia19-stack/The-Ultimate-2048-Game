package game2048;

import javax.swing.*;
import java.awt.*;

/**
 * Main game controller that manages the overall game state and navigation between screens.
 * Handles game initialization, screen transitions, player actions, scoring, and music playback.
 * Coordinates interactions between the board model, UI screens, and game logic components.
 */
public class Game extends JFrame {
    private SplashScreen splashScreen;
    private NameInputPanel nameInputPanel;
    private GameplayScreen gameplayScreen;
    private Instructions instructions;
    private Leaderboard leaderboard;
    private Board board;
    private Expectimax expectimax; 
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
        
        // Preload instruction images in background while splash screen loads
        System.out.println("Starting background image preload...");
        Instructions.preloadImages();
        
        showSplashScreen();
    }
    
    // Helper method to properly clean up old panels before switching screens
    private void cleanupCurrentPanel() {
        Container contentPane = getContentPane();
        if (contentPane.getComponentCount() > 0) {
            Component currentPanel = contentPane.getComponent(0);
            
            // Force cleanup by calling removeNotify
            if (currentPanel instanceof JPanel) {
                ((JPanel) currentPanel).removeNotify();
            }
            
            contentPane.removeAll();
            System.out.println("Cleaned up old panel: " + currentPanel.getClass().getSimpleName());
        }
    }
    
    private void showSplashScreen() {
        cleanupCurrentPanel();
        
        splashScreen = new SplashScreen(this);
        setContentPane(splashScreen);
        revalidate();
        repaint();
        
        // Play menu music
        musicPlayer.playMenuMusic();
        
        System.out.println("Showing Splash Screen");
    }
    
    public void showInstructions() {
        cleanupCurrentPanel();
        
        instructions = new Instructions(this);
        setContentPane(instructions);
        revalidate();
        repaint();
        
        System.out.println("Showing Instructions");
    }

    public void showLeaderboard() {
        cleanupCurrentPanel();
        
        leaderboard = new Leaderboard(this);
        setContentPane(leaderboard);
        revalidate();
        repaint();
        
        System.out.println("Showing Leaderboard");
    }
    
    public void showNameInput() {
        cleanupCurrentPanel();
        
        nameInputPanel = new NameInputPanel(this);
        setContentPane(nameInputPanel);
        revalidate();
        repaint();
        
        System.out.println("Showing Name Input");
    }
    
    public void setPlayerName(String name) {
        this.playerName = name;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void startNewGame() {
        cleanupCurrentPanel();
        
        board = new Board(5);
        expectimax = new Expectimax(board);
        score = 0;
        startTime = System.currentTimeMillis();
        gameStarted = true;
        autoSuggestMode = false;
        remainingSuggestions = 0;
        
        board.addRandomTile();
        board.addRandomTile();
        
        // Reset mute state when starting new game
        musicPlayer.unmute();
        
        showGameplayScreen();
        
        // Switch to gameplay music
        musicPlayer.playGameplayMusic();
        
        System.out.println("Started New Game");
    }
    
    private void showGameplayScreen() {
        gameplayScreen = new GameplayScreen(this, board);
        setContentPane(gameplayScreen);
        revalidate();
        repaint();
        gameplayScreen.requestFocusInWindow();
        
        System.out.println("Showing Gameplay Screen");
    }
    
    public void returnToMainMenu() {
        cleanupCurrentPanel();
        
        gameStarted = false;
        board = null;
        expectimax = null;
        score = 0;
        autoSuggestMode = false;
        remainingSuggestions = 0;
        playerName = "Player";
        
        showSplashScreen();
        
        // Switch back to menu music
        musicPlayer.playMenuMusic();
        
        System.out.println("Returned to Main Menu");
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
    
    public void activateAutoSuggestMode() {
        if (!gameStarted || board == null) return;
        
        autoSuggestMode = true;
        remainingSuggestions = TOTAL_AUTO_SUGGESTIONS;
        
        if (gameplayScreen != null) {
            gameplayScreen.updateAutoSuggestion();
        }
    }
    
    public String getSuggestion() {
        if (!gameStarted || board == null) {
            return "No suggestion available";
        }
        
        expectimax = new Expectimax(board);
        
        if (autoSuggestMode) {
            var topMoves = expectimax.getTopMoves();
            
            if (topMoves.isEmpty()) {
                autoSuggestMode = false;
                return "No valid moves available!";
            }
            
            var bestMove = topMoves.get(0);
            
            StringBuilder sb = new StringBuilder();
            sb.append("SUGGESTION\n\n");
            sb.append("Move ").append(TOTAL_AUTO_SUGGESTIONS - remainingSuggestions + 1);
            sb.append(" of ").append(TOTAL_AUTO_SUGGESTIONS).append("\n\n");
            sb.append("SUGGESTED MOVE:\n");
            sb.append(" ► ").append(bestMove.directionName).append("\n\n");
            sb.append(remainingSuggestions).append(" suggestions remaining");
            
            return sb.toString();
        } else {
            return "CLICK TO ACTIVATE\n\n" +
                   "Auto-Suggest Mode\n\n" +
                   "Get 8 consecutive smart\n" +
                   "move suggestions!\n\n" +
                   "The Algo will guide you\n" +
                   "through 8 moves.\n\n" +
                   "Click to start!";
        }
    }
    
    public boolean isAutoSuggestActive() {
        return autoSuggestMode;
    }
    
    public void deactivateAutoSuggestMode() {
        autoSuggestMode = false;
        remainingSuggestions = 0;
    }
    
    private void gameOver() {
        autoSuggestMode = false;
    
        int highestTile = board.getHighestTile();
    
        System.out.println("Saving to leaderboard: " + playerName + ", Score: " + score + ", Tile: " + highestTile);
    
        int rank = LeaderboardManager.addEntry(playerName, score, highestTile);
    
        boolean madeLeaderboard = (rank > 0 && rank <= 10);
    
        if (madeLeaderboard) {
            System.out.println("Made leaderboard at rank: " + rank);
        } else {
            System.out.println("Did not make leaderboard");
        }
    
        CustomDialog.showGameOverDialog(this, playerName, score, highestTile, madeLeaderboard, rank);
    
        returnToMainMenu();
    }
    
    private void victory() {
        autoSuggestMode = false;
    
        int highestTile = board.getHighestTile();
    
        System.out.println("Victory! Saving to leaderboard: " + playerName + ", Score: " + score + ", Tile: " + highestTile);
    
        int rank = LeaderboardManager.addEntry(playerName, score, highestTile);
    
        boolean madeLeaderboard = (rank > 0 && rank <= 10);
    
        if (madeLeaderboard) {
            System.out.println("Made leaderboard at rank: " + rank);
        } else {
            System.out.println("Did not make leaderboard");
        }
    
        CustomDialog.showVictoryDialog(this, playerName, score, highestTile, madeLeaderboard, rank);
    
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
    
    public MusicPlayer getMusicPlayer() {
        return musicPlayer;
    }
}