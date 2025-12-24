package game2048;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Leaderboard displays the top 10 player scores in the game.
 * Shows player names, scores, and highest tiles achieved with animated starry background and medal icons for top 3 players.
 */
public class Leaderboard extends JPanel {
    private Game game;
    private Rectangle backButtonBounds;
    
    // Animation variables
    private float pulseAlpha = 0f;
    private float pulseDirection = 0.02f;
    private javax.swing.Timer animationTimer;
    private int starCount = 100;
    private Star[] stars;
    
    // Leaderboard data - loaded from file
    private List<LeaderboardManager.LeaderboardEntry> entries;
    
    class Star {
        float x, y, size, speed, alpha;
        
        Star() {
            x = (float)(Math.random() * 1120);
            y = (float)(Math.random() * 630);
            size = (float)(Math.random() * 2 + 1);
            speed = (float)(Math.random() * 0.5 + 0.2);
            alpha = (float)Math.random();
        }
        
        void update() {
            alpha += speed * 0.02f;
            if (alpha > 1f) alpha = 0f;
        }
    }
    
    public Leaderboard(Game game) {
        this.game = game;
        setPreferredSize(new Dimension(1120, 630));
        setBackground(new Color(10, 10, 30));
        
        initializeStars();
        loadLeaderboardData();
        setupButtons();
        setupMouseListener();
        startAnimations();
    }
    
    private void initializeStars() {
        stars = new Star[starCount];
        for (int i = 0; i < starCount; i++) {
            stars[i] = new Star();
        }
    }
    
    private void loadLeaderboardData() {
        entries = LeaderboardManager.loadLeaderboard();
        
        if (entries.isEmpty()) {
            System.out.println("Leaderboard is empty - play some games to fill it!");
        } else {
            System.out.println("Loaded " + entries.size() + " leaderboard entries");
        }
    }
    
    private void setupButtons() {
        // Center the back button
        int buttonWidth = 200;
        int buttonHeight = 60;
        int buttonY = 540;
        int centerX = (1120 - buttonWidth) / 2;
        
        backButtonBounds = new Rectangle(centerX, buttonY, buttonWidth, buttonHeight);
    }
    
    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (backButtonBounds.contains(e.getPoint())) {
                    System.out.println("BACK button clicked!");
                    game.returnToMainMenu();
                }
            }
        });
    }
    
    private void startAnimations() {
        animationTimer = new javax.swing.Timer(30, e -> {
            pulseAlpha += pulseDirection;
            if (pulseAlpha > 0.3f) {
                pulseAlpha = 0.3f;
                pulseDirection *= -1;
            }
            if (pulseAlpha < 0f) {
                pulseAlpha = 0f;
                pulseDirection *= -1;
            }
            
            for (Star star : stars) {
                star.update();
            }
            
            repaint();
        });
        animationTimer.start();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        drawBackground(g2d);
        drawStars(g2d);
        drawTitle(g2d);
        drawLeaderboardEntries(g2d);
        drawBackButton(g2d);
    }
    
    private void drawBackground(Graphics2D g2d) {
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(10, 10, 50),
            0, 630, new Color(60, 20, 80)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 1120, 630);
    }
    
    private void drawStars(Graphics2D g2d) {
        for (Star star : stars) {
            g2d.setColor(new Color(255, 255, 255, (int)(star.alpha * 255)));
            g2d.fillOval((int)star.x, (int)star.y, (int)star.size, (int)star.size);
        }
    }
    
    private void drawTitle(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        g2d.setColor(new Color(150, 100, 255, (int)(pulseAlpha * 255)));
        
        String title = "LEADERBOARD";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (1120 - fm.stringWidth(title)) / 2;
        
        g2d.drawString(title, x - 2, 62);
        g2d.drawString(title, x + 2, 66);
        
        g2d.setColor(Color.WHITE);
        g2d.drawString(title, x, 64);
    }
    
    private void drawLeaderboardEntries(Graphics2D g2d) {
        int panelX = 160;
        int panelY = 120;
        int panelWidth = 800;
        int panelHeight = 400;
        
        // Panel background with glow
        g2d.setColor(new Color(120, 80, 220, (int)(100 + pulseAlpha * 100)));
        g2d.fillRoundRect(panelX - 12, panelY - 12, panelWidth + 24, panelHeight + 24, 25, 25);
        
        g2d.setColor(new Color(30, 20, 60, 230));
        g2d.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);
        
        // Border
        g2d.setColor(new Color(150, 120, 255, (int)(200 + pulseAlpha * 55)));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);
        
        if (entries.isEmpty()) {
            // Show "No entries yet" message
            g2d.setFont(new Font("Arial", Font.BOLD, 32));
            g2d.setColor(new Color(200, 180, 255));
            String emptyMsg = "No entries yet!";
            FontMetrics fm = g2d.getFontMetrics();
            int msgX = panelX + (panelWidth - fm.stringWidth(emptyMsg)) / 2;
            g2d.drawString(emptyMsg, msgX, panelY + 180);
            
            g2d.setFont(new Font("Arial", Font.PLAIN, 20));
            g2d.setColor(Color.WHITE);
            String playMsg = "Play some games to fill the leaderboard!";
            fm = g2d.getFontMetrics();
            msgX = panelX + (panelWidth - fm.stringWidth(playMsg)) / 2;
            g2d.drawString(playMsg, msgX, panelY + 230);
            
            return;
        }
        
        // Header
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.setColor(new Color(200, 180, 255));
        g2d.drawString("RANK", panelX + 30, panelY + 35);
        g2d.drawString("PLAYER", panelX + 130, panelY + 35);
        g2d.drawString("SCORE", panelX + 450, panelY + 35);
        g2d.drawString("HIGHEST TILE", panelX + 600, panelY + 35);
        
        // Header line
        g2d.setColor(new Color(150, 120, 255, 180));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(panelX + 20, panelY + 50, panelX + panelWidth - 20, panelY + 50);
        
        // Draw entries
        int entryY = panelY + 80;
        int lineHeight = 35;
        
        for (int i = 0; i < Math.min(10, entries.size()); i++) {
            LeaderboardManager.LeaderboardEntry entry = entries.get(i);
            
            // Rank color based on position
            Color rankColor;
            if (i == 0) {
                rankColor = new Color(255, 215, 0); // Gold
            } else if (i == 1) {
                rankColor = new Color(192, 192, 192); // Silver
            } else if (i == 2) {
                rankColor = new Color(205, 127, 50); // Bronze
            } else {
                rankColor = new Color(200, 200, 255); // Regular
            }
            
            // Highlight for top 3
            if (i < 3) {
                g2d.setColor(new Color(rankColor.getRed(), rankColor.getGreen(), rankColor.getBlue(), 30));
                g2d.fillRoundRect(panelX + 15, entryY - 22, panelWidth - 30, 30, 10, 10);
            }
            
            g2d.setFont(new Font("Arial", Font.BOLD, 18));
            
            // Draw medal icon for top 3
            if (i < 3) {
                drawMedalIcon(g2d, panelX + 30, entryY - 17, i, rankColor);
            }
            
            // Rank number
            g2d.setColor(rankColor);
            String rank = String.valueOf(i + 1);
            g2d.drawString(rank, panelX + 60, entryY);
            
            // Player name
            g2d.setColor(Color.WHITE);
            String playerName = entry.playerName;
            if (playerName.length() > 15) {
                playerName = playerName.substring(0, 15) + "...";
            }
            g2d.drawString(playerName, panelX + 130, entryY);
            
            // Score
            g2d.setColor(new Color(150, 255, 150));
            g2d.drawString(String.format("%,d", entry.score), panelX + 450, entryY);
            
            // Highest tile
            g2d.setColor(getTileColor(entry.highestTile));
            g2d.drawString(String.valueOf(entry.highestTile), panelX + 640, entryY);
            
            entryY += lineHeight;
        }
    }
    
    private void drawMedalIcon(Graphics2D g2d, int x, int y, int rank, Color color) {
        int size = 20;
        
        // Draw circle
        g2d.setColor(color);
        g2d.fillOval(x, y, size, size);
        
        // Draw darker border
        g2d.setColor(color.darker());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(x, y, size, size);
        
        // Draw star in the center
        g2d.setColor(Color.WHITE);
        int[] xPoints = new int[10];
        int[] yPoints = new int[10];
        int centerX = x + size / 2;
        int centerY = y + size / 2;
        double angle = -Math.PI / 2;
        int outerRadius = 7;
        int innerRadius = 3;
        
        for (int i = 0; i < 10; i++) {
            int radius = (i % 2 == 0) ? outerRadius : innerRadius;
            xPoints[i] = centerX + (int)(radius * Math.cos(angle));
            yPoints[i] = centerY + (int)(radius * Math.sin(angle));
            angle += Math.PI / 5;
        }
        
        g2d.fillPolygon(xPoints, yPoints, 10);
    }
    
    // Color of the tile
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
            case 2048: return new Color(255, 215, 0);
            default: return new Color(186, 85, 211);
        }
    }
    
    // For back button
    private void drawBackButton(Graphics2D g2d) {
        drawStyledButton(g2d, backButtonBounds, "◄ BACK", new Color(120, 80, 220));
    }
    
    private void drawStyledButton(Graphics2D g2d, Rectangle bounds, String text, Color baseColor) {
        g2d.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 
            (int)(80 + pulseAlpha * 150)));
        g2d.fillRoundRect(bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4, 27, 27);
        
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillRoundRect(bounds.x + 4, bounds.y + 4, bounds.width, bounds.height, 25, 25);
        
        Color darkerColor = new Color(
            Math.max(0, baseColor.getRed() - 30),
            Math.max(0, baseColor.getGreen() - 30),
            Math.max(0, baseColor.getBlue() - 30)
        );
        
        GradientPaint buttonGradient = new GradientPaint(
            bounds.x, bounds.y, baseColor,
            bounds.x, bounds.y + bounds.height, darkerColor
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
        
        g2d.setFont(new Font("Arial", Font.BOLD, 22));
        FontMetrics fm = g2d.getFontMetrics();
        int textX = bounds.x + (bounds.width - fm.stringWidth(text)) / 2;
        int textY = bounds.y + ((bounds.height - fm.getHeight()) / 2) + fm.getAscent();
        
        g2d.setColor(new Color(200, 180, 255, (int)(pulseAlpha * 200)));
        g2d.drawString(text, textX - 1, textY - 1);
        g2d.drawString(text, textX + 1, textY + 1);
        
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, textX, textY);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
    
        System.out.println("Leaderboard: Stopping animation timer");
    
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
            System.out.println("Leaderboard timer stopped");
        }
    }
}