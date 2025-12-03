package game2048;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SplashScreen extends JWindow {
    private int duration;
    private float opacity = 0.0f;

    public SplashScreen(int duration) {
        this.duration = duration;
    }

    public void showSplash() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.BLACK);
        
        try {
            ImageIcon icon = new ImageIcon("components/splashscreen.png");
            
            if (icon.getIconWidth() <= 0) {
                throw new Exception("Image not loaded");
            }
            
            // Scale the image to a reasonable size
            Image scaledImage = icon.getImage().getScaledInstance(1200, 700, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            
            JLabel label = new JLabel(scaledIcon);
            content.add(label, BorderLayout.CENTER);
        } catch (Exception e) {
            System.out.println("Could not load splash image: " + e.getMessage());
            JLabel label = new JLabel("THE ULTIMATE 2048 GAME", SwingConstants.CENTER);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Arial", Font.BOLD, 40));
            content.add(label, BorderLayout.CENTER);
        }
        
        setContentPane(content);
        pack();
        setLocationRelativeTo(null);
        
        // Enable transparency for fade effects
        setBackground(new Color(0, 0, 0, 0));
        setOpacity(0.0f);
        setVisible(true);
        
        // Start fade-in animation
        fadeIn();
    }
    
    private void fadeIn() {
        Timer fadeInTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity += 0.05f;
                if (opacity >= 1.0f) {
                    opacity = 1.0f;
                    setOpacity(opacity);
                    ((Timer) e.getSource()).stop();
                    // After fade in, wait then fade out
                    waitAndFadeOut();
                } else {
                    setOpacity(opacity);
                }
            }
        });
        fadeInTimer.start();
    }
    
    private void waitAndFadeOut() {
        // Wait for the specified duration before fading out
        Timer waitTimer = new Timer(duration, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((Timer) e.getSource()).stop();
                fadeOut();
            }
        });
        waitTimer.setRepeats(false);
        waitTimer.start();
    }
    
    private void fadeOut() {
        Timer fadeOutTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity -= 0.05f;
                if (opacity <= 0.0f) {
                    opacity = 0.0f;
                    setOpacity(opacity);
                    ((Timer) e.getSource()).stop();
                    dispose();
                    // Launch the game after fade out
                    launchGame();
                } else {
                    setOpacity(opacity);
                }
            }
        });
        fadeOutTimer.start();
    }
    
    private void launchGame() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Game();
            }
        });
    }
}