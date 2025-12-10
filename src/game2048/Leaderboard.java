package game2048;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.*;
import java.util.List;

public class Leaderboard extends JPanel {
    private Game game;
    private Image leaderboardImage;
    private Rectangle backButtonBounds;
    
    // Animation variables
    private float pulseAlpha = 0f;
    private float pulseDirection = 0.02f;
    private javax.swing.Timer animationTimer;
    private int starCount = 100;
    private Star[] stars;
    
    // Leaderboard data - Top 10 players
    private List<LeaderboardEntry> entries;
    
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
    
    class LeaderboardEntry {
        String playerName;
        int score;
        int highestTile;
        
        LeaderboardEntry(String playerName, int score, int highestTile) {
            this.playerName = playerName;
            this.score = score;
            this.highestTile = highestTile;
        }
    }
    
    public Leaderboard(Game game) {
        this.game = game;
        setPreferredSize(new Dimension(1120, 630));
        setBackground(new Color(10, 10, 30));
        
        loadImages();
        initializeStars();
        loadLeaderboardData();
        setupButtons();
        setupMouseListener();
        startAnimations();
    }
    
    private void loadImages() {
        try {
            leaderboardImage = ImageIO.read(new File("components/images/leaderboard.png"));
            System.out.println("Loaded leaderboard.png successfully!");
        } catch (Exception e) {
            System.out.println("Could not load leaderboard image: " + e.getMessage());
        }
    }
    
    private void initializeStars() {
        stars = new Star[starCount];
        for (int i = 0; i < starCount; i++) {
            stars[i] = new Star();
        }
    }
    
    private void loadLeaderboardData() {
        // Sample leaderboard data - In a real implementation, this would load from a file or database
        entries = new ArrayList<>();
        entries.add(new LeaderboardEntry("MASTER_2048", 45280, 2048));
        entries.add(new LeaderboardEntry("ProGamer", 38450, 1024));
        entries.add(new LeaderboardEntry("TileKing", 32100, 1024));
        entries.add(new LeaderboardEntry("Player1", 28900, 512));
        entries.add(new LeaderboardEntry("Champion", 24560, 512));
        entries.add(new LeaderboardEntry("HighScore", 21340, 512));
        entries.add(new LeaderboardEntry("GameMaster", 18920, 256));
        entries.add(new LeaderboardEntry("TopPlayer", 15680, 256));
        entries.add(new LeaderboardEntry("Rookie", 12450, 256));
        entries.add(new LeaderboardEntry("Beginner", 9870, 128));
    }
    
    private void setupButtons() {
        // Back button at bottom center
        int buttonWidth = 200;
        int buttonHeight = 60;
        int buttonX = (1120 - buttonWidth) / 2;
        int buttonY = 540;
        
        backButtonBounds = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
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
        drawLeaderboardImage(g2d);
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
    
    private void drawLeaderboardImage(Graphics2D g2d) {
        if (leaderboardImage != null) {
            // Draw the leaderboard image as background
            g2d.drawImage(leaderboardImage, 0, 0, 1120, 630, this);
        }
    }
    
    private void drawTitle(Graphics2D g2d) {
        // Only draw title if image is not loaded (fallback)
        if (leaderboardImage == null) {
            g2d.setFont(new Font("Arial", Font.BOLD, 48));
            g2d.setColor(new Color(150, 100, 255, (int)(pulseAlpha * 255)));
            
            String title = "LEADERBOARD";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (1120 - fm.stringWidth(title)) / 2;
            
            // Shadow effect
            g2d.drawString(title, x - 2, 62);
            g2d.drawString(title, x + 2, 66);
            
            g2d.setColor(Color.WHITE);
            g2d.drawString(title, x, 64);
        }
    }
    
    private void drawLeaderboardEntries(Graphics2D g2d) {
        // Draw a semi-transparent panel for the leaderboard entries
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
            LeaderboardEntry entry = entries.get(i);
            
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
            
            // Rank
            g2d.setColor(rankColor);
            String rank = (i < 3) ? getMedalEmoji(i) + " " + (i + 1) : String.valueOf(i + 1);
            g2d.drawString(rank, panelX + 30, entryY);
            
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
    
    private String getMedalEmoji(int rank) {
        switch (rank) {
            case 0: return "🥇";
            case 1: return "🥈";
            case 2: return "🥉";
            default: return "";
        }
    }
    
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
            case 2048: return new Color(255, 215, 0); // Gold for 2048
            default: return new Color(186, 85, 211);
        }
    }
    
    private void drawBackButton(Graphics2D g2d) {
        drawStyledButton(g2d, backButtonBounds, "◄ BACK", new Color(120, 80, 220));
    }
    
    private void drawStyledButton(Graphics2D g2d, Rectangle bounds, String text, Color baseColor) {
        // Glow effect
        g2d.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 
            (int)(80 + pulseAlpha * 150)));
        g2d.fillRoundRect(bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4, 27, 27);
        
        // Shadow
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillRoundRect(bounds.x + 4, bounds.y + 4, bounds.width, bounds.height, 25, 25);
        
        // Button gradient
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
        
        // Shine effect
        GradientPaint shine = new GradientPaint(
            bounds.x, bounds.y, new Color(255, 255, 255, 60),
            bounds.x, bounds.y + bounds.height / 2, new Color(255, 255, 255, 0)
        );
        g2d.setPaint(shine);
        g2d.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height / 2, 25, 25);
        
        // Border
        g2d.setColor(new Color(180, 150, 255, (int)(200 + pulseAlpha * 55)));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 25, 25);
        
        // Text
        g2d.setFont(new Font("Arial", Font.BOLD, 22));
        FontMetrics fm = g2d.getFontMetrics();
        int textX = bounds.x + (bounds.width - fm.stringWidth(text)) / 2;
        int textY = bounds.y + ((bounds.height - fm.getHeight()) / 2) + fm.getAscent();
        
        // Text shadow
        g2d.setColor(new Color(200, 180, 255, (int)(pulseAlpha * 200)));
        g2d.drawString(text, textX - 1, textY - 1);
        g2d.drawString(text, textX + 1, textY + 1);
        
        // Main text
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, textX, textY);
    }
}