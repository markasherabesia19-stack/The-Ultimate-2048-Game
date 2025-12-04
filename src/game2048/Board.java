package game2048;

import java.util.*;

public class Board {
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    
    private int size;
    private Tile[][] grid;
    private int score;
    private boolean hasWon;
    
    public Board(int size) {
        this.size = size;
        this.grid = new Tile[size][size];
        this.score = 0;
        this.hasWon = false;
    }
    
    public int getSize() {
        return size;
    }
    
    public Tile getTile(int row, int col) {
        if (row >= 0 && row < size && col >= 0 && col < size) {
            return grid[row][col];
        }
        return null;
    }
    
    public void setTile(int row, int col, Tile tile) {
        if (row >= 0 && row < size && col >= 0 && col < size) {
            grid[row][col] = tile;
        }
    }
    
    public int getScore() {
        return score;
    }
    
    public boolean hasWon() {
        return hasWon;
    }
    
    public void addRandomTile() {
        List<int[]> emptyCells = new ArrayList<>();
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (grid[row][col] == null) {
                    emptyCells.add(new int[]{row, col});
                }
            }
        }
        
        if (!emptyCells.isEmpty()) {
            int[] cell = emptyCells.get((int)(Math.random() * emptyCells.size()));
            int value = Math.random() < 0.9 ? 2 : 4;
            grid[cell[0]][cell[1]] = new Tile(value);
        }
    }
    
    public boolean move(int direction) {
        boolean moved = false;
        resetMergedFlags();
        
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
        
        return moved;
    }
    
    private void resetMergedFlags() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (grid[row][col] != null) {
                    grid[row][col].resetMerged();
                }
            }
        }
    }
    
    private boolean moveUp() {
        boolean moved = false;
        
        for (int col = 0; col < size; col++) {
            int writePos = 0;
            
            for (int row = 0; row < size; row++) {
                if (grid[row][col] != null) {
                    Tile current = grid[row][col];
                    
                    if (writePos > 0 && grid[writePos - 1][col] != null && 
                        grid[writePos - 1][col].getValue() == current.getValue() && 
                        !grid[writePos - 1][col].isMerged()) {
                        
                        grid[writePos - 1][col].doubleValue();
                        grid[writePos - 1][col].setMerged(true);
                        score += grid[writePos - 1][col].getValue();
                        
                        if (grid[writePos - 1][col].getValue() == 2048) {
                            hasWon = true;
                        }
                        
                        grid[row][col] = null;
                        moved = true;
                    } else {
                        if (row != writePos) {
                            grid[writePos][col] = current;
                            grid[row][col] = null;
                            moved = true;
                        }
                        writePos++;
                    }
                }
            }
        }
        
        return moved;
    }
    
    private boolean moveDown() {
        boolean moved = false;
        
        for (int col = 0; col < size; col++) {
            int writePos = size - 1;
            
            for (int row = size - 1; row >= 0; row--) {
                if (grid[row][col] != null) {
                    Tile current = grid[row][col];
                    
                    if (writePos < size - 1 && grid[writePos + 1][col] != null && 
                        grid[writePos + 1][col].getValue() == current.getValue() && 
                        !grid[writePos + 1][col].isMerged()) {
                        
                        grid[writePos + 1][col].doubleValue();
                        grid[writePos + 1][col].setMerged(true);
                        score += grid[writePos + 1][col].getValue();
                        
                        if (grid[writePos + 1][col].getValue() == 2048) {
                            hasWon = true;
                        }
                        
                        grid[row][col] = null;
                        moved = true;
                    } else {
                        if (row != writePos) {
                            grid[writePos][col] = current;
                            grid[row][col] = null;
                            moved = true;
                        }
                        writePos--;
                    }
                }
            }
        }
        
        return moved;
    }
    
    private boolean moveLeft() {
        boolean moved = false;
        
        for (int row = 0; row < size; row++) {
            int writePos = 0;
            
            for (int col = 0; col < size; col++) {
                if (grid[row][col] != null) {
                    Tile current = grid[row][col];
                    
                    if (writePos > 0 && grid[row][writePos - 1] != null && 
                        grid[row][writePos - 1].getValue() == current.getValue() && 
                        !grid[row][writePos - 1].isMerged()) {
                        
                        grid[row][writePos - 1].doubleValue();
                        grid[row][writePos - 1].setMerged(true);
                        score += grid[row][writePos - 1].getValue();
                        
                        if (grid[row][writePos - 1].getValue() == 2048) {
                            hasWon = true;
                        }
                        
                        grid[row][col] = null;
                        moved = true;
                    } else {
                        if (col != writePos) {
                            grid[row][writePos] = current;
                            grid[row][col] = null;
                            moved = true;
                        }
                        writePos++;
                    }
                }
            }
        }
        
        return moved;
    }
    
    private boolean moveRight() {
        boolean moved = false;
        
        for (int row = 0; row < size; row++) {
            int writePos = size - 1;
            
            for (int col = size - 1; col >= 0; col--) {
                if (grid[row][col] != null) {
                    Tile current = grid[row][col];
                    
                    if (writePos < size - 1 && grid[row][writePos + 1] != null && 
                        grid[row][writePos + 1].getValue() == current.getValue() && 
                        !grid[row][writePos + 1].isMerged()) {
                        
                        grid[row][writePos + 1].doubleValue();
                        grid[row][writePos + 1].setMerged(true);
                        score += grid[row][writePos + 1].getValue();
                        
                        if (grid[row][writePos + 1].getValue() == 2048) {
                            hasWon = true;
                        }
                        
                        grid[row][col] = null;
                        moved = true;
                    } else {
                        if (col != writePos) {
                            grid[row][writePos] = current;
                            grid[row][col] = null;
                            moved = true;
                        }
                        writePos--;
                    }
                }
            }
        }
        
        return moved;
    }
    
    public boolean isGameOver() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (grid[row][col] == null) {
                    return false;
                }
                
                if (col < size - 1 && grid[row][col + 1] != null && 
                    grid[row][col].getValue() == grid[row][col + 1].getValue()) {
                    return false;
                }
                
                if (row < size - 1 && grid[row + 1][col] != null && 
                    grid[row][col].getValue() == grid[row + 1][col].getValue()) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public boolean canMove(int direction) {
        Board testBoard = this.copy();
        return testBoard.move(direction);
    }
    
    public Board copy() {
        Board copy = new Board(this.size);
        copy.score = this.score;
        copy.hasWon = this.hasWon;
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (this.grid[row][col] != null) {
                    copy.grid[row][col] = this.grid[row][col].copy();
                }
            }
        }
        
        return copy;
    }
    
    public int getHighestTile() {
        int highest = 0;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (grid[row][col] != null && grid[row][col].getValue() > highest) {
                    highest = grid[row][col].getValue();
                }
            }
        }
        return highest;
    }
    
    public int getEmptyCellCount() {
        int count = 0;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (grid[row][col] == null) {
                    count++;
                }
            }
        }
        return count;
    }
}