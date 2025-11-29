package game2048;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {
    private int[][] grid;
    private int size;
    private Random random;
    
    public Board(int size) {
        this.size = size;
        this.grid = new int[size][size];
        this.random = new Random();
        initializeBoard();
    }
    
    private void initializeBoard() {
        // Clear the board
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = 0;
            }
        }
        // Add two initial tiles
        addRandomTile();
        addRandomTile();
    }
    
    public void addRandomTile() {
        List<int[]> emptyPositions = getEmptyPositions();
        if (emptyPositions.isEmpty()) {
            return;
        }
        
        int[] position = emptyPositions.get(random.nextInt(emptyPositions.size()));
        // 90% chance of 2, 10% chance of 4
        int value = random.nextInt(10) < 9 ? 2 : 4;
        grid[position[0]][position[1]] = value;
    }
    
    private List<int[]> getEmptyPositions() {
        List<int[]> emptyPositions = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == 0) {
                    emptyPositions.add(new int[]{i, j});
                }
            }
        }
        return emptyPositions;
    }
    
    public boolean move(Constants.Direction direction) {
        int[][] oldGrid = copyGrid(grid);
        boolean moved = false;
        
        switch (direction) {
            case UP:
                moved = moveUp();
                break;
            case DOWN:
                moved = moveDown();
                break;
            case LEFT:
                moved = moveLeft();
                break;
            case RIGHT:
                moved = moveRight();
                break;
        }
        
        // Check if board changed
        if (moved) {
            addRandomTile();
        }
        
        return moved;
    }
    
    private boolean moveLeft() {
        boolean moved = false;
        for (int i = 0; i < size; i++) {
            int[] row = grid[i];
            int[] newRow = mergeLine(row);
            grid[i] = newRow;
            if (!arraysEqual(row, newRow)) {
                moved = true;
            }
        }
        return moved;
    }
    
    private boolean moveRight() {
        boolean moved = false;
        for (int i = 0; i < size; i++) {
            int[] row = grid[i];
            int[] reversed = reverse(row);
            int[] merged = mergeLine(reversed);
            int[] newRow = reverse(merged);
            grid[i] = newRow;
            if (!arraysEqual(row, newRow)) {
                moved = true;
            }
        }
        return moved;
    }
    
    private boolean moveUp() {
        transpose();
        boolean moved = moveLeft();
        transpose();
        return moved;
    }
    
    private boolean moveDown() {
        transpose();
        boolean moved = moveRight();
        transpose();
        return moved;
    }
    
    private int[] mergeLine(int[] line) {
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
    
    private void transpose() {
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
    
    private boolean arraysEqual(int[] a, int[] b) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) return false;
        }
        return true;
    }
    
    public int[][] getGrid() {
        return grid;
    }
    
    public int[][] copyGrid(int[][] source) {
        int[][] copy = new int[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(source[i], 0, copy[i], 0, size);
        }
        return copy;
    }
    
    public void setGrid(int[][] newGrid) {
        this.grid = copyGrid(newGrid);
    }
    
    public boolean hasEmptyTile() {
        return !getEmptyPositions().isEmpty();
    }
    
    public boolean canMove() {
        // Check if there are empty tiles
        if (hasEmptyTile()) {
            return true;
        }
        
        // Check if adjacent tiles can merge
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int current = grid[i][j];
                // Check right
                if (j < size - 1 && current == grid[i][j + 1]) {
                    return true;
                }
                // Check down
                if (i < size - 1 && current == grid[i + 1][j]) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public int getLargestValue() {
        int max = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] > max) {
                    max = grid[i][j];
                }
            }
        }
        return max;
    }
    
    public int getSize() {
        return size;
    }
    
    public void reset() {
        initializeBoard();
    }
}