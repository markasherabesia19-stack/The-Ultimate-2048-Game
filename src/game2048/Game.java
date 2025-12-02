package game2048;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Game extends JFrame {
    private Board board;
    private GamePanel gamePanel;
    private Suggestion suggestion;
    private int score;
    private JLabel scoreLabel;
    private JTextArea suggestionTextArea;
    private JButton suggestButton;
    private JButton solveButton;
    private JButton newGameButton;
    
    public Game() {
        setTitle("The Ultimate 2048 Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        board = new Board(Constants.GRID_SIZE);
        suggestion = new Suggestion(board);
        score = 0;
        
        initializeUI();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        
        SwingUtilities.invokeLater(() -> gamePanel.requestFocusInWindow());
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(20, 20));
        getContentPane().setBackground(Color.WHITE);
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Left side - Game board with score on top
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setBackground(Color.WHITE);
        
        // Score panel
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.X_AXIS));
        scorePanel.setBackground(Color.WHITE);
        scorePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        scorePanel.setMaximumSize(new Dimension(500, 50));
        
        JLabel scoreTitle = new JLabel("SCORE");
        scoreTitle.setFont(new Font("Arial", Font.BOLD, 16));
        scoreTitle.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        scoreLabel = new JLabel("0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        scorePanel.add(scoreTitle);
        scorePanel.add(Box.createHorizontalGlue());
        scorePanel.add(scoreLabel);
        
        leftPanel.add(scorePanel, BorderLayout.NORTH);
        
        // Game panel with border
        gamePanel = new GamePanel(board);
        gamePanel.setFocusable(true);
        gamePanel.addKeyListener(new GameKeyListener());
        gamePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        gamePanel.setPreferredSize(new Dimension(
            Constants.GRID_SIZE * (Constants.TILE_SIZE + Constants.TILE_MARGIN) + Constants.TILE_MARGIN,
            Constants.GRID_SIZE * (Constants.TILE_SIZE + Constants.TILE_MARGIN) + Constants.TILE_MARGIN
        ));
        
        leftPanel.add(gamePanel, BorderLayout.CENTER);
        
        add(leftPanel, BorderLayout.CENTER);
        
        // Right side - Control panel
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension(300, 0));
        
        // Suggestion text area
        suggestionTextArea = new JTextArea(8, 15);
        suggestionTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
        suggestionTextArea.setLineWrap(true);
        suggestionTextArea.setWrapStyleWord(true);
        suggestionTextArea.setEditable(false);
        suggestionTextArea.setText("SUGGESTION TEXT\n\nClick 'Suggest Move' for intelligent move recommendations.");
        suggestionTextArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JScrollPane scrollPane = new JScrollPane(suggestionTextArea);
        scrollPane.setMaximumSize(new Dimension(200, 150));
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        rightPanel.add(scrollPane);
        rightPanel.add(Box.createVerticalStrut(20));
        
        // Buttons
        suggestButton = createStyledButton("SUGGEST MOVE");
        suggestButton.addActionListener(e -> showSuggestion());
        
        solveButton = createStyledButton("SOLVE THE NEXT NUMBER");
        solveButton.addActionListener(e -> solveNextNumber());
        
        newGameButton = createStyledButton("NEW GAME");
        newGameButton.addActionListener(e -> resetGame());
        
        rightPanel.add(suggestButton);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(solveButton);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(newGameButton);
        rightPanel.add(Box.createVerticalGlue());
        
        add(rightPanel, BorderLayout.EAST);
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusable(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 40));
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(240, 240, 240));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });
        
        return button;
    }
    
    private void resetGame() {
        board.reset();
        score = 0;
        updateScore();
        suggestionTextArea.setText("SUGGESTION TEXT\n\nClick 'Suggest Move' for intelligent move recommendations.");
        gamePanel.repaint();
        gamePanel.requestFocusInWindow();
    }
    
    private void showSuggestion() {
        String message = suggestion.getDetailedSuggestion();
        suggestionTextArea.setText(message);
        gamePanel.requestFocusInWindow();
    }
    
    private void solveNextNumber() {
        Constants.Direction bestMove = suggestion.findBestMove();
        if (bestMove != null) {
            makeMove(bestMove);
            suggestionTextArea.setText("MOVE EXECUTED\n\nMoved " + bestMove + " to create optimal merge!");
        } else {
            suggestionTextArea.setText("NO VALID MOVES\n\nGame over or no moves available.");
        }
        gamePanel.requestFocusInWindow();
    }
    
    private void updateScore() {
        scoreLabel.setText(String.valueOf(score));
    }
    
    private void makeMove(Constants.Direction direction) {
        boolean moved = board.move(direction);
        
        if (moved) {
            score += board.getLastMoveScore();
            updateScore();
            gamePanel.repaint();
            
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