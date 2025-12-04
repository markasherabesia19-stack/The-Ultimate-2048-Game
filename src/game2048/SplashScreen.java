package game2048;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {
    
    public SplashScreen() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.BLACK);
        
        // Try to load splash image
        try {
            ImageIcon icon = new ImageIcon("components/images/splashscreen.png");
            Image img = icon.getImage().getScaledInstance(1120, 630, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(img));
            content.add(imageLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            // Fallback to text if image not found
            JLabel textLabel = new JLabel("2048 GAME", SwingConstants.CENTER);
            textLabel.setForeground(Color.WHITE);
            textLabel.setFont(new Font("Arial", Font.BOLD, 72));
            content.add(textLabel, BorderLayout.CENTER);
        }
        
        setContentPane(content);
        setSize(1120, 630);
        setLocationRelativeTo(null);
        setVisible(true);
        
        // Show for 5 seconds then launch game
        Timer timer = new Timer(5000, e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new Game());
        });
        timer.setRepeats(false);
        timer.start();
    }
}