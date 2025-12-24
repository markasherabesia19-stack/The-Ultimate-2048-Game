package game2048;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.imageio.ImageIO;
import java.io.File;

/**
 * Main gameplay screen that displays the 2048 game board and controls during active play.
 * Handles rendering of the game board, tiles, animations, user input, and the AI suggestion panel.
 * Manages particle effects, button interactions, and real-time display updates.
 */
public class GameplayScreen extends JPanel {
    private Game game;
    private Board board;
    private Timer repaintTimer;
    private Image backgroundImage;
    private Rectangle suggestionButtonBounds;
    private Rectangle newGameButtonBounds;
    private Rectangle muteButtonBounds;
    private Rectangle quitButtonBounds;
    private String suggestionText = "Click to activate Auto-Suggest";
    
    // Animation variables
    private float pulseAlpha = 0f;
    private float pulseDirection = 0.02f;
    private int particleCount = 50;
    private Particle[] particles;
    
    // Board positioning constants
    private static final int BOARD_X = 60;
    private static final int BOARD_Y = 99;
    private static final int BOARD_SIZE = 478;
    
    class Particle {
        float x, y, size, speedY, alpha, maxAlpha;
        Color color;
        
        Particle() {
            reset();
        }
        
        void reset() {
            x = (float)(Math.random() * 1120);
            y = 630 + (float)(Math.random() * 100);
            size = (float)(Math.random() * 3 + 1);
            speedY = (float)(Math.random() * 0.5 + 0.3);
            maxAlpha = (float)(Math.random() * 0.5 + 0.3);
            alpha = maxAlpha;
            int colorChoice = (int)(Math.random() * 3);
            if (colorChoice == 0) color = new Color(150, 100, 255);
            else if (colorChoice == 1) color = new Color(100, 200, 255);
            else color = new Color(255, 150, 200);
        }
        
        void update() {
            y -= speedY;
            if (y < -10) reset();
        }
    }
    
    public GameplayScreen(Game game, Board board) {
        this.game = game;
        this.board = board;
        
        setPreferredSize(new Dimension(1120, 630));
        setBackground(new Color(10, 10, 30));
        setFocusable(true);
        
        loadImages();
        setupButtons();
        setupKeyListener();
        setupMouseListener();
        initializeParticles();
        
        // Start animation timer for continuous visual updates
        repaintTimer = new Timer(16, e -> {
            updateAnimations();
            repaint();
        });
        repaintTimer.start();
    }
    
    private void loadImages() {
        try {
            backgroundImage = ImageIO.read(new File("components/images/background.png"));
        } catch (Exception e) {
            System.out.println("Could not load background image: " + e.getMessage());
        }
    }
    
    private void initializeParticles() {
        particles = new Particle[particleCount];
        for (int i = 0; i < particleCount; i++) {
            particles[i] = new Particle();
            particles[i].y = (float)(Math.random() * 630);
        }
    }
    
    // Update all animation effects, the pulsing and floating particles
    private void updateAnimations() {
        pulseAlpha += pulseDirection;
        if (pulseAlpha > 0.3f || pulseAlpha < 0f) {
            pulseDirection *= -1;
        }
        
        for (Particle p : particles) {
            p.update();
        }
    }
    
    private void setupButtons() {
        suggestionButtonBounds = new Rectangle(615, 420, 380, 60);
        newGameButtonBounds = new Rectangle(615, 500, 380, 60);
        muteButtonBounds = new Rectangle(980, 20, 120, 50);
        quitButtonBounds = new Rectangle(850, 20, 120, 50);
    }
    
    // Handle arrow key input for tile movement
    private void setupKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                boolean moved = false;
                
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        moved = game.makeMove(Board.UP);
                        break;
                    case KeyEvent.VK_DOWN:
                        moved = game.makeMove(Board.DOWN);
                        break;
                    case KeyEvent.VK_LEFT:
                        moved = game.makeMove(Board.LEFT);
                        break;
                    case KeyEvent.VK_RIGHT:
                        moved = game.makeMove(Board.RIGHT);
                        break;
                    case KeyEvent.VK_ESCAPE:
                        // ESC key cancels auto-suggest mode
                        if (game.isAutoSuggestActive()) {
                            game.deactivateAutoSuggestMode();
                            suggestionText = "Auto-suggest cancelled.\nClick to start again.";
                            repaint();
                        }
                        break;
                    case KeyEvent.VK_M:
                        // M key toggles mute
                        game.getMusicPlayer().toggleMute();
                        repaint();
                        break;
                }
                
                if (moved) {
                    repaint();
                }
            }
        });
    }
    
    // Handle button clicks for suggestions, new game, mute and quit
    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (suggestionButtonBounds.contains(e.getPoint())) {
                    handleSuggestionClick();
                } else if (newGameButtonBounds.contains(e.getPoint())) {
                    boolean confirmed = CustomDialog.showConfirmDialog(
                        (JFrame) SwingUtilities.getWindowAncestor(GameplayScreen.this),
                        "New Game",
                        "Start a new game? Current progress will be lost."
                    );
                    if (confirmed) {
                        game.startNewGame();
                        suggestionText = "Click to activate Auto-Suggest";
                        repaint();
                    }
                } else if (muteButtonBounds.contains(e.getPoint())) {
                    game.getMusicPlayer().toggleMute();
                    repaint();
                } else if (quitButtonBounds.contains(e.getPoint())) {
                    boolean confirmed = CustomDialog.showConfirmDialog(
                        (JFrame) SwingUtilities.getWindowAncestor(GameplayScreen.this),
                        "Quit Game",
                        "Are you sure you want to quit? Current progress will be lost."
                    );
                    if (confirmed) {
                        System.exit(0);
                    }
                }
            }
        });
    }
    
    // Activate auto-suggest mode and compute suggestions in background thread
    private void handleSuggestionClick() {
        if (!game.isAutoSuggestActive()) {
            suggestionText = "Activating Suggestion...";
            repaint();
            
            // Use background thread to avoid freezing UI during AI computation
            new Thread(() -> {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ex) {}
                
                game.activateAutoSuggestMode();
                
                SwingUtilities.invokeLater(() -> {
                    updateAutoSuggestion();
                });
            }).start();
        } else {
            updateAutoSuggestion();
        }
    }
    
    // Update the suggestion panel with new AI computed move
    public void updateAutoSuggestion() {
        new Thread(() -> {
            String suggestion = game.getSuggestion();
            
            SwingUtilities.invokeLater(() -> {
                suggestionText = suggestion;
                repaint();
            });
        }).start();
    }
    
    public void showCompletionMessage() {
        suggestionText = "SUGGESTION COMPLETE!\n\n" +
                        "All 8 suggestions used.\n\n" +
                        "Great job!\n\n" +
                        "Click to start again.";
        repaint();
    }
    
    public void updateDisplay() {
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        drawBackground(g2d);
        drawParticles(g2d);
        drawHeader(g2d);
        drawBoard(g2d);
        drawSidePanel(g2d);
        drawSuggestionButton(g2d);
        drawNewGameButton(g2d);
        drawMuteButton(g2d);
        drawQuitButton(g2d);
    }
    
    private void drawParticles(Graphics2D g2d) {
        for (Particle p : particles) {
            g2d.setColor(new Color(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), (int)(p.alpha * 255)));
            g2d.fillOval((int)p.x, (int)p.y, (int)p.size, (int)p.size);
        }
    }
    
    private void drawBackground(Graphics2D g2d) {
        g2d.setColor(new Color(10, 10, 30));
        g2d.fillRect(0, 0, 1120, 630);
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(10, 10, 50),
                0, 630, new Color(60, 20, 80)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, 1120, 630);
        }
    }
    
    // Draw the score display with pulsing glow effect
    private void drawHeader(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        
        // Draw glow effect around text
        g2d.setColor(new Color(150, 100, 255, (int)(pulseAlpha * 255)));
        for (int i = 1; i <= 3; i++) {
            String scoreText = "SCORE: " + game.getScore();
            FontMetrics fm = g2d.getFontMetrics();
            int scoreX = (1120 - fm.stringWidth(scoreText)) / 2;
            g2d.drawString(scoreText, scoreX - i, 70 - i);
            g2d.drawString(scoreText, scoreX + i, 70 + i);
        }
        
        g2d.setColor(Color.WHITE);
        String scoreText = "SCORE: " + game.getScore();
        FontMetrics fm = g2d.getFontMetrics();
        int scoreX = (1120 - fm.stringWidth(scoreText)) / 2;
        g2d.drawString(scoreText, scoreX, 70);
    }
    
    // Draw the main game board grid and all tiles
    private void drawBoard(Graphics2D g2d) {
        int cellSize = BOARD_SIZE / board.getSize();
        
        // Draw board background
        g2d.setColor(new Color(30, 20, 60, 180));
        g2d.fillRoundRect(BOARD_X - 10, BOARD_Y - 10, BOARD_SIZE + 20, BOARD_SIZE + 20, 15, 15);
        
        // Draw each cell in the grid
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                int x = BOARD_X + col * cellSize;
                int y = BOARD_Y + row * cellSize;
                
                // Draw empty cell background
                g2d.setColor(new Color(40, 30, 70, 120));
                g2d.fillRoundRect(x + 3, y + 3, cellSize - 6, cellSize - 6, 10, 10);
                
                g2d.setColor(new Color(80, 60, 120, 180));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(x + 3, y + 3, cellSize - 6, cellSize - 6, 10, 10);
                
                // Draw tile if present
                Tile tile = board.getTile(row, col);
                if (tile != null) {
                    drawTile(g2d, tile, x + 3, y + 3, cellSize - 6);
                }
            }
        }
    }
    
    // Draw individual tile with value-based colors and text
    private void drawTile(Graphics2D g2d, Tile tile, int x, int y, int size) {
        Color bgColor = getTileColor(tile.getValue());
        g2d.setColor(bgColor);
        g2d.fillRoundRect(x, y, size, size, 10, 10);
        
        g2d.setColor(new Color(150, 130, 200, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x, y, size, size, 10, 10);
        
        g2d.setColor(getTileTextColor(tile.getValue()));
        String text = String.valueOf(tile.getValue());
        Font font = new Font("Arial", Font.BOLD, size / 3);
        g2d.setFont(font);
        
        FontMetrics fm = g2d.getFontMetrics();
        int textX = x + (size - fm.stringWidth(text)) / 2;
        int textY = y + ((size - fm.getHeight()) / 2) + fm.getAscent();
        
        // Draw text shadow for depth
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.drawString(text, textX + 2, textY + 2);
        
        g2d.setColor(getTileTextColor(tile.getValue()));
        g2d.drawString(text, textX, textY);
    }
    
    // Returns color based on tile value 
    private Color getTileColor(int value) {
        switch (value) {
            case 2: return new Color(70, 130, 220);
            case 4: return new Color(65, 105, 225);
            case 8: return new Color(50, 80, 200);
            case 16: return new Color(40, 60, 180);
            case 32: return new Color(35, 45, 160);
            case 64: return new Color(60, 40, 150);
            case 128: return new Color(70, 50, 160);
            case 256: return new Color(80, 40, 170);
            case 512: return new Color(90, 50, 180);
            case 1024: return new Color(110, 50, 190);
            case 2048: return new Color(128, 0, 128);
            default: return new Color(186, 85, 211);
        }
    }
    
    private Color getTileTextColor(int value) {
        if (value <= 4) {
            return new Color(230, 220, 255);
        }
        return new Color(255, 250, 200);
    }
    
    // Draw the suggestion panel with animated glow when active
    private void drawSidePanel(Graphics2D g2d) {
        // Glow intensity increases when auto-suggest is active
        int glowIntensity = game.isAutoSuggestActive() ? 150 : 50;
        g2d.setColor(new Color(120, 80, 220, (int)(glowIntensity + pulseAlpha * 100)));
        g2d.fillRoundRect(563, 97, 484, 310, 22, 22);
        
        g2d.setColor(new Color(60, 40, 100, 220));
        g2d.fillRoundRect(565, 99, 480, 306, 20, 20);
        
        // Border color changes when auto-suggest is active
        Color borderColor = game.isAutoSuggestActive() ? 
            new Color(100, 255, 100, (int)(200 + pulseAlpha * 55)) :
            new Color(150, 120, 255, (int)(150 + pulseAlpha * 100));
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(565, 99, 480, 306, 20, 20);
        
        // Panel title
        String title = "SUGGESTION";
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = 565 + (480 - fm.stringWidth(title)) / 2;
        
        g2d.setColor(new Color(150, 100, 255, (int)(pulseAlpha * 200)));
        g2d.drawString(title, titleX - 1, 139);
        g2d.drawString(title, titleX + 1, 141);
        
        g2d.setColor(Color.WHITE);
        g2d.drawString(title, titleX, 140);
        
        // Clear the content area before drawing new text
        g2d.setColor(new Color(60, 40, 100, 220));
        g2d.fillRect(585, 160, 440, 230);
        
        // Draw the suggestion text
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(Color.WHITE);
        drawWrappedText(g2d, suggestionText, 585, 180, 440, 22);
    }
    
    private void drawSuggestionButton(Graphics2D g2d) {
        String buttonText = game.isAutoSuggestActive() ? 
            "REFRESH" : "GET SUGGESTION";
        
        drawStyledButton(g2d, suggestionButtonBounds, buttonText, 
            game.isAutoSuggestActive());
    }
    
    private void drawNewGameButton(Graphics2D g2d) {
        drawStyledButton(g2d, newGameButtonBounds, "NEW GAME", false);
    }
    
    private void drawMuteButton(Graphics2D g2d) {
        boolean isMuted = game.getMusicPlayer().isMuted();
        String buttonText = isMuted ? "MUTED" : "MUSIC";
        Color buttonColor = isMuted ? new Color(150, 50, 50) : new Color(70, 150, 70);
        
        drawTopButton(g2d, muteButtonBounds, buttonText, buttonColor);
    }
    
    private void drawQuitButton(Graphics2D g2d) {
        String buttonText = "QUIT";
        Color buttonColor = new Color(200, 50, 50);
        
        drawTopButton(g2d, quitButtonBounds, buttonText, buttonColor);
    }
    
    // Draw top row buttons with custom styling
    private void drawTopButton(Graphics2D g2d, Rectangle bounds, String text, Color buttonColor) {
        int glowAlpha = (int)(80 + pulseAlpha * 150);
        g2d.setColor(new Color(buttonColor.getRed(), buttonColor.getGreen(), buttonColor.getBlue(), glowAlpha));
        g2d.fillRoundRect(bounds.x - 2, bounds.y - 2, 
            bounds.width + 4, bounds.height + 4, 22, 22);
        
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillRoundRect(bounds.x + 3, bounds.y + 3, 
            bounds.width, bounds.height, 20, 20);
        
        Color darkerColor = new Color(
            Math.max(0, buttonColor.getRed() - 30),
            Math.max(0, buttonColor.getGreen() - 30),
            Math.max(0, buttonColor.getBlue() - 30)
        );
        
        GradientPaint buttonGradient = new GradientPaint(
            bounds.x, bounds.y, buttonColor,
            bounds.x, bounds.y + bounds.height, darkerColor
        );
        g2d.setPaint(buttonGradient);
        g2d.fillRoundRect(bounds.x, bounds.y, 
            bounds.width, bounds.height, 20, 20);
        
        GradientPaint shine = new GradientPaint(
            bounds.x, bounds.y, new Color(255, 255, 255, 60),
            bounds.x, bounds.y + bounds.height / 2, 
            new Color(255, 255, 255, 0)
        );
        g2d.setPaint(shine);
        g2d.fillRoundRect(bounds.x, bounds.y, 
            bounds.width, bounds.height / 2, 20, 20);
        
        g2d.setColor(new Color(180, 150, 255, (int)(200 + pulseAlpha * 55)));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(bounds.x, bounds.y, 
            bounds.width, bounds.height, 20, 20);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g2d.getFontMetrics();
        int textX = bounds.x + (bounds.width - fm.stringWidth(text)) / 2;
        int textY = bounds.y + ((bounds.height - fm.getHeight()) / 2) + fm.getAscent();
        
        g2d.setColor(new Color(200, 180, 255, (int)(pulseAlpha * 200)));
        g2d.drawString(text, textX - 1, textY - 1);
        g2d.drawString(text, textX + 1, textY + 1);
        
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, textX, textY);
    }
    
    // Draw styled button with gradient, glow and shine effects
    private void drawStyledButton(Graphics2D g2d, Rectangle bounds, String text, boolean isActive) {
        int glowAlpha = isActive ? (int)(150 + pulseAlpha * 105) : (int)(80 + pulseAlpha * 150);
        g2d.setColor(new Color(150, 100, 255, glowAlpha));
        g2d.fillRoundRect(bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4, 27, 27);
        
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillRoundRect(bounds.x + 4, bounds.y + 4, bounds.width, bounds.height, 25, 25);
        
        Color topColor = isActive ? new Color(100, 200, 100) : new Color(120, 80, 220);
        Color bottomColor = isActive ? new Color(60, 150, 60) : new Color(80, 60, 180);
        
        GradientPaint buttonGradient = new GradientPaint(
            bounds.x, bounds.y, topColor,
            bounds.x, bounds.y + bounds.height, bottomColor
        );
        g2d.setPaint(buttonGradient);
        g2d.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 25, 25);
        
        GradientPaint shine = new GradientPaint(
            bounds.x, bounds.y, new Color(255, 255, 255, 60),
            bounds.x, bounds.y + bounds.height / 2, new Color(255, 255, 255, 0)
        );
        g2d.setPaint(shine);
        g2d.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height / 2, 25, 25);
        
        g2d.setColor(new Color(180, 150, 255, (int)(200 + pulseAlpha * 55)));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 25, 25);
        
        int fontSize = text.length() > 12 ? 20 : 24;
        g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
        FontMetrics fm = g2d.getFontMetrics();
        int textX = bounds.x + (bounds.width - fm.stringWidth(text)) / 2;
        int textY = bounds.y + ((bounds.height - fm.getHeight()) / 2) + fm.getAscent();
        
        g2d.setColor(new Color(200, 180, 255, (int)(pulseAlpha * 200)));
        g2d.drawString(text, textX - 1, textY - 1);
        g2d.drawString(text, textX + 1, textY + 1);
        
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, textX, textY);
    }
    
    private void drawWrappedText(Graphics2D g2d, String text, int x, int y, int maxWidth, int lineHeight) {
        String[] lines = text.split("\n");
        int currentY = y;
        
        for (String line : lines) {
            g2d.drawString(line, x, currentY);
            currentY += lineHeight;
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        // This method is called when the panel is removed from the screen.
        // We stop the timer to prevent memory leaks
        
        System.out.println("GameplayScreen: Stopping animation timer to prevent memory leak");
        
        if (repaintTimer != null && repaintTimer.isRunning()) {
            repaintTimer.stop();
            System.out.println("Animation timer stopped successfully");
        }
    }
}