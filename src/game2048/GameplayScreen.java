package game2048;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.imageio.ImageIO;
import java.io.File;

public class GameplayScreen extends JPanel {
    private Game game;
    private Board board;
    private Timer repaintTimer;
    private Image backgroundImage;
    private Image suggestionBoxImage;
    private Image suggestionButtonImage;
    private Rectangle suggestionButtonBounds;
    private String suggestionText = "Press for suggestion";
    
    private static final int BOARD_X = 50;
    private static final int BOARD_Y = 100;
    private static final int BOARD_SIZE = 500;
    
    public GameplayScreen(Game game, Board board) {
        this.game = game;
        this.board = board;
        
        setPreferredSize(new Dimension(1120, 630));
        setBackground(new Color(10, 10, 30));
        setFocusable(true);
        
        loadImages();
        setupSuggestionButton();
        setupKeyListener();
        setupMouseListener();
        
        repaintTimer = new Timer(1000, e -> repaint());
        repaintTimer.start();
    }
    
    private void loadImages() {
        try {
            backgroundImage = ImageIO.read(new File("components/images/Back.png"));
            suggestionBoxImage = ImageIO.read(new File("components/images/suggestiontextbox.png"));
            suggestionButtonImage = ImageIO.read(new File("components/images/button2.png"));
        } catch (Exception e) {
            System.out.println("Could not load images: " + e.getMessage());
        }
    }
    
    private void setupSuggestionButton() {
        suggestionButtonBounds = new Rectangle(650, 400, 400, 60);
    }
    
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
                }
                
                if (moved) {
                    repaint();
                }
            }
        });
    }
    
    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (suggestionButtonBounds.contains(e.getPoint())) {
                    suggestionText = game.getSuggestion();
                    repaint();
                }
            }
        });
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
        drawHeader(g2d);
        drawBoard(g2d);
        drawSidePanel(g2d);
    }
    
    private void drawBackground(Graphics2D g2d) {
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, 1120, 630, null);
        } else {
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(10, 10, 50),
                0, 630, new Color(60, 20, 80)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, 1120, 630);
        }
    }
    
    private void drawHeader(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.BOLD, 28));
        g2d.setColor(Color.WHITE);
        g2d.drawString("THE ULTIMATE 2048 GAME", 350, 40);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("SCORE: " + game.getScore(), 50, 80);
        
        long seconds = game.getElapsedTime();
        long minutes = seconds / 60;
        seconds = seconds % 60;
        String timeStr = String.format("TIME: %02d:%02d", minutes, seconds);
        g2d.drawString(timeStr, 900, 80);
    }
    
    private void drawBoard(Graphics2D g2d) {
        int cellSize = BOARD_SIZE / board.getSize();
        
        g2d.setColor(new Color(30, 20, 60, 150));
        g2d.fillRoundRect(BOARD_X - 10, BOARD_Y - 10, BOARD_SIZE + 20, BOARD_SIZE + 20, 15, 15);
        
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                int x = BOARD_X + col * cellSize;
                int y = BOARD_Y + row * cellSize;
                
                g2d.setColor(new Color(40, 30, 70, 100));
                g2d.fillRoundRect(x + 2, y + 2, cellSize - 4, cellSize - 4, 8, 8);
                
                g2d.setColor(new Color(80, 60, 120, 150));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(x + 2, y + 2, cellSize - 4, cellSize - 4, 8, 8);
                
                Tile tile = board.getTile(row, col);
                if (tile != null) {
                    drawTile(g2d, tile, x + 2, y + 2, cellSize - 4);
                }
            }
        }
    }
    
    private void drawTile(Graphics2D g2d, Tile tile, int x, int y, int size) {
        Color bgColor = getTileColor(tile.getValue());
        g2d.setColor(bgColor);
        g2d.fillRoundRect(x, y, size, size, 8, 8);
        
        g2d.setColor(new Color(150, 130, 200, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x, y, size, size, 8, 8);
        
        g2d.setColor(getTileTextColor(tile.getValue()));
        String text = String.valueOf(tile.getValue());
        Font font = new Font("Arial", Font.BOLD, size / 3);
        g2d.setFont(font);
        
        FontMetrics fm = g2d.getFontMetrics();
        int textX = x + (size - fm.stringWidth(text)) / 2;
        int textY = y + ((size - fm.getHeight()) / 2) + fm.getAscent();
        
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.drawString(text, textX + 2, textY + 2);
        
        g2d.setColor(getTileTextColor(tile.getValue()));
        g2d.drawString(text, textX, textY);
    }
    
    private Color getTileColor(int value) {
        switch (value) {
            case 2: return new Color(60, 40, 120);
            case 4: return new Color(70, 50, 140);
            case 8: return new Color(80, 60, 160);
            case 16: return new Color(90, 70, 180);
            case 32: return new Color(100, 80, 200);
            case 64: return new Color(110, 90, 220);
            case 128: return new Color(120, 100, 240);
            case 256: return new Color(130, 110, 255);
            case 512: return new Color(140, 90, 230);
            case 1024: return new Color(150, 80, 220);
            case 2048: return new Color(160, 70, 210);
            default: return new Color(170, 60, 200);
        }
    }
    
    private Color getTileTextColor(int value) {
        if (value <= 4) {
            return new Color(230, 220, 255);
        }
        return new Color(255, 250, 200);
    }
    
    private void drawSidePanel(Graphics2D g2d) {
        if (suggestionBoxImage != null) {
            g2d.drawImage(suggestionBoxImage, 600, 150, 450, 200, null);
        } else {
            g2d.setColor(new Color(60, 40, 100, 200));
            g2d.fillRoundRect(600, 150, 450, 200, 20, 20);
            
            g2d.setColor(new Color(100, 80, 150));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(600, 150, 450, 200, 20, 20);
        }
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("SUGGESTION", 750, 190);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 18));
        drawWrappedText(g2d, suggestionText, 620, 230, 410, 20);
        
        if (suggestionButtonImage != null) {
            g2d.drawImage(suggestionButtonImage, suggestionButtonBounds.x, suggestionButtonBounds.y,
                         suggestionButtonBounds.width, suggestionButtonBounds.height, null);
        } else {
            g2d.setColor(new Color(80, 60, 160, 200));
            g2d.fillRoundRect(suggestionButtonBounds.x, suggestionButtonBounds.y,
                             suggestionButtonBounds.width, suggestionButtonBounds.height, 20, 20);
            
            g2d.setColor(new Color(120, 100, 200));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(suggestionButtonBounds.x, suggestionButtonBounds.y,
                             suggestionButtonBounds.width, suggestionButtonBounds.height, 20, 20);
            
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            String btnText = "GET SUGGESTION";
            FontMetrics fm = g2d.getFontMetrics();
            int textX = suggestionButtonBounds.x + (suggestionButtonBounds.width - fm.stringWidth(btnText)) / 2;
            int textY = suggestionButtonBounds.y + ((suggestionButtonBounds.height - fm.getHeight()) / 2) + fm.getAscent();
            g2d.drawString(btnText, textX, textY);
        }
    }
    
    private void drawWrappedText(Graphics2D g2d, String text, int x, int y, int maxWidth, int lineHeight) {
        FontMetrics fm = g2d.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int currentY = y;
        
        for (String word : words) {
            String testLine = line.length() == 0 ? word : line + " " + word;
            int testWidth = fm.stringWidth(testLine);
            
            if (testWidth > maxWidth && line.length() > 0) {
                g2d.drawString(line.toString(), x, currentY);
                currentY += lineHeight;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(testLine);
            }
        }
        
        if (line.length() > 0) {
            g2d.drawString(line.toString(), x, currentY);
        }
    }
}