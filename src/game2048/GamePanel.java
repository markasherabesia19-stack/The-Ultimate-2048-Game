package game2048;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private Board board;
    
    public GamePanel(Board board) {
        this.board = board;
        setBackground(Constants.BACKGROUND_COLOR);
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
        Color tileColor;
        if (value == 0) {
            tileColor = Constants.EMPTY_TILE_COLOR;
        } else {
            tileColor = Constants.TILE_COLORS.getOrDefault(value, new Color(60, 58, 50));
        }
        
        g2d.setColor(tileColor);
        g2d.fillRoundRect(x, y, Constants.TILE_SIZE, Constants.TILE_SIZE, 10, 10);
        
        // Draw value text
        if (value != 0) {
            String text = String.valueOf(value);
            Font font = Constants.TILE_FONT;
            
            // Adjust font size for larger numbers
            if (text.length() > 3) {
                font = new Font("Arial", Font.BOLD, 28);
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