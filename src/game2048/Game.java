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
    private JTextArea suggestionTextArea;
    private JButton suggestButton;
    private JButton solveButton;
    private JButton newGameButton;
    private Image backgroundImage;
    
    public Game() {
        setTitle("The Ultimate 2048 Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);
        
        board = new Board(Constants.GRID_SIZE);
        suggestion = new Suggestion(board);
        score = 0;
        
        // Load background image
        try {
            ImageIcon bgIcon = new ImageIcon("components/images/background.png");
            backgroundImage = bgIcon.getImage().getScaledInstance(1280, 720, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            System.out.println("Could not load background image: " + e.getMessage());
            backgroundImage = null;
        }
        
        initializeUI();
        setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setVisible(true);
        
        SwingUtilities.invokeLater(() -> gamePanel.requestFocusInWindow());
    }
    
    private void initializeUI() {
        // Main panel with background image
        JPanel mainPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        mainPanel.setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        
        // Score panel - positioned at top
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.X_AXIS));
        scorePanel.setBackground(new Color(255, 255, 255, 150)); // Semi-transparent white
        scorePanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        scorePanel.setBounds(Constants.BOARD_X_OFFSET, 50, Constants.BOARD_WIDTH, 80);
        
        JLabel scoreTitle = new JLabel("SCORE");
        scoreTitle.setFont(new Font("Arial", Font.BOLD, 28));
        scoreTitle.setForeground(Color.BLACK);
        scoreTitle.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        scoreLabel = new JLabel("0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 42));
        scoreLabel.setForeground(Color.BLACK);
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        scorePanel.add(scoreTitle);
        scorePanel.add(Box.createHorizontalGlue());
        scorePanel.add(scoreLabel);
        
        mainPanel.add(scorePanel);
        
        // Game panel
        gamePanel = new GamePanel(board);
        gamePanel.setFocusable(true);
        gamePanel.addKeyListener(new GameKeyListener());
        gamePanel.setBounds(Constants.BOARD_X_OFFSET, Constants.BOARD_Y_OFFSET, 
                           Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT);
        gamePanel.setOpaque(false); // Make transparent to show background
        
        mainPanel.add(gamePanel);
        
        // Right side control panel
        int rightPanelX = Constants.BOARD_X_OFFSET + Constants.BOARD_WIDTH + 80;
        int rightPanelWidth = 400;
        
        // Suggestion text area
        suggestionTextArea = new JTextArea(10, 20);
        suggestionTextArea.setFont(new Font("Arial", Font.PLAIN, 16));
        suggestionTextArea.setLineWrap(true);
        suggestionTextArea.setWrapStyleWord(true);
        suggestionTextArea.setEditable(false);
        suggestionTextArea.setText("SUGGESTION TEXT\n\nClick 'Suggest Move' for intelligent move recommendations.");
        suggestionTextArea.setBackground(new Color(255, 255, 255, 150));
        suggestionTextArea.setForeground(Color.BLACK);
        suggestionTextArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 3),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JScrollPane scrollPane = new JScrollPane(suggestionTextArea);
        scrollPane.setBounds(rightPanelX, Constants.BOARD_Y_OFFSET, rightPanelWidth, 250);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        mainPanel.add(scrollPane);
        
        // Buttons
        int buttonY = Constants.BOARD_Y_OFFSET + 300;
        int buttonSpacing = 80;
        
        suggestButton = createStyledButton("SUGGEST MOVE");
        suggestButton.setBounds(rightPanelX, buttonY, rightPanelWidth, 60);
        suggestButton.addActionListener(e -> showSuggestion());
        mainPanel.add(suggestButton);
        
        solveButton = createStyledButton("SOLVE THE NEXT NUMBER");
        solveButton.setBounds(rightPanelX, buttonY + buttonSpacing, rightPanelWidth, 60);
        solveButton.addActionListener(e -> solveNextNumber());
        mainPanel.add(solveButton);
        
        newGameButton = createStyledButton("NEW GAME");
        newGameButton.setBounds(rightPanelX, buttonY + buttonSpacing * 2, rightPanelWidth, 60);
        newGameButton.addActionListener(e -> resetGame());
        mainPanel.add(newGameButton);
        
        setContentPane(mainPanel);
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setFocusable(false);
        button.setBackground(new Color(255, 255, 255, 150));
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 255, 255, 220));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 255, 255, 150));
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
                case KeyEvent.VK_ESCAPE:
                    System.exit(0);
                    break;
            }
        }
    }
}