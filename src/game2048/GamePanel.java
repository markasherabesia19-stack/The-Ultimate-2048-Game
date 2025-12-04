package game2048;

import java.awt.*;
import javax.swing.*;

public class GamePanel extends JPanel {
    private Board board;
    private Image backgroundImage;
    
    public GamePanel(Board board) {
        this.board = board;
        setBackground(new Color(0, 0, 0, 0));
        setFocusable(true);
        
        try {
            ImageIcon bgIcon = new ImageIcon("components/images/background.png");
            backgroundImage = bgIcon.getImage();
        } catch (Exception e) {
            System.out.println("Could not load background image: " + e.getMessage());
            backgroundImage = null;
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        drawBoard(g2d);
    }
    
    private void drawBoard(Graphics2D g2d) {
        int[][] grid = board.getGrid();
        int size = board.getSize();
        
        // Draw white grid lines - thinner for smaller tiles
        g2d.setColor(Constants.GRID_COLOR);
        g2d.setStroke(new BasicStroke(2)); // Thinner lines (was 3)
        
        for (int i = 0; i <= size; i++) {
            int pos = i * (Constants.TILE_SIZE + Constants.TILE_MARGIN) + Constants.TILE_MARGIN;
            // Vertical lines
            g2d.drawLine(Constants.TILE_MARGIN, pos, 
                        size * (Constants.TILE_SIZE + Constants.TILE_MARGIN) + Constants.TILE_MARGIN, pos);
            // Horizontal lines
            g2d.drawLine(pos, Constants.TILE_MARGIN, 
                        pos, size * (Constants.TILE_SIZE + Constants.TILE_MARGIN) + Constants.TILE_MARGIN);
        }
        
        // Draw tiles
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int x = j * (Constants.TILE_SIZE + Constants.TILE_MARGIN) + Constants.TILE_MARGIN;
                int y = i * (Constants.TILE_SIZE + Constants.TILE_MARGIN) + Constants.TILE_MARGIN;
                
                drawTile(g2d, grid[i][j], x, y);
            }
        }
    }
    
    private void drawTile(Graphics2D g2d, int value, int x, int y) {
        // Draw tile background
        if (value != 0) {
            Color tileColor = Constants.TILE_COLORS.getOrDefault(value, new Color(60, 58, 50));
            g2d.setColor(tileColor);
            g2d.fillRect(x + 2, y + 2, Constants.TILE_SIZE - 4, Constants.TILE_SIZE - 4);
        }
        
        // Draw value text
        if (value != 0) {
            String text = String.valueOf(value);
            Font font = Constants.TILE_FONT;
            
            // Adjust font size for larger numbers - scaled down
            if (text.length() > 3) {
                font = new Font("Arial", Font.BOLD, 24); // Was 45
            } else if (text.length() > 2) {
                font = new Font("Arial", Font.BOLD, 28); // Was 52
            }
            
            g2d.setFont(font);
            
            // Choose text color based on tile value
            if (value <= 4) {
                g2d.setColor(Constants.TEXT_DARK);
            } else {
                g2d.setColor(Constants.TEXT_LIGHT);
            }
            
            // Center the text
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            
            int textX = x + (Constants.TILE_SIZE - textWidth) / 2;
            int textY = y + (Constants.TILE_SIZE + textHeight) / 2 - 3; // Adjusted offset
            
            g2d.drawString(text, textX, textY);
        }
    }
}