package game2048;

import java.util.ArrayList;
import java.util.List;

public class Suggestion {
    private Board board;
    
    public Suggestion
    (Board board) {
        this.board = board;
    }
    
    /**
     * Find the best move based on creating a merge equal to the largest value
     * @return Direction to move, or null if no good move found
     */
    public Constants.Direction findBestMove() {
        int largestValue = board.getLargestValue();
        
        if (largestValue == 0) {
            return null; // Empty board
        }
        
        // Try each direction and see which creates the target merge
        Constants.Direction[] directions = Constants.Direction.values();
        List<Constants.Direction> validMoves = new ArrayList<>();
        
        for (Constants.Direction direction : directions) {
            if (createsMerge(direction, largestValue)) {
                validMoves.add(direction);
            }
        }
        
        // If we found moves that create the target merge, return the first one
        if (!validMoves.isEmpty()) {
            return validMoves.get(0);
        }
        
        // If no move creates the target merge, suggest any valid move
        for (Constants.Direction direction : directions) {
            if (isValidMove(direction)) {
                return direction;
            }
        }
        
        return null; // No valid moves
    }
    
    /**
     * Check if a move creates a merge equal to the target value
     */
    private boolean createsMerge(Constants.Direction direction, int targetValue) {
        // Create a simulation board
        Board simulationBoard = new Board(board.getSize());
        simulationBoard.setGrid(board.copyGrid(board.getGrid()));
        
        int[][] beforeGrid = simulationBoard.copyGrid(simulationBoard.getGrid());
        
        // Simulate the move without adding random tile
        boolean moved = simulateMove(simulationBoard, direction);
        
        if (!moved) {
            return false; // Move not valid
        }
        
        int[][] afterGrid = simulationBoard.getGrid();
        
        // Check if any merge created the target value
        return hasMergedTo(beforeGrid, afterGrid, targetValue);
    }
    
    /**
     * Check if the move from beforeGrid to afterGrid created a tile with targetValue
     * by merging (not just moving an existing tile)
     */
    private boolean hasMergedTo(int[][] before, int[][] after, int targetValue) {
        int size = before.length;
        
        // Count occurrences of targetValue before and after
        int countBefore = 0;
        int countAfter = 0;
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (before[i][j] == targetValue) {
                    countBefore++;
                }
                if (after[i][j] == targetValue) {
                    countAfter++;
                }
            }
        }
        
        // If count increased, a merge created a new instance of targetValue
        return countAfter > countBefore;
    }
    
    /**
     * Simulate a move without adding a random tile
     */
    private boolean simulateMove(Board simBoard, Constants.Direction direction) {
        int[][] oldGrid = simBoard.copyGrid(simBoard.getGrid());
        
        switch (direction) {
            case UP:
                simulateMoveUp(simBoard);
                break;
            case DOWN:
                simulateMoveDown(simBoard);
                break;
            case LEFT:
                simulateMoveLeft(simBoard);
                break;
            case RIGHT:
                simulateMoveRight(simBoard);
                break;
        }
        
        // Check if board changed
        return !gridsEqual(oldGrid, simBoard.getGrid());
    }
    
    private void simulateMoveLeft(Board simBoard) {
        int size = simBoard.getSize();
        int[][] grid = simBoard.getGrid();
        
        for (int i = 0; i < size; i++) {
            int[] row = grid[i];
            int[] newRow = mergeLine(row);
            grid[i] = newRow;
        }
    }
    
    private void simulateMoveRight(Board simBoard) {
        int size = simBoard.getSize();
        int[][] grid = simBoard.getGrid();
        
        for (int i = 0; i < size; i++) {
            int[] row = grid[i];
            int[] reversed = reverse(row);
            int[] merged = mergeLine(reversed);
            int[] newRow = reverse(merged);
            grid[i] = newRow;
        }
    }
    
    private void simulateMoveUp(Board simBoard) {
        transpose(simBoard);
        simulateMoveLeft(simBoard);
        transpose(simBoard);
    }
    
    private void simulateMoveDown(Board simBoard) {
        transpose(simBoard);
        simulateMoveRight(simBoard);
        transpose(simBoard);
    }
    
    private int[] mergeLine(int[] line) {
        int size = line.length;
        int[] result = new int[size];
        int position = 0;
        
        // Move all non-zero values to the left
        for (int i = 0; i < size; i++) {
            if (line[i] != 0) {
                result[position++] = line[i];
            }
        }
        
        // Merge adjacent equal values
        for (int i = 0; i < size - 1; i++) {
            if (result[i] != 0 && result[i] == result[i + 1]) {
                result[i] *= 2;
                result[i + 1] = 0;
            }
        }
        
        // Move non-zero values to the left again
        int[] finalResult = new int[size];
        position = 0;
        for (int i = 0; i < size; i++) {
            if (result[i] != 0) {
                finalResult[position++] = result[i];
            }
        }
        
        return finalResult;
    }
    
    private void transpose(Board simBoard) {
        int size = simBoard.getSize();
        int[][] grid = simBoard.getGrid();
        
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                int temp = grid[i][j];
                grid[i][j] = grid[j][i];
                grid[j][i] = temp;
            }
        }
    }
    
    private int[] reverse(int[] array) {
        int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[array.length - 1 - i];
        }
        return result;
    }
    
    private boolean gridsEqual(int[][] a, int[][] b) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                if (a[i][j] != b[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean isValidMove(Constants.Direction direction) {
        Board simulationBoard = new Board(board.getSize());
        simulationBoard.setGrid(board.copyGrid(board.getGrid()));
        return simulateMove(simulationBoard, direction);
    }
    
    /**
     * Get areadable suggestion message
     */
    public String getSuggestionMessage() {
        Constants.Direction bestMove = findBestMove();
        
        if (bestMove == null) {
            return "No valid moves available!";
        }
        
        int largestValue = board.getLargestValue();
        String directionText = "";
        
        switch (bestMove) {
            case UP:
                directionText = "UP (↑)";
                break;
            case DOWN:
                directionText = "DOWN (↓)";
                break;
            case LEFT:
                directionText = "LEFT (←)";
                break;
            case RIGHT:
                directionText = "RIGHT (→)";
                break;
        }
        
        return "Suggestion: Move " + directionText + "\n(Targeting merge to " + largestValue + ")";
    }
}