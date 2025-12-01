package game2048;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class Game extends JFrame {
    private Board board;
    private GamePanel gamePanel;
    private Suggestion suggestion;
    private int score;
    private JLabel scoreLabel;
    private JLabel suggestionLabel; 
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
        setVisible(true);
        
        // Request focus after window is visible
        SwingUtilities.invokeLater(() -> gamePanel.requestFocusInWindow());
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
        gamePanel.setFocusable(true);
        gamePanel.addKeyListener(new GameKeyListener());
        add(gamePanel, BorderLayout.CENTER);
    
        // Bottom panel with instructions and suggestion
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(new Color(250, 248, 239));
    
        JLabel instructions = new JLabel("Use Arrow Keys to Play");
        instructions.setFont(new Font("Arial", Font.PLAIN, 14));
        instructions.setForeground(Constants.TEXT_DARK);
        instructions.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        suggestionLabel = new JLabel("Click 'Suggest Move' for help");
        suggestionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        suggestionLabel.setForeground(new Color(0, 123, 255));
        suggestionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        bottomPanel.add(Box.createVerticalStrut(5));
        bottomPanel.add(instructions);
        bottomPanel.add(Box.createVerticalStrut(5));
        bottomPanel.add(suggestionLabel);
        bottomPanel.add(Box.createVerticalStrut(5));
    
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void resetGame() {
        board.reset();
        score = 0;
        updateScore();
        suggestionLabel.setText("Click 'Suggest Move' for help");
        gamePanel.repaint();
        gamePanel.requestFocusInWindow();
    }
    
    private void showSuggestion() {
        String message = suggestion.getSuggestionMessage();
        suggestionLabel.setText(message.replace("\n", " "));
        gamePanel.requestFocusInWindow();
    }
    
    private void updateScore() {
        scoreLabel.setText("Score: " + score);
    }
    
    private void makeMove(Constants.Direction direction) {
        boolean moved = board.move(direction);
    
        if (moved) {
            score += board.getLastMoveScore();
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
                gamePanel.requestFocusInWindow();
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