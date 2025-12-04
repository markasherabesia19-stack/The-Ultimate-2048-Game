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
    private Image newGameButtonImage;
    private float titleAlpha = 0f;
    private float buttonAlpha = 0f;
    private Timer animationTimer;
    private int starCount = 100;
    private Star[] stars;
    
    private Rectangle newGameButtonBounds;
    
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
        startAnimations();
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (newGameButtonBounds.contains(e.getPoint())) {
                    game.startNewGame();
                }
            }
        });
    }
    
    private void loadImages() {
        try {
            splashImage = ImageIO.read(new File("components/images/splashscreen.png"));
            // Not loading button image - using code-generated button instead
        } catch (Exception e) {
            System.out.println("Could not load images: " + e.getMessage());
        }
    }
    
    private void initializeStars() {
        stars = new Star[starCount];
        for (int i = 0; i < starCount; i++) {
            stars[i] = new Star();
        }
    }
    
    private void setupButtons() {
        // Shorter, nicer button that fits perfectly with the splash screen
        newGameButtonBounds = new Rectangle(410, 510, 300, 55);
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
        drawButton(g2d);
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
            // Full screen title image
            g2d.drawImage(splashImage, 0, 0, 1120, 630, null);
        } else {
            // Fallback text rendering
            g2d.setFont(new Font("Arial", Font.BOLD, 80));
            g2d.setColor(Color.WHITE);
            String title = "THE ULTIMATE";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (1120 - fm.stringWidth(title)) / 2;
            g2d.drawString(title, x, 160);
            
            g2d.setFont(new Font("Arial", Font.BOLD, 110));
            String subtitle = "2048 GAME";
            fm = g2d.getFontMetrics();
            x = (1120 - fm.stringWidth(subtitle)) / 2;
            g2d.drawString(subtitle, x, 290);
        }
        
        g2d.setComposite(oldComposite);
    }
    
    private void drawButton(Graphics2D g2d) {
        Composite oldComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, buttonAlpha));
        
        // Nice code-generated button with gradient and shadow
        // Button shadow for depth
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRoundRect(newGameButtonBounds.x + 4, newGameButtonBounds.y + 4, 
                         newGameButtonBounds.width, newGameButtonBounds.height, 35, 35);
        
        // Button background with gradient
        GradientPaint buttonGradient = new GradientPaint(
            newGameButtonBounds.x, newGameButtonBounds.y, new Color(100, 70, 200),
            newGameButtonBounds.x, newGameButtonBounds.y + newGameButtonBounds.height, new Color(70, 50, 170)
        );
        g2d.setPaint(buttonGradient);
        g2d.fillRoundRect(newGameButtonBounds.x, newGameButtonBounds.y, 
                         newGameButtonBounds.width, newGameButtonBounds.height, 35, 35);
        
        // Button border
        g2d.setColor(new Color(130, 100, 230));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(newGameButtonBounds.x, newGameButtonBounds.y, 
                         newGameButtonBounds.width, newGameButtonBounds.height, 35, 35);
        
        // Button text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 38));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "NEW GAME";
        int textX = newGameButtonBounds.x + (newGameButtonBounds.width - fm.stringWidth(text)) / 2;
        int textY = newGameButtonBounds.y + ((newGameButtonBounds.height - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(text, textX, textY);
        
        g2d.setComposite(oldComposite);
    }
}