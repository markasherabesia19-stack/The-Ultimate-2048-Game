package game2048;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class SplashScreen extends JWindow {
    private float opacity = 0.0f;
    private JLabel pressLabel;

    public SplashScreen() {
    }

    public void showSplash() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.BLACK);
        
        try {
            ImageIcon icon = new ImageIcon("components/images/splashscreen.png");
            
            if (icon.getIconWidth() <= 0) {
                throw new Exception("Image not loaded");
            }
            
            Image scaledImage = icon.getImage().getScaledInstance(1200, 700, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            
            JLabel label = new JLabel(scaledIcon);
            content.add(label, BorderLayout.CENTER);
            
            pressLabel = new JLabel("Press any key to continue...", SwingConstants.CENTER);
            pressLabel.setForeground(Color.WHITE);
            
            // Try to load Garet font
            try {
                Font garetFont = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Garet-Book.ttf"));
                pressLabel.setFont(garetFont.deriveFont(Font.BOLD, 24f));
            } catch (FontFormatException | IOException e) {
                // Fallback to Arial if Garet not found
                pressLabel.setFont(new Font("Arial", Font.BOLD, 24));
                System.out.println("Garet font not found, using Arial");
            }
            
            pressLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
            pressLabel.setVisible(false);
            content.add(pressLabel, BorderLayout.SOUTH);
            
        } catch (Exception e) {
            System.out.println("Could not load splash image: " + e.getMessage());
            e.printStackTrace();
        }
        
        setContentPane(content);
        pack();
        setLocationRelativeTo(null);
        
        setBackground(new Color(0, 0, 0, 0));
        setOpacity(0.0f);
        setVisible(true);
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                fadeOut();
            }
        });
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                fadeOut();
            }
        });
        
        setFocusable(true);
        requestFocus();
        
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
                    showPressLabelAfterDelay();
                }
                setOpacity(opacity);
            }
        });
        fadeInTimer.start();
    }
    
    private void showPressLabelAfterDelay() {
        Timer delayTimer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((Timer) e.getSource()).stop();
                if (pressLabel != null) {
                    pressLabel.setVisible(true);
                }
            }
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
    }
    
    private void fadeOut() {
        for (KeyListener kl : getKeyListeners()) {
            removeKeyListener(kl);
        }
        for (MouseListener ml : getMouseListeners()) {
            removeMouseListener(ml);
        }
        
        Timer fadeOutTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity -= 0.05f;
                if (opacity <= 0.0f) {
                    opacity = 0.0f;
                    setOpacity(opacity);
                    ((Timer) e.getSource()).stop();
                    dispose();
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