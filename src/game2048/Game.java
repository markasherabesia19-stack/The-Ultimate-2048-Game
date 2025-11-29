package game2048;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Game extends JFrame {
    private Board board;
    private GamePanel gamePanel;
    private Suggestion suggestion;
    private int score;
    private JLabel scoreLabel;
    private JButton suggestButton;
    private JButton newGameButton;
    
    public Game() {
        setTitle("The Ultimate 2048 Game");
        setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        board = new Board(Constants.GRID_SIZE);
        suggestion = new Suggestion(board);
        score = 0;
        
        initializeUI();
        addKeyListener(new GameKeyListener());
        
        setVisible(true);
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Top panel with score and buttons
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        topPanel.setBackground(new Color(250, 248, 239));
        
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(Constants.SCORE_FONT);
        scoreLabel.setForeground(Constants.TEXT_DARK);
        
        newGameButton = new JButton("New Game");
        newGameButton.setFont(Constants.BUTTON_FONT);
        newGameButton.setFocusable(false);
        newGameButton.addActionListener(e -> resetGame());
        
        suggestButton = new JButton("Suggest Move");
        suggestButton.setFont(Constants.BUTTON_FONT);
        suggestButton.setFocusable(false);
        suggestButton.addActionListener(e -> showSuggestion());
        
        topPanel.add(scoreLabel);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(newGameButton);
        topPanel.add(suggestButton);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Game panel
        gamePanel = new GamePanel(board);
        add(gamePanel, BorderLayout.CENTER);
        
        // Instructions panel
        JPanel instructionsPanel = new JPanel();
        instructionsPanel.setBackground(new Color(250, 248, 239));
        JLabel instructions = new JLabel("Use Arrow Keys to Play");
        instructions.setFont(new Font("Arial", Font.PLAIN, 14));
        instructions.setForeground(Constants.TEXT_DARK);
        instructionsPanel.add(instructions);
        add(instructionsPanel, BorderLayout.SOUTH);
    }
    
    private void resetGame() {
        board.reset();
        score = 0;
        updateScore();
        gamePanel.repaint();
        requestFocus();
    }
    
    private void showSuggestion() {
        String suggestion = suggestionAlgorithm.getSuggestionMessage();
        JOptionPane.showMessageDialog(this, suggestion, "Move Suggestion", 
                                      JOptionPane.INFORMATION_MESSAGE);
        requestFocus();
    }
    
    private void updateScore() {
        scoreLabel.setText("Score: " + score);
    }
    
    private void makeMove(Constants.Direction direction) {
        int[][] oldGrid = board.copyGrid(board.getGrid());
        boolean moved = board.move(direction);
        
        if (moved) {
            // Calculate score increase
            int[][] newGrid = board.getGrid();
            int scoreIncrease = calculateScoreIncrease(oldGrid, newGrid);
            score += scoreIncrease;
            updateScore();
            
            gamePanel.repaint();
            
            // Check win condition
            if (board.getLargestValue() >= Constants.WIN_VALUE) {
                int option = JOptionPane.showConfirmDialog(this, 
                    "Congratulations! You reached " + Constants.WIN_VALUE + "!\nDo you want to continue?",
                    "You Win!", 
                    JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.NO_OPTION) {
                    resetGame();
                    return;
                }
            }
            
            // Check lose condition
            if (!board.canMove()) {
                JOptionPane.showMessageDialog(this, 
                    "Game Over! Your score: " + score,
                    "Game Over", 
                    JOptionPane.INFORMATION_MESSAGE);
                resetGame();
            }
        }
    }
    
    private int calculateScoreIncrease(int[][] oldGrid, int[][] newGrid) {
        int increase = 0;
        int size = board.getSize();
        
        // Sum all values in new grid
        int newSum = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                newSum += newGrid[i][j];
            }
        }
        
        // Sum all values in old grid
        int oldSum = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                oldSum += oldGrid[i][j];
            }
        }
        
        // The difference is the score increase (merged tiles)
        // Note: This is simplified - you may want a different scoring system
        return newSum - oldSum;
    }
    
    private class GameKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    makeMove(Constants.Direction.UP);
                    break;
                case KeyEvent.VK_DOWN:
                    makeMove(Constants.Direction.DOWN);
                    break;
                case KeyEvent.VK_LEFT:
                    makeMove(Constants.Direction.LEFT);
                    break;
                case KeyEvent.VK_RIGHT:
                    makeMove(Constants.Direction.RIGHT);
                    break;
            }
        }
    }
}