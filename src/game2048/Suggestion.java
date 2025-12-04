package game2048;

public class Suggestion {
    
    public static String getBestMove(Board board) {
        if (board == null || board.isGameOver()) {
            return "No moves available - Game Over!";
        }
        
        int bestDirection = -1;
        int bestScore = -1;
        String[] directionNames = {"UP ↑", "DOWN ↓", "LEFT ←", "RIGHT →"};
        
        for (int direction = 0; direction < 4; direction++) {
            Board testBoard = board.copy();
            
            if (testBoard.move(direction)) {
                int moveScore = evaluateBoard(testBoard);
                
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
    
    private static int evaluateBoard(Board board) {
        int score = 0;
        
        score += board.getScore() * 2;
        
        score += board.getEmptyCellCount() * 100;
        
        score += board.getHighestTile() * 10;
        
        score += evaluateMonotonicity(board) * 50;
        
        score += evaluateSmoothness(board) * 30;
        
        score += evaluateCornerStrategy(board) * 80;
        
        return score;
    }
    
    private static int evaluateMonotonicity(Board board) {
        int size = board.getSize();
        int monotonicity = 0;
        
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
    
    private static int evaluateSmoothness(Board board) {
        int size = board.getSize();
        int smoothness = 0;
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Tile current = board.getTile(row, col);
                if (current != null) {
                    int value = current.getValue();
                    
                    if (col < size - 1) {
                        Tile right = board.getTile(row, col + 1);
                        if (right != null) {
                            int diff = Math.abs(value - right.getValue());
                            smoothness -= diff;
                        }
                    }
                    
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
    
    private static int evaluateCornerStrategy(Board board) {
        int score = 0;
        int size = board.getSize();
        int highestValue = board.getHighestTile();
        
        int[][] corners = {{0, 0}, {0, size-1}, {size-1, 0}, {size-1, size-1}};
        
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
                
                break;
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