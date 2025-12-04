package game2048;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

public class Constants {
    // Board settings
    public static final int GRID_SIZE = 5;
    public static final int TILE_SIZE = 75;
    public static final int TILE_MARGIN = 10;
    
    // Window settings
    public static final int WINDOW_WIDTH = 1120;
    public static final int WINDOW_HEIGHT = 630;
    
    // Game board positioning (centered on screen)
    public static final int BOARD_WIDTH = GRID_SIZE * (TILE_SIZE + TILE_MARGIN) + TILE_MARGIN;
    public static final int BOARD_HEIGHT = GRID_SIZE * (TILE_SIZE + TILE_MARGIN) + TILE_MARGIN;
    public static final int BOARD_X_OFFSET = 50; // Left margin for board
    public static final int BOARD_Y_OFFSET = 100; // Top margin for board
    
    // Colors
    public static final Color BACKGROUND_COLOR = new Color(187, 173, 160);
    public static final Color EMPTY_TILE_COLOR = new Color(205, 193, 180);
    public static final Color TEXT_DARK = new Color(119, 110, 101);
    public static final Color TEXT_LIGHT = new Color(249, 246, 242);
    public static final Color GRID_COLOR = Color.WHITE; // White grid lines
    
    // Tile colors map
    public static final Map<Integer, Color> TILE_COLORS = new HashMap<Integer, Color>() {{
        put(2, new Color(238, 228, 218));
        put(4, new Color(237, 224, 200));
        put(8, new Color(242, 177, 121));
        put(16, new Color(245, 149, 99));
        put(32, new Color(246, 124, 95));
        put(64, new Color(246, 94, 59));
        put(128, new Color(237, 207, 114));
        put(256, new Color(237, 204, 97));
        put(512, new Color(237, 200, 80));
        put(1024, new Color(237, 197, 63));
        put(2048, new Color(237, 194, 46));
        put(4096, new Color(60, 58, 50));
    }};
    
    // Font settings
    public static final Font TILE_FONT = new Font("Arial", Font.BOLD, 32);
    public static final Font SCORE_FONT = new Font("Arial", Font.BOLD, 24);
    public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 16);
    
    // Game settings
    public static final int WIN_VALUE = 2048;
    
    // Direction enum
    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}