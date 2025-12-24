package game2048;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.File;

/**
 * Instructions panel displays the game's how-to-play guide with multiple pages.
 * Features an animated starry background, image-based instruction pages with navigation controls,
 * and page indicators. Images are preloaded and cached for optimal performance across sessions.
 */
public class Instructions extends JPanel {
    private Game game;
    private String[] imageNames = {"obj2", "controls", "getSug", "win1", "favor"};
    private int currentPage = 0;
    private Rectangle backButtonBounds;
    private Rectangle nextButtonBounds;
    private Rectangle prevButtonBounds;
    
    // Animation variables
    private float pulseAlpha = 0f;
    private float pulseDirection = 0.02f;
    private Timer animationTimer;
    
    // Static cache to store loaded images (shared across all instances)
    private static Image[] cachedInstructionImages = null;
    private static boolean imagesLoaded = false;
    
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
    
    private int starCount = 100;
    private Star[] stars;
    
    public Instructions(Game game) {
        this.game = game;
        setPreferredSize(new Dimension(1120, 630));
        setBackground(new Color(10, 10, 30));
        
        // Load images only once
        if (!imagesLoaded) {
            System.out.println("Loading instruction images for the first time...");
            loadInstructionImages();
        } else {
            System.out.println("Using cached instruction images (instant load!)");
        }
        
        setupButtons();
        setupMouseListener();
        startAnimations();
    }
    
    private void loadInstructionImages() {
        long startTime = System.currentTimeMillis();
        
        cachedInstructionImages = new Image[imageNames.length];
        
        // Load images in parallel for faster loading
        Thread[] loaders = new Thread[imageNames.length];
        
        for (int i = 0; i < imageNames.length; i++) {
            final int index = i;
            loaders[i] = new Thread(() -> {
                try {
                    File imageFile = new File("components/images/" + imageNames[index] + ".png");
                    if (imageFile.exists()) {
                        cachedInstructionImages[index] = ImageIO.read(imageFile);
                        System.out.println("  Loaded: " + imageNames[index] + ".png");
                    } else {
                        System.out.println("  Missing: " + imageNames[index] + ".png");
                    }
                } catch (Exception e) {
                    System.out.println("  Error loading " + imageNames[index] + ".png: " + e.getMessage());
                }
            });
            loaders[i].start();
        }
        
        // Wait for all images to load
        for (Thread loader : loaders) {
            try {
                loader.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        imagesLoaded = true;
        long endTime = System.currentTimeMillis();
        System.out.println("All instruction images loaded in " + (endTime - startTime) + "ms");
    }
    
    // Static method to preload images (called from Game constructor)
    public static void preloadImages() {
        if (!imagesLoaded) {
            System.out.println("Preloading instruction images in background...");
            new Thread(() -> {
                // Create a temporary Instructions object to trigger image loading
                String[] imageNames = {"obj2", "controls", "getSug", "win1", "favor"};
                long startTime = System.currentTimeMillis();
                
                cachedInstructionImages = new Image[imageNames.length];
                
                // Load images in parallel
                Thread[] loaders = new Thread[imageNames.length];
                
                for (int i = 0; i < imageNames.length; i++) {
                    final int index = i;
                    loaders[i] = new Thread(() -> {
                        try {
                            File imageFile = new File("components/images/" + imageNames[index] + ".png");
                            if (imageFile.exists()) {
                                cachedInstructionImages[index] = ImageIO.read(imageFile);
                                System.out.println("  Preloaded: " + imageNames[index] + ".png");
                            }
                        } catch (Exception e) {
                            System.out.println("  Error preloading " + imageNames[index] + ".png");
                        }
                    });
                    loaders[i].start();
                }
                
                // Wait for all to finish
                for (Thread loader : loaders) {
                    try {
                        loader.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                
                imagesLoaded = true;
                long endTime = System.currentTimeMillis();
                System.out.println("Background preload completed in " + (endTime - startTime) + "ms");
            }).start();
        } else {
            System.out.println("Images already preloaded");
        }
    }
    
    private void initializeStars() {
        stars = new Star[starCount];
        for (int i = 0; i < starCount; i++) {
            stars[i] = new Star();
        }
    }
    
    private void setupButtons() {
        // Button positions - symmetrical layout
        int buttonY = 540;
        int buttonWidth = 200;
        int buttonHeight = 60;
        
        // BACK button - far left
        backButtonBounds = new Rectangle(80, buttonY, buttonWidth, buttonHeight);
        
        // PREV button - center
        int centerX = (1120 - buttonWidth) / 2;
        prevButtonBounds = new Rectangle(centerX, buttonY, buttonWidth, buttonHeight);
        
        // NEXT button - far right
        nextButtonBounds = new Rectangle(1120 - buttonWidth - 80, buttonY, buttonWidth, buttonHeight);
    }
    
    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Check BACK button first
                if (backButtonBounds.contains(e.getPoint())) {
                    System.out.println("BACK button clicked!");
                    game.returnToMainMenu();
                    return;
                }
                
                // Check PREV button
                if (prevButtonBounds.contains(e.getPoint()) && currentPage > 0) {
                    System.out.println("PREV button clicked!");
                    currentPage--;
                    repaint();
                    return;
                }
                
                // Check NEXT button
                if (nextButtonBounds.contains(e.getPoint()) && currentPage < imageNames.length - 1) {
                    System.out.println("NEXT button clicked!");
                    currentPage++;
                    repaint();
                    return;
                }
            }
        });
    }
    
    private void startAnimations() {
        initializeStars();
        
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
        drawTitle(g2d);
        drawInstructionImage(g2d);
        drawPageIndicator(g2d);
        drawNavigationButtons(g2d);
        drawReturnButton(g2d);
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
        
        String title = "HOW TO PLAY";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (1120 - fm.stringWidth(title)) / 2;
        
        // Shadow effect
        g2d.drawString(title, x - 2, 62);
        g2d.drawString(title, x + 2, 66);
        
        g2d.setColor(Color.WHITE);
        g2d.drawString(title, x, 64);
    }
    
    private void drawInstructionImage(Graphics2D g2d) {
        if (cachedInstructionImages != null && cachedInstructionImages[currentPage] != null) {
            // Draw image in a centered frame with border
            int imgWidth = 800;
            int imgHeight = 400;
            int imgX = (1120 - imgWidth) / 2;
            int imgY = 100;
            
            // Border/frame
            g2d.setColor(new Color(120, 80, 220, (int)(100 + pulseAlpha * 100)));
            g2d.fillRoundRect(imgX - 12, imgY - 12, imgWidth + 24, imgHeight + 24, 20, 20);
            
            g2d.setColor(new Color(40, 30, 80));
            g2d.fillRoundRect(imgX - 8, imgY - 8, imgWidth + 16, imgHeight + 16, 15, 15);
            
            // Draw the instruction image
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(cachedInstructionImages[currentPage], imgX, imgY, imgWidth, imgHeight, this);
            
            // Border outline
            g2d.setColor(new Color(150, 120, 255, (int)(200 + pulseAlpha * 55)));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(imgX - 8, imgY - 8, imgWidth + 16, imgHeight + 16, 15, 15);
        } else {
            // Fallback if image not loaded
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 24));
            String errorText = "Loading image: " + imageNames[currentPage] + ".png";
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(errorText, (1120 - fm.stringWidth(errorText)) / 2, 300);
        }
    }
    
    private void drawPageIndicator(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.setColor(Color.WHITE);
        
        String pageText = "Page " + (currentPage + 1) + " of " + imageNames.length;
        FontMetrics fm = g2d.getFontMetrics();
        int x = (1120 - fm.stringWidth(pageText)) / 2;
        
        g2d.drawString(pageText, x, 530);
        
        // Draw dots indicator
        int dotSize = 12;
        int spacing = 20;
        int totalWidth = imageNames.length * spacing - spacing + dotSize;
        int startX = (1120 - totalWidth) / 2;
        int dotY = 545;
        
        for (int i = 0; i < imageNames.length; i++) {
            if (i == currentPage) {
                g2d.setColor(new Color(150, 100, 255));
                g2d.fillOval(startX + i * spacing, dotY, dotSize, dotSize);
            } else {
                g2d.setColor(new Color(100, 80, 150, 150));
                g2d.fillOval(startX + i * spacing, dotY, dotSize, dotSize);
            }
        }
    }
    
    private void drawNavigationButtons(Graphics2D g2d) {
        // Previous button
        if (currentPage > 0) {
            drawStyledButton(g2d, prevButtonBounds, "◄ PREV", new Color(100, 70, 180));
        }
        
        // Next button
        if (currentPage < imageNames.length - 1) {
            drawStyledButton(g2d, nextButtonBounds, "NEXT ►", new Color(100, 70, 180));
        }
    }
    
    private void drawReturnButton(Graphics2D g2d) {
        drawStyledButton(g2d, backButtonBounds, "◄ BACK", new Color(120, 80, 220));
    }
    
    private void drawStyledButton(Graphics2D g2d, Rectangle bounds, String text, Color baseColor) {
        // Glow effect
        g2d.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), (int)(80 + pulseAlpha * 150)));
        g2d.fillRoundRect(bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4, 27, 27);
        
        // Shadow
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillRoundRect(bounds.x + 4, bounds.y + 4, bounds.width, bounds.height, 25, 25);
        
        // Button gradient
        GradientPaint buttonGradient = new GradientPaint(
            bounds.x, bounds.y, baseColor,
            bounds.x, bounds.y + bounds.height, new Color(baseColor.getRed() - 40, baseColor.getGreen() - 40, baseColor.getBlue() - 40)
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

    @Override
    public void removeNotify() {
        super.removeNotify();
    
        System.out.println("Instructions: Stopping animation timer");
    
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
            System.out.println("Instructions timer stopped");
        }
    }
}