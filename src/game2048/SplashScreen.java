package game2048;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.imageio.ImageIO;
import java.io.File;

public class SplashScreen extends JPanel {
    private Game game;
    private Image splashImage;
    private float titleAlpha = 0f;
    private float buttonAlpha = 0f;
    private Timer animationTimer;
    private int starCount = 100;
    private Star[] stars;
    
    private Rectangle newGameButtonBounds;
    private Rectangle howToPlayButtonBounds;
    private Rectangle leaderboardButtonBounds;
    
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
    
    public SplashScreen(Game game) {
        this.game = game;
        setPreferredSize(new Dimension(1120, 630));
        setBackground(new Color(10, 10, 30));
        
        loadImages();
        initializeStars();
        setupButtons();
        setupMouseListener();
        startAnimations();
    }
    
    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Mouse clicked at: " + e.getX() + ", " + e.getY());
                System.out.println("New Game bounds: " + newGameButtonBounds);
                System.out.println("How to Play bounds: " + howToPlayButtonBounds);
                System.out.println("Leaderboard bounds: " + leaderboardButtonBounds);
                
                if (newGameButtonBounds.contains(e.getPoint())) {
                    System.out.println("NEW GAME clicked!");
                    game.showNameInput();
                } else if (howToPlayButtonBounds.contains(e.getPoint())) {
                    System.out.println("HOW TO PLAY clicked!");
                    game.showInstructions();
                } else if (leaderboardButtonBounds.contains(e.getPoint())) {
                    System.out.println("LEADERBOARD clicked!");
                    game.showLeaderboard();
                }
            }
        });
    }
    
    private void loadImages() {
        try {
            splashImage = ImageIO.read(new File("components/images/splashscreen.png"));
        } catch (Exception e) {
            System.out.println("Could not load splash image: " + e.getMessage());
        }
    }
    
    private void initializeStars() {
        stars = new Star[starCount];
        for (int i = 0; i < starCount; i++) {
            stars[i] = new Star();
        }
    }
    
    private void setupButtons() {
        // Three buttons in a row
        int buttonWidth = 250;
        int buttonHeight = 55;
        int buttonY = 520;
        int spacing = 30;
        int totalWidth = (buttonWidth * 3) + (spacing * 2);
        int startX = (1120 - totalWidth) / 2;
        
        newGameButtonBounds = new Rectangle(startX, buttonY, buttonWidth, buttonHeight);
        howToPlayButtonBounds = new Rectangle(startX + buttonWidth + spacing, buttonY, buttonWidth, buttonHeight);
        leaderboardButtonBounds = new Rectangle(startX + (buttonWidth + spacing) * 2, buttonY, buttonWidth, buttonHeight);
    }
    
    private void startAnimations() {
        animationTimer = new Timer(30, e -> {
            titleAlpha = Math.min(1f, titleAlpha + 0.02f);
            if (titleAlpha >= 1f) {
                buttonAlpha = Math.min(1f, buttonAlpha + 0.03f);
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
        
        drawBackground(g2d);
        drawStars(g2d);
        drawTitle(g2d);
        drawButtons(g2d);
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
        Composite oldComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, titleAlpha));
        
        if (splashImage != null) {
            // Draw the image higher and expand proportionally to avoid distortion
            int imgWidth = 1228; // Proportionally expanded width
            int imgHeight = 690; // Expanded height to cover bottom when shifted
            int imgX = (1120 - imgWidth) / 2; // Center horizontally
            // Shift image up by 60 pixels to balance with buttons
            g2d.drawImage(splashImage, imgX, -60, imgWidth, imgHeight, null);
        } else {
            // Fallback text rendering
            g2d.setFont(new Font("Arial", Font.BOLD, 80));
            g2d.setColor(Color.WHITE);
            String title = "THE ULTIMATE";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (1120 - fm.stringWidth(title)) / 2;
            g2d.drawString(title, x, 140);
            
            g2d.setFont(new Font("Arial", Font.BOLD, 110));
            String subtitle = "2048 GAME";
            fm = g2d.getFontMetrics();
            x = (1120 - fm.stringWidth(subtitle)) / 2;
            g2d.drawString(subtitle, x, 270);
        }
        
        g2d.setComposite(oldComposite);
    }
    
    private void drawButtons(Graphics2D g2d) {
        Composite oldComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, buttonAlpha));
        
        // Draw NEW GAME button (left)
        drawButton(g2d, newGameButtonBounds, "NEW GAME", new Color(70, 100, 200));
        
        // Draw HOW TO PLAY button (center)
        drawButton(g2d, howToPlayButtonBounds, "HOW TO PLAY", new Color(70, 100, 200));
        
        // Draw LEADERBOARD button (right)
        drawButton(g2d, leaderboardButtonBounds, "LEADERBOARD", new Color(70, 100, 200));
        
        g2d.setComposite(oldComposite);
    }
    
    private void drawButton(Graphics2D g2d, Rectangle bounds, String text, Color baseColor) {
        // Button shadow for depth
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRoundRect(bounds.x + 4, bounds.y + 4, bounds.width, bounds.height, 35, 35);
        
        // Button background with gradient
        GradientPaint buttonGradient = new GradientPaint(
            bounds.x, bounds.y, baseColor,
            bounds.x, bounds.y + bounds.height, new Color(baseColor.getRed() - 30, baseColor.getGreen() - 30, baseColor.getBlue() - 30)
        );
        g2d.setPaint(buttonGradient);
        g2d.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 35, 35);
        
        // Button border
        g2d.setColor(new Color(baseColor.getRed() + 30, baseColor.getGreen() + 30, baseColor.getBlue() + 30));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 35, 35);
        
        // Button text - adjust font size based on text length
        int fontSize = text.length() > 10 ? 22 : 28;
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
        FontMetrics fm = g2d.getFontMetrics();
        int textX = bounds.x + (bounds.width - fm.stringWidth(text)) / 2;
        int textY = bounds.y + ((bounds.height - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(text, textX, textY);
    }
}