package game2048;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.File;

public class NameInputPanel extends JPanel {
    private Game game;
    private Image nameImage;
    private JTextField nameField;
    private Rectangle startButtonBounds;
    private Rectangle backButtonBounds;
    
    // Animation variables
    private float pulseAlpha = 0f;
    private float pulseDirection = 0.02f;
    private Timer animationTimer;
    private int starCount = 100;
    private Star[] stars;
    
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
    
    public NameInputPanel(Game game) {
        this.game = game;
        setPreferredSize(new Dimension(1120, 630));
        setBackground(new Color(10, 10, 30));
        setLayout(null);
        
        loadImages();
        initializeStars();
        setupNameField();
        setupButtons();
        startAnimations();
        setupMouseListener();
    }
    
    private void loadImages() {
        try {
            nameImage = ImageIO.read(new File("components/images/name.png"));
        } catch (Exception e) {
            System.out.println("Could not load name image: " + e.getMessage());
        }
    }
    
    private void initializeStars() {
        stars = new Star[starCount];
        for (int i = 0; i < starCount; i++) {
            stars[i] = new Star();
        }
    }
    
    private void setupNameField() {
        nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.BOLD, 28));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setBackground(new Color(40, 30, 80));
        nameField.setForeground(Color.WHITE);
        nameField.setCaretColor(Color.WHITE);
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 120, 255), 3),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        // Position below where the name image will be
        nameField.setBounds(360, 380, 400, 60);
        add(nameField);
        
        // Add Enter key listener to start game
        nameField.addActionListener(e -> startGameWithName());
    }
    
    private void setupButtons() {
        int buttonWidth = 250;
        int buttonHeight = 60;
        int buttonY = 480;
        int spacing = 40;
        int totalWidth = (buttonWidth * 2) + spacing;
        int startX = (1120 - totalWidth) / 2;
        
        startButtonBounds = new Rectangle(startX, buttonY, buttonWidth, buttonHeight);
        backButtonBounds = new Rectangle(startX + buttonWidth + spacing, buttonY, buttonWidth, buttonHeight);
    }
    
    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (startButtonBounds.contains(e.getPoint())) {
                    startGameWithName();
                } else if (backButtonBounds.contains(e.getPoint())) {
                    game.returnToMainMenu();
                }
            }
        });
    }
    
    private void startGameWithName() {
        String playerName = nameField.getText().trim();
        
        if (playerName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter your name!",
                "Name Required",
                JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return;
        }
        
        game.setPlayerName(playerName);
        game.startNewGame();
    }
    
    private void startAnimations() {
        animationTimer = new Timer(30, e -> {
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
        drawNameImage(g2d);
        drawButtons(g2d);
    }
    
    private void drawBackground(Graphics2D g2d) {
        if (nameImage != null) {
            // Draw name.png as full background
            g2d.drawImage(nameImage, 0, 0, 1120, 630, this);
        } else {
            // Fallback gradient
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(10, 10, 50),
                0, 630, new Color(60, 20, 80)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, 1120, 630);
        }
    }
    
    private void drawStars(Graphics2D g2d) {
        for (Star star : stars) {
            g2d.setColor(new Color(255, 255, 255, (int)(star.alpha * 255)));
            g2d.fillOval((int)star.x, (int)star.y, (int)star.size, (int)star.size);
        }
    }
    
    private void drawNameImage(Graphics2D g2d) {
        // Name image is now the background, so this method does nothing
        // If you want a fallback title when image is not loaded:
        if (nameImage == null) {
            g2d.setFont(new Font("Arial", Font.BOLD, 48));
            g2d.setColor(new Color(150, 100, 255, (int)(pulseAlpha * 255)));
            
            String title = "ENTER YOUR NAME";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (1120 - fm.stringWidth(title)) / 2;
            
            g2d.drawString(title, x - 2, 222);
            g2d.drawString(title, x + 2, 226);
            
            g2d.setColor(Color.WHITE);
            g2d.drawString(title, x, 224);
            
            g2d.setFont(new Font("Arial", Font.BOLD, 32));
            String subtitle = "MASTER!";
            fm = g2d.getFontMetrics();
            x = (1120 - fm.stringWidth(subtitle)) / 2;
            
            g2d.setColor(new Color(150, 100, 255, (int)(pulseAlpha * 255)));
            g2d.drawString(subtitle, x - 1, 277);
            g2d.drawString(subtitle, x + 1, 279);
            
            g2d.setColor(Color.WHITE);
            g2d.drawString(subtitle, x, 278);
        }
    }
    
    private void drawButtons(Graphics2D g2d) {
        drawStyledButton(g2d, startButtonBounds, "START GAME", new Color(70, 150, 70));
        drawStyledButton(g2d, backButtonBounds, "◄ BACK", new Color(100, 70, 180));
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
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
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