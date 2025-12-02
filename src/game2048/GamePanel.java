package game2048;

import java.awt.*;
import javax.swing.*;

public class GamePanel extends JPanel {
    private Board board;
    
    public GamePanel(Board board) {
        this.board = board;
        setBackground(Color.WHITE);
        setFocusable(true);
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
        
        // Draw grid lines
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        
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
            g2d.fillRect(x + 1, y + 1, Constants.TILE_SIZE - 2, Constants.TILE_SIZE - 2);
        }
        
        // Draw value text
        if (value != 0) {
            String text = String.valueOf(value);
            Font font = Constants.TILE_FONT;
            
            // Adjust font size for larger numbers
            if (text.length() > 3) {
                font = new Font("Arial", Font.BOLD, 28);
            } else if (text.length() > 2) {
                font = new Font("Arial", Font.BOLD, 32);
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
            int textY = y + (Constants.TILE_SIZE + textHeight) / 2 - 5;
            
            g2d.drawString(text, textX, textY);
        }
    }
}