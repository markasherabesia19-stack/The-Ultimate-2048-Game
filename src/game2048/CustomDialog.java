package game2048;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class CustomDialog extends JDialog {
    private boolean confirmed = false;
    private float pulseAlpha = 0f;
    private float pulseDirection = 0.02f;
    private Timer animationTimer;
    
    public CustomDialog(JFrame parent, String title, String message) {
        super(parent, title, true);
        setUndecorated(true);
        setSize(450, 250);
        setLocationRelativeTo(parent);
        
        // Custom panel with painting
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Outer glow
                g2d.setColor(new Color(150, 100, 255, (int)(100 + pulseAlpha * 100)));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                
                // Background gradient
                GradientPaint bgGradient = new GradientPaint(
                    0, 0, new Color(40, 30, 80),
                    0, getHeight(), new Color(60, 40, 100)
                );
                g2d.setPaint(bgGradient);
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 22, 22);
                
                // Border
                g2d.setColor(new Color(150, 120, 255, (int)(200 + pulseAlpha * 55)));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 22, 22);
            }
        };
        
        contentPanel.setLayout(null);
        contentPanel.setOpaque(false);
        
        // Icon
        JLabel iconLabel = new JLabel("⚠️");
        iconLabel.setFont(new Font("Arial", Font.BOLD, 48));
        iconLabel.setBounds(30, 30, 60, 60);
        contentPanel.add(iconLabel);
        
        // Title - Centered
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(255, 220, 100));
        titleLabel.setBounds(0, 35, 450, 35);
        contentPanel.add(titleLabel);
        
        // Message
        JLabel messageLabel = new JLabel("<html><center>" + message + "</center></html>");
        messageLabel.setFont(new Font("Arial", Font.BOLD, 18));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setBounds(30, 90, 390, 60);
        contentPanel.add(messageLabel);
        
        // YES button
        JButton yesButton = createStyledButton("YES", new Color(70, 150, 70));
        yesButton.setBounds(80, 165, 140, 50);
        yesButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });
        contentPanel.add(yesButton);
        
        // NO button
        JButton noButton = createStyledButton("NO", new Color(150, 50, 50));
        noButton.setBounds(230, 165, 140, 50);
        noButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        contentPanel.add(noButton);
        
        setContentPane(contentPanel);
        
        // Start animation
        animationTimer = new Timer(30, e -> {
            pulseAlpha += pulseDirection;
            if (pulseAlpha > 0.3f || pulseAlpha < 0f) {
                pulseDirection *= -1;
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
    
    public static boolean showConfirmDialog(JFrame parent, String title, String message) {
        CustomDialog dialog = new CustomDialog(parent, title, message);
        dialog.setVisible(true);
        return dialog.isConfirmed();
    }
}