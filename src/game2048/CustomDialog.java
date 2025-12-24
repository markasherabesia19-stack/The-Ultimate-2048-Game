package game2048;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Custom styled dialog for various game notifications and confirmations.
 * Supports multiple dialog types: confirm dialogs, game over screens, victory screens, and warning messages. 
 * Features animated backgrounds with pulsing effects and floating stars for victory dialogs.
 */
public class CustomDialog extends JDialog {
    private boolean confirmed = false;
    private float pulseAlpha = 0f;
    private float pulseDirection = 0.02f;
    private Timer animationTimer;
    
    // Dialog types
    public static final int CONFIRM_DIALOG = 0;
    public static final int GAME_OVER_DIALOG = 1;
    public static final int VICTORY_DIALOG = 2;
    public static final int WARNING_DIALOG = 3;
    
    // Victory animation
    private int starCount = 50;
    private Star[] stars;
    
    class Star {
        float x, y, size, speedY, alpha;
        
        Star() {
            reset();
        }
        
        void reset() {
            x = (float)(Math.random() * getWidth());
            y = getHeight() + (float)(Math.random() * 50);
            size = (float)(Math.random() * 3 + 1);
            speedY = (float)(Math.random() * 2 + 1);
            alpha = (float)(Math.random() * 0.8 + 0.2);
        }
        
        void update() {
            y -= speedY;
            if (y < -10) reset();
        }
    }
    
    // Constructor for confirm dialogs
    public CustomDialog(JFrame parent, String title, String message) {
        this(parent, title, message, CONFIRM_DIALOG, null, 0, 0, false, 0);
    }
    
    // Constructor for warning dialogs
    public CustomDialog(JFrame parent, String title, String message, int dialogType) {
        this(parent, title, message, dialogType, null, 0, 0, false, 0);
    }
    
    // Constructor for game over and victory dialogs
    public CustomDialog(JFrame parent, String title, String message, int dialogType, 
                       String playerName, int score, int highestTile, boolean madeLeaderboard, int rank) {
        super(parent, title, true);
        setUndecorated(true);
        
        // Set size based on dialog type
        if (dialogType == VICTORY_DIALOG) {
            setSize(500, 380);
        } else if (dialogType == GAME_OVER_DIALOG) {
            setSize(500, 350);
        } else if (dialogType == WARNING_DIALOG) {
            setSize(450, 220);
        } else {
            setSize(450, 250);
        }
        
        setLocationRelativeTo(parent);
        
        // Initialize stars for victory dialog
        if (dialogType == VICTORY_DIALOG) {
            stars = new Star[starCount];
            for (int i = 0; i < starCount; i++) {
                stars[i] = new Star();
                stars[i].y = (float)(Math.random() * 380);
            }
        }
        
        // Custom panel with painting
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Draw stars for victory dialog
                if (dialogType == VICTORY_DIALOG && stars != null) {
                    for (Star star : stars) {
                        g2d.setColor(new Color(255, 215, 0, (int)(star.alpha * 255)));
                        g2d.fillOval((int)star.x, (int)star.y, (int)star.size, (int)star.size);
                    }
                }
                
                // Outer glow - color based on dialog type
                Color glowColor;
                if (dialogType == VICTORY_DIALOG) {
                    glowColor = new Color(255, 215, 0, (int)(100 + pulseAlpha * 100)); // Gold
                } else if (dialogType == GAME_OVER_DIALOG) {
                    glowColor = new Color(150, 100, 255, (int)(100 + pulseAlpha * 100)); // Purple
                } else if (dialogType == WARNING_DIALOG) {
                    glowColor = new Color(255, 150, 0, (int)(100 + pulseAlpha * 100)); // Orange
                } else {
                    glowColor = new Color(150, 100, 255, (int)(100 + pulseAlpha * 100)); // Purple
                }
                g2d.setColor(glowColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                
                // Background gradient - different colors for different types
                GradientPaint bgGradient;
                if (dialogType == VICTORY_DIALOG) {
                    bgGradient = new GradientPaint(
                        0, 0, new Color(60, 40, 100),
                        0, getHeight(), new Color(40, 60, 100)
                    );
                } else if (dialogType == WARNING_DIALOG) {
                    bgGradient = new GradientPaint(
                        0, 0, new Color(60, 40, 30),
                        0, getHeight(), new Color(80, 50, 40)
                    );
                } else {
                    bgGradient = new GradientPaint(
                        0, 0, new Color(40, 30, 80),
                        0, getHeight(), new Color(60, 40, 100)
                    );
                }
                g2d.setPaint(bgGradient);
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 22, 22);
                
                // Border - gold for victory, orange for warning, purple for others
                Color borderColor;
                if (dialogType == VICTORY_DIALOG) {
                    borderColor = new Color(255, 215, 0, (int)(200 + pulseAlpha * 55));
                } else if (dialogType == WARNING_DIALOG) {
                    borderColor = new Color(255, 180, 50, (int)(200 + pulseAlpha * 55));
                } else {
                    borderColor = new Color(150, 120, 255, (int)(200 + pulseAlpha * 55));
                }
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 22, 22);
            }
        };
        
        contentPanel.setLayout(null);
        contentPanel.setOpaque(false);
        
        // Build UI based on dialog type
        if (dialogType == CONFIRM_DIALOG) {
            buildConfirmDialog(contentPanel, title, message);
        } else if (dialogType == WARNING_DIALOG) {
            buildWarningDialog(contentPanel, title, message);
        } else if (dialogType == GAME_OVER_DIALOG) {
            buildGameOverDialog(contentPanel, playerName, score, highestTile, madeLeaderboard, rank);
        } else if (dialogType == VICTORY_DIALOG) {
            buildVictoryDialog(contentPanel, playerName, score, highestTile, madeLeaderboard, rank);
        }
        
        setContentPane(contentPanel);
        
        // Start animation
        animationTimer = new Timer(30, e -> {
            pulseAlpha += pulseDirection;
            if (pulseAlpha > 0.3f || pulseAlpha < 0f) {
                pulseDirection *= -1;
            }
            
            // Update stars for victory dialog
            if (dialogType == VICTORY_DIALOG && stars != null) {
                for (Star star : stars) {
                    star.update();
                }
            }
            
            contentPanel.repaint();
        });
        animationTimer.start();
        
        // Stop animation when dialog closes
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                animationTimer.stop();
            }
        });
    }
    
    private void buildConfirmDialog(JPanel panel, String title, String message) {
        // Title - Centered
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(255, 220, 100));
        titleLabel.setBounds(0, 40, 450, 35);
        panel.add(titleLabel);
        
        // Message
        JLabel messageLabel = new JLabel("<html><center>" + message + "</center></html>");
        messageLabel.setFont(new Font("Arial", Font.BOLD, 18));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setBounds(30, 95, 390, 60);
        panel.add(messageLabel);
        
        // YES button
        JButton yesButton = createStyledButton("YES", new Color(70, 150, 70));
        yesButton.setBounds(80, 165, 140, 50);
        yesButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });
        panel.add(yesButton);
        
        // NO button
        JButton noButton = createStyledButton("NO", new Color(150, 50, 50));
        noButton.setBounds(230, 165, 140, 50);
        noButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        panel.add(noButton);
    }
    
    private void buildWarningDialog(JPanel panel, String title, String message) {
        // Warning icon - Draw a custom triangle with exclamation mark
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                
                // Draw warning triangle
                int[] xPoints = {centerX, centerX - 25, centerX + 25};
                int[] yPoints = {centerY - 20, centerY + 20, centerY + 20};
                
                g2d.setColor(new Color(255, 200, 0));
                g2d.fillPolygon(xPoints, yPoints, 3);
                
                g2d.setColor(new Color(200, 150, 0));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawPolygon(xPoints, yPoints, 3);
                
                // Draw exclamation mark
                g2d.setColor(new Color(80, 50, 20));
                g2d.fillRect(centerX - 3, centerY - 10, 6, 15);
                g2d.fillOval(centerX - 3, centerY + 8, 6, 6);
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setBounds(0, 30, 450, 50);
        panel.add(iconPanel);
        
        // Title
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 220, 100));
        titleLabel.setBounds(0, 85, 450, 30);
        panel.add(titleLabel);
        
        // Message
        JLabel messageLabel = new JLabel("<html><center>" + message + "</center></html>");
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setBounds(30, 120, 390, 40);
        panel.add(messageLabel);
        
        // OK button
        JButton okButton = createStyledButton("OK", new Color(200, 120, 50));
        okButton.setBounds(150, 165, 150, 45);
        okButton.addActionListener(e -> dispose());
        panel.add(okButton);
    }
    
    private void buildGameOverDialog(JPanel panel, String playerName, int score, 
                                     int highestTile, boolean madeLeaderboard, int rank) {
        // Title
        JLabel titleLabel = new JLabel("GAME OVER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(255, 100, 100));
        titleLabel.setBounds(0, 35, 500, 40);
        panel.add(titleLabel);
        
        // Player name
        JLabel nameLabel = new JLabel(playerName, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        nameLabel.setForeground(new Color(200, 180, 255));
        nameLabel.setBounds(0, 95, 500, 30);
        panel.add(nameLabel);
        
        // Score
        JLabel scoreLabel = new JLabel("Final Score: " + String.format("%,d", score), SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 22));
        scoreLabel.setForeground(new Color(150, 255, 150));
        scoreLabel.setBounds(0, 145, 500, 30);
        panel.add(scoreLabel);
        
        // Highest Tile
        JLabel tileLabel = new JLabel("Highest Tile: " + highestTile, SwingConstants.CENTER);
        tileLabel.setFont(new Font("Arial", Font.BOLD, 20));
        tileLabel.setForeground(new Color(255, 215, 0));
        tileLabel.setBounds(0, 185, 500, 25);
        panel.add(tileLabel);
        
        // Leaderboard status
        if (madeLeaderboard && rank > 0) {
            JLabel leaderboardLabel = new JLabel("LEADERBOARD RANK: #" + rank, SwingConstants.CENTER);
            leaderboardLabel.setFont(new Font("Arial", Font.BOLD, 20));
            leaderboardLabel.setForeground(new Color(255, 215, 0));
            leaderboardLabel.setBounds(0, 230, 500, 30);
            panel.add(leaderboardLabel);
        } else if (madeLeaderboard) {
            JLabel leaderboardLabel = new JLabel("YOU MADE THE LEADERBOARD!", SwingConstants.CENTER);
            leaderboardLabel.setFont(new Font("Arial", Font.BOLD, 20));
            leaderboardLabel.setForeground(new Color(255, 215, 0));
            leaderboardLabel.setBounds(0, 230, 500, 30);
            panel.add(leaderboardLabel);
        } else {
            JLabel encourageLabel = new JLabel("Keep trying to reach the leaderboard!", SwingConstants.CENTER);
            encourageLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            encourageLabel.setForeground(new Color(200, 200, 200));
            encourageLabel.setBounds(0, 230, 500, 30);
            panel.add(encourageLabel);
        }
        
        // OK button
        JButton okButton = createStyledButton("OK", new Color(120, 80, 220));
        okButton.setBounds(175, 275, 150, 55);
        okButton.addActionListener(e -> dispose());
        panel.add(okButton);
    }
    
    private void buildVictoryDialog(JPanel panel, String playerName, int score, 
                                    int highestTile, boolean madeLeaderboard, int rank) {
        // Title - Larger and more prominent
        JLabel titleLabel = new JLabel("VICTORY!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(new Color(255, 215, 0));
        titleLabel.setBounds(0, 40, 500, 50);
        panel.add(titleLabel);
        
        // Congratulations
        JLabel congratsLabel = new JLabel("Congratulations " + playerName + "!", SwingConstants.CENTER);
        congratsLabel.setFont(new Font("Arial", Font.BOLD, 24));
        congratsLabel.setForeground(Color.WHITE);
        congratsLabel.setBounds(0, 110, 500, 30);
        panel.add(congratsLabel);
        
        // Achievement
        JLabel achievementLabel = new JLabel("You reached 2048!", SwingConstants.CENTER);
        achievementLabel.setFont(new Font("Arial", Font.BOLD, 22));
        achievementLabel.setForeground(new Color(200, 255, 200));
        achievementLabel.setBounds(0, 155, 500, 30);
        panel.add(achievementLabel);
        
        // Score
        JLabel scoreLabel = new JLabel("Final Score: " + String.format("%,d", score), SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 22));
        scoreLabel.setForeground(new Color(150, 255, 150));
        scoreLabel.setBounds(0, 200, 500, 30);
        panel.add(scoreLabel);
        
        // Leaderboard status
        if (madeLeaderboard && rank > 0) {
            JLabel leaderboardLabel = new JLabel("LEADERBOARD RANK: #" + rank, SwingConstants.CENTER);
            leaderboardLabel.setFont(new Font("Arial", Font.BOLD, 22));
            leaderboardLabel.setForeground(new Color(255, 215, 0));
            leaderboardLabel.setBounds(0, 250, 500, 30);
            panel.add(leaderboardLabel);
        } else if (madeLeaderboard) {
            JLabel leaderboardLabel = new JLabel("YOU MADE THE LEADERBOARD!", SwingConstants.CENTER);
            leaderboardLabel.setFont(new Font("Arial", Font.BOLD, 22));
            leaderboardLabel.setForeground(new Color(255, 215, 0));
            leaderboardLabel.setBounds(0, 250, 500, 30);
            panel.add(leaderboardLabel);
        }
        
        // OK button
        JButton okButton = createStyledButton("CONTINUE", new Color(70, 150, 70));
        okButton.setBounds(175, 300, 150, 55);
        okButton.addActionListener(e -> dispose());
        panel.add(okButton);
    }
    
    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Shadow
                g2d.setColor(new Color(0, 0, 0, 120));
                g2d.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, 20, 20);
                
                // Button gradient
                Color topColor = getModel().isPressed() ? 
                    new Color(baseColor.getRed() - 20, baseColor.getGreen() - 20, baseColor.getBlue() - 20) : 
                    baseColor;
                Color bottomColor = new Color(
                    Math.max(0, baseColor.getRed() - 40),
                    Math.max(0, baseColor.getGreen() - 40),
                    Math.max(0, baseColor.getBlue() - 40)
                );
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, topColor,
                    0, getHeight(), bottomColor
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 20, 20);
                
                // Shine effect
                GradientPaint shine = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 60),
                    0, getHeight() / 2, new Color(255, 255, 255, 0)
                );
                g2d.setPaint(shine);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() / 2, 20, 20);
                
                // Border
                g2d.setColor(new Color(180, 150, 255, 200));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 20, 20);
                
                // Text
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                
                // Text glow
                g2d.setColor(new Color(200, 180, 255, 100));
                g2d.drawString(getText(), textX - 1, textY - 1);
                g2d.drawString(getText(), textX + 1, textY + 1);
                
                // Main text
                g2d.setColor(Color.WHITE);
                g2d.drawString(getText(), textX, textY);
            }
        };
        
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    // Static method for confirm dialogs 
    public static boolean showConfirmDialog(JFrame parent, String title, String message) {
        CustomDialog dialog = new CustomDialog(parent, title, message);
        dialog.setVisible(true);
        return dialog.isConfirmed();
    }
    
    // Static method for warning dialogs
    public static void showWarningDialog(JFrame parent, String title, String message) {
        CustomDialog dialog = new CustomDialog(parent, title, message, WARNING_DIALOG);
        dialog.setVisible(true);
    }
    
    // Static method for game over dialog
    public static void showGameOverDialog(JFrame parent, String playerName, int score, 
                                         int highestTile, boolean madeLeaderboard, int rank) {
        CustomDialog dialog = new CustomDialog(parent, "Game Over", "", GAME_OVER_DIALOG, 
                                               playerName, score, highestTile, madeLeaderboard, rank);
        dialog.setVisible(true);
    }
    
    // Static method for victory dialog
    public static void showVictoryDialog(JFrame parent, String playerName, int score, 
                                        int highestTile, boolean madeLeaderboard, int rank) {
        CustomDialog dialog = new CustomDialog(parent, "Victory!", "", VICTORY_DIALOG, 
                                               playerName, score, highestTile, madeLeaderboard, rank);
        dialog.setVisible(true);
    }
}