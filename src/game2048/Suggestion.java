package game2048;

/**
 * Provides AI-powered move suggestions for the 2048 game.
 * Uses heuristic evaluation to determine the best move based on board state.
 * This class is superseded by Expectimax for better suggestions.
 */
public class Suggestion {
    
    public static String getBestMove(Board board) {
        if (board == null || board.isGameOver()) {
            return "No moves available - Game Over!";
        }
        
        int bestDirection = -1;
        int bestScore = -1;
        String[] directionNames = {"UP ↑", "DOWN ↓", "LEFT ←", "RIGHT →"};
        
        // Try all four directions and evaluate each resulting board state
        for (int direction = 0; direction < 4; direction++) {
            Board testBoard = board.copy();
            
            if (testBoard.move(direction)) {
                // Evaluate how good this board state is using heuristics
                int moveScore = evaluateBoard(testBoard);
                
                // Keep track of the best move found so far
                if (moveScore > bestScore) {
                    bestScore = moveScore;
                    bestDirection = direction;
                }
            }
        }
        
        if (bestDirection == -1) {
            return "No valid moves available!";
        }
        
        return "Suggested move: " + directionNames[bestDirection] + 
               " (Score potential: " + bestScore + ")";
    }
    
    // Evaluates board state using multiple heuristics to determine move quality
    private static int evaluateBoard(Board board) {
        int score = 0;
        
        // Current game score (high priority)
        score += board.getScore() * 2;
        
        // Empty cells (more space = better)
        score += board.getEmptyCellCount() * 100;
        
        // Highest tile value (reward progress)
        score += board.getHighestTile() * 10;
        
        // Monotonicity (tiles ordered in sequence)
        score += evaluateMonotonicity(board) * 50;
        
        // Smoothness (similar adjacent tiles)
        score += evaluateSmoothness(board) * 30;
        
        // Corner strategy (keep high tiles in corners)
        score += evaluateCornerStrategy(board) * 80;
        
        return score;
    }
    
    // Evaluates if tiles are arranged in increasing or decreasing order
    // Higher score means tiles follow a clear pattern
    private static int evaluateMonotonicity(Board board) {
        int size = board.getSize();
        int monotonicity = 0;
        
        // Check each row for monotonic pattern
        for (int row = 0; row < size; row++) {
            boolean increasing = true;
            boolean decreasing = true;
            
            for (int col = 0; col < size - 1; col++) {
                Tile current = board.getTile(row, col);
                Tile next = board.getTile(row, col + 1);
                
                if (current != null && next != null) {
                    if (current.getValue() > next.getValue()) {
                        increasing = false;
                    }
                    if (current.getValue() < next.getValue()) {
                        decreasing = false;
                    }
                }
            }
            
            if (increasing || decreasing) monotonicity += 10;
        }
        
        // Check each column for monotonic pattern
        for (int col = 0; col < size; col++) {
            boolean increasing = true;
            boolean decreasing = true;
            
            for (int row = 0; row < size - 1; row++) {
                Tile current = board.getTile(row, col);
                Tile next = board.getTile(row + 1, col);
                
                if (current != null && next != null) {
                    if (current.getValue() > next.getValue()) {
                        increasing = false;
                    }
                    if (current.getValue() < next.getValue()) {
                        decreasing = false;
                    }
                }
            }
            
            if (increasing || decreasing) monotonicity += 10;
        }
        
        return monotonicity;
    }
    
    // Evaluates how similar adjacent tiles are
    // Lower differences between neighbors = higher score (easier to merge)
    private static int evaluateSmoothness(Board board) {
        int size = board.getSize();
        int smoothness = 0;
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Tile current = board.getTile(row, col);
                if (current != null) {
                    int value = current.getValue();
                    
                    // Check right neighbor
                    if (col < size - 1) {
                        Tile right = board.getTile(row, col + 1);
                        if (right != null) {
                            int diff = Math.abs(value - right.getValue());
                            smoothness -= diff;
                        }
                    }
                    
                    // Check bottom neighbor
                    if (row < size - 1) {
                        Tile down = board.getTile(row + 1, col);
                        if (down != null) {
                            int diff = Math.abs(value - down.getValue());
                            smoothness -= diff; 
                        }
                    }
                }
            }
        }
        
        return smoothness;
    }
    
    // Rewards keeping the highest tile in a corner
    // This is a key strategy in 2048 - keep high tiles in corners and build around them
    private static int evaluateCornerStrategy(Board board) {
        int score = 0;
        int size = board.getSize();
        int highestValue = board.getHighestTile();
        
        int[][] corners = {{0, 0}, {0, size-1}, {size-1, 0}, {size-1, size-1}};
        
        // Check if highest tile is in any corner
        for (int[] corner : corners) {
            Tile tile = board.getTile(corner[0], corner[1]);
            if (tile != null && tile.getValue() == highestValue) {
                score += 100; 
                
                if (corner[0] == 0 || corner[0] == size - 1) {
                    for (int col = 0; col < size; col++) {
                        Tile edgeTile = board.getTile(corner[0], col);
                        if (edgeTile != null) {
                            score += edgeTile.getValue() / 10;
                        }
                    }
                }
                
                if (corner[1] == 0 || corner[1] == size - 1) {
                    for (int row = 0; row < size; row++) {
                        Tile edgeTile = board.getTile(row, corner[1]);
                        if (edgeTile != null) {
                            score += edgeTile.getValue() / 10;
                        }
                    }
                }
                
                break; // Found the highest tile in a corner, stop checking
            }
        }
        
        return score;
    }
    
    public static int[] findHighestTilePosition(Board board) {
        int size = board.getSize();
        int maxValue = 0;
        int[] position = new int[]{-1, -1};
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Tile tile = board.getTile(row, col);
                if (tile != null && tile.getValue() > maxValue) {
                    maxValue = tile.getValue();
                    position[0] = row;
                    position[1] = col;
                }
            }
        }
        
        return position;
    }
    
    public static int countMergePossibilities(Board board, int direction) {
        Board testBoard = board.copy();
        int size = board.getSize();
        int mergeCount = 0;
        
        if (direction == Board.UP || direction == Board.DOWN) {
            for (int col = 0; col < size; col++) {
                for (int row = 0; row < size - 1; row++) {
                    Tile current = board.getTile(row, col);
                    Tile next = board.getTile(row + 1, col);
                    if (current != null && next != null && 
                        current.getValue() == next.getValue()) {
                        mergeCount++;
                    }
                }
            }
        } else {
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size - 1; col++) {
                    Tile current = board.getTile(row, col);
                    Tile next = board.getTile(row, col + 1);
                    if (current != null && next != null && 
                        current.getValue() == next.getValue()) {
                        mergeCount++;
                    }
                }
            }
        }
        
        return mergeCount;
    }
}