package game2048;

public class Expictimax {
    private static final int SEARCH_DEPTH = 3;
    
    // Heuristic weights for optimal performance
    private static final double MONOTONICITY_WEIGHT = 1.0;
    private static final double SMOOTHNESS_WEIGHT = 0.1;
    private static final double EMPTY_WEIGHT = 2.7;
    private static final double MAX_TILE_WEIGHT = 1.0;
    
    private Board board;
    
    public Expictimax(Board board) {
        this.board = board;
    }
    
    public String getBestMove() {
        int bestDirection = -1;
        double bestScore = -1;
        
        // Try all four directions
        for (int direction : new int[]{Board.UP, Board.DOWN, Board.LEFT, Board.RIGHT}) {
            Board clonedBoard = board.copy();
            
            if (clonedBoard.move(direction)) {
                double score = expectimax(clonedBoard, SEARCH_DEPTH - 1, false);
                
                if (score > bestScore) {
                    bestScore = score;
                    bestDirection = direction;
                }
            }
        }
        
        return directionToString(bestDirection);
    }
    
    public String getBestMoveSequence(int numMoves) {
        StringBuilder sequence = new StringBuilder();
        Board simulatedBoard = board.copy();
        
        sequence.append("Strategy Path:\n");
        
        for (int i = 0; i < numMoves; i++) {
            Expictimax tempAI = new Expictimax(simulatedBoard);
            
            int bestDirection = -1;
            double bestScore = -1;
            
            // Find best move for current board state
            for (int direction : new int[]{Board.UP, Board.DOWN, Board.LEFT, Board.RIGHT}) {
                Board clonedBoard = simulatedBoard.copy();
                
                if (clonedBoard.move(direction)) {
                    double score = tempAI.expectimax(clonedBoard, SEARCH_DEPTH - 1, false);
                    
                    if (score > bestScore) {
                        bestScore = score;
                        bestDirection = direction;
                    }
                }
            }
            
            // If no valid move found, stop
            if (bestDirection == -1) {
                sequence.append("\n✓ Sequence complete!");
                break;
            }
            
            // Add move to sequence
            String moveStr = directionToArrow(bestDirection);
            if (i == 0) {
                sequence.append(moveStr);
            } else {
                sequence.append(" → ").append(moveStr);
            }
            
            // Simulate the move on our test board
            simulatedBoard.move(bestDirection);
            simulatedBoard.addRandomTile(); // Simulate random tile spawn
            
            // Check if game would be over
            if (simulatedBoard.isGameOver()) {
                sequence.append("\n✓ Max sequence reached!");
                break;
            }
        }
        
        return sequence.toString();
    }
    
    private String directionToArrow(int direction) {
        switch (direction) {
            case Board.UP: return "UP";
            case Board.DOWN: return "DOWN";
            case Board.LEFT: return "LEFT";
            case Board.RIGHT: return "RIGHT";
            default: return "?";
        }
    }

    private double expectimax(Board board, int depth, boolean isMaxNode) {
        if (depth == 0 || board.isGameOver()) {
            return evaluateBoard(board);
        }
        
        if (isMaxNode) {
            // MAX node (player's turn)
            return maxNode(board, depth);
        } else {
            // CHANCE node (random tile spawn)
            return chanceNode(board, depth);
        }
    }
    
    private double maxNode(Board board, int depth) {
        double maxScore = -1;
        
        for (int direction : new int[]{Board.UP, Board.DOWN, Board.LEFT, Board.RIGHT}) {
            Board clonedBoard = board.copy();
            
            if (clonedBoard.move(direction)) {
                double score = expectimax(clonedBoard, depth - 1, false);
                maxScore = Math.max(maxScore, score);
            }
        }
        
        return maxScore == -1 ? evaluateBoard(board) : maxScore;
    }
    
    private double chanceNode(Board board, int depth) {
        double totalScore = 0;
        int emptyCount = 0;
        int size = board.getSize();
        
        // Find all empty cells
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board.getTile(row, col) == null) {
                    emptyCount++;
                    
                    // Simulate spawning a 2 (90% probability)
                    Board clonedBoard = board.copy();
                    clonedBoard.setTile(row, col, new Tile(2));
                    totalScore += 0.9 * expectimax(clonedBoard, depth - 1, true);
                    
                    // Simulate spawning a 4 (10% probability)
                    clonedBoard = board.copy();
                    clonedBoard.setTile(row, col, new Tile(4));
                    totalScore += 0.1 * expectimax(clonedBoard, depth - 1, true);
                }
            }
        }
        
        return emptyCount == 0 ? evaluateBoard(board) : totalScore / emptyCount;
    }
    
    // Comprehensive board evaluation using multiple heuristics
    private double evaluateBoard(Board board) {
        double score = 0;
        
        score += calculateMonotonicity(board) * MONOTONICITY_WEIGHT;
        score += calculateSmoothness(board) * SMOOTHNESS_WEIGHT;
        score += board.getEmptyCellCount() * EMPTY_WEIGHT;
        score += maxTileInCorner(board) * MAX_TILE_WEIGHT;
        
        return score;
    }
    
    
    // Monotonicity: Prefers tiles arranged in increasing/decreasing order
    private double calculateMonotonicity(Board board) {
        double score = 0;
        int size = board.getSize();
        
        // Check rows
        for (int row = 0; row < size; row++) {
            int increasing = 0, decreasing = 0;
            for (int col = 0; col < size - 1; col++) {
                Tile current = board.getTile(row, col);
                Tile next = board.getTile(row, col + 1);
                
                if (current != null && next != null) {
                    if (current.getValue() <= next.getValue()) increasing++;
                    if (current.getValue() >= next.getValue()) decreasing++;
                }
            }
            score += Math.max(increasing, decreasing);
        }
        
        // Check columns
        for (int col = 0; col < size; col++) {
            int increasing = 0, decreasing = 0;
            for (int row = 0; row < size - 1; row++) {
                Tile current = board.getTile(row, col);
                Tile next = board.getTile(row + 1, col);
                
                if (current != null && next != null) {
                    if (current.getValue() <= next.getValue()) increasing++;
                    if (current.getValue() >= next.getValue()) decreasing++;
                }
            }
            score += Math.max(increasing, decreasing);
        }
        
        return score;
    }
    
    // Smoothness: Prefers adjacent tiles with similar values
    private double calculateSmoothness(Board board) {
        double score = 0;
        int size = board.getSize();
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Tile tile = board.getTile(row, col);
                if (tile != null) {
                    int value = tile.getValue();
                    
                    // Check right neighbor
                    if (col < size - 1) {
                        Tile right = board.getTile(row, col + 1);
                        if (right != null) {
                            score -= Math.abs(Math.log(value) / Math.log(2) - 
                                            Math.log(right.getValue()) / Math.log(2));
                        }
                    }
                    
                    // Check down neighbor
                    if (row < size - 1) {
                        Tile down = board.getTile(row + 1, col);
                        if (down != null) {
                            score -= Math.abs(Math.log(value) / Math.log(2) - 
                                            Math.log(down.getValue()) / Math.log(2));
                        }
                    }
                }
            }
        }
        
        return score;
    }
    
    // Max tile position: Prefers maximum tile in a corner
    private double maxTileInCorner(Board board) {
        int size = board.getSize();
        int maxValue = board.getHighestTile();
        
        // Check if max tile is in any corner
        boolean inCorner = false;
        
        // Top-left
        if (board.getTile(0, 0) != null && board.getTile(0, 0).getValue() == maxValue) {
            inCorner = true;
        }
        // Top-right
        if (board.getTile(0, size - 1) != null && board.getTile(0, size - 1).getValue() == maxValue) {
            inCorner = true;
        }
        // Bottom-left
        if (board.getTile(size - 1, 0) != null && board.getTile(size - 1, 0).getValue() == maxValue) {
            inCorner = true;
        }
        // Bottom-right
        if (board.getTile(size - 1, size - 1) != null && board.getTile(size - 1, size - 1).getValue() == maxValue) {
            inCorner = true;
        }
        
        return inCorner ? maxValue : 0;
    }
    
    // Convert direction to user-friendly string
    private String directionToString(int direction) {
        switch (direction) {
            case Board.UP: 
                return "Suggestion: Move UP \nBest strategic move to maximize your score!";
            case Board.DOWN: 
                return "Suggestion: Move DOWN \nBest strategic move to maximize your score!";
            case Board.LEFT: 
                return "Suggestion: Move LEFT \nBest strategic move to maximize your score!";
            case Board.RIGHT: 
                return "Suggestion: Move RIGHT \nBest strategic move to maximize your score!";
            default: 
                return "No valid moves available!\nGame Over - Try starting a new game.";
        }
    }
}