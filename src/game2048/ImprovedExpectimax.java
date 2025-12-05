package game2048;

import java.util.*;

public class ImprovedExpectimax {
    private static final int SEARCH_DEPTH = 3;
    
    // Heuristic weights
    private static final double MONOTONICITY_WEIGHT = 1.0;
    private static final double SMOOTHNESS_WEIGHT = 0.1;
    private static final double EMPTY_WEIGHT = 2.7;
    private static final double MAX_TILE_WEIGHT = 1.0;
    
    private Board board;
    
    // Class to store move evaluation results
    public static class MoveEvaluation implements Comparable<MoveEvaluation> {
        public int direction;
        public double score;
        public String directionName;
        public String reasoning;
        
        public MoveEvaluation(int direction, double score, String directionName, String reasoning) {
            this.direction = direction;
            this.score = score;
            this.directionName = directionName;
            this.reasoning = reasoning;
        }
        
        @Override
        public int compareTo(MoveEvaluation other) {
            return Double.compare(other.score, this.score);
        }
    }
    
    public ImprovedExpectimax(Board board) {
        this.board = board;
    }
    
    public List<MoveEvaluation> getTopMoves() {
        List<MoveEvaluation> evaluations = new ArrayList<>();
        String[] directionNames = {"UP ↑", "DOWN ↓", "LEFT ←", "RIGHT →"};
        
        for (int direction : new int[]{Board.UP, Board.DOWN, Board.LEFT, Board.RIGHT}) {
            Board clonedBoard = board.copy();
            
            if (clonedBoard.move(direction)) {
                double score = expectimax(clonedBoard, SEARCH_DEPTH - 1, false);
                String reasoning = generateReasoning(board, clonedBoard, direction);
                
                evaluations.add(new MoveEvaluation(
                    direction, 
                    score, 
                    directionNames[direction],
                    reasoning
                ));
            }
        }
        
        Collections.sort(evaluations);
        
        return evaluations.subList(0, Math.min(3, evaluations.size()));
    }
    
    private String generateReasoning(Board original, Board afterMove, int direction) {
        List<String> reasons = new ArrayList<>();
        
        int mergeCount = countPotentialMerges(afterMove);
        if (mergeCount > 0) {
            reasons.add(mergeCount + " merge opportunity");
        }
        
        int emptyBefore = original.getEmptyCellCount();
        int emptyAfter = afterMove.getEmptyCellCount();
        if (emptyAfter > emptyBefore) {
            reasons.add("Creates " + (emptyAfter - emptyBefore) + " empty space");
        } else if (emptyAfter == emptyBefore) {
            reasons.add("Maintains empty space");
        }
        
        int maxTile = afterMove.getHighestTile();
        if (isMaxTileInCorner(afterMove, maxTile)) {
            reasons.add("Keeps " + maxTile + " in corner");
        }
        
        double monotonicity = calculateMonotonicity(afterMove);
        if (monotonicity > 15) {
            reasons.add("Good tile organization");
        }
        
        if (reasons.isEmpty()) {
            reasons.add("Standard move");
        }
        
        return String.join(" • ", reasons);
    }
    
    private int countPotentialMerges(Board board) {
        int count = 0;
        int size = board.getSize();
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Tile current = board.getTile(row, col);
                if (current != null) {
                    if (col < size - 1) {
                        Tile right = board.getTile(row, col + 1);
                        if (right != null && right.getValue() == current.getValue()) {
                            count++;
                        }
                    }
                    if (row < size - 1) {
                        Tile down = board.getTile(row + 1, col);
                        if (down != null && down.getValue() == current.getValue()) {
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }
    
    private boolean isMaxTileInCorner(Board board, int maxValue) {
        int size = board.getSize();
        int[][] corners = {{0, 0}, {0, size-1}, {size-1, 0}, {size-1, size-1}};
        
        for (int[] corner : corners) {
            Tile tile = board.getTile(corner[0], corner[1]);
            if (tile != null && tile.getValue() == maxValue) {
                return true;
            }
        }
        return false;
    }
    
    public String getFormattedSuggestion() {
        List<MoveEvaluation> topMoves = getTopMoves();
        
        if (topMoves.isEmpty()) {
            return "No valid moves available!\nGame Over";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("TOP MOVES\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━\n\n");
        
        String[] medals = {"🥇 BEST", "🥈 GOOD", "🥉 OKAY"};
        
        for (int i = 0; i < topMoves.size(); i++) {
            MoveEvaluation eval = topMoves.get(i);
            
            sb.append(medals[i]).append(": ").append(eval.directionName);
            sb.append(" (").append(String.format("%.1f", eval.score)).append(")\n");
            sb.append("   • ").append(eval.reasoning).append("\n");
            
            if (i < topMoves.size() - 1) {
                sb.append("\n");
            }
        }
        
        sb.append("\n");
        if (topMoves.size() >= 2) {
            double scoreDiff = topMoves.get(0).score - topMoves.get(1).score;
            if (scoreDiff > 500) {
                sb.append("💡 Best move is clearly superior!");
            } else if (scoreDiff > 200) {
                sb.append("💡 Best move is recommended");
            } else {
                sb.append("💡 Multiple good options available");
            }
        }
        
        return sb.toString();
    }
    
    private double expectimax(Board board, int depth, boolean isMaxNode) {
        if (depth == 0 || board.isGameOver()) {
            return evaluateBoard(board);
        }
        
        if (isMaxNode) {
            return maxNode(board, depth);
        } else {
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
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board.getTile(row, col) == null) {
                    emptyCount++;
                    
                    Board clonedBoard = board.copy();
                    clonedBoard.setTile(row, col, new Tile(2));
                    totalScore += 0.9 * expectimax(clonedBoard, depth - 1, true);
                    
                    clonedBoard = board.copy();
                    clonedBoard.setTile(row, col, new Tile(4));
                    totalScore += 0.1 * expectimax(clonedBoard, depth - 1, true);
                }
            }
        }
        
        return emptyCount == 0 ? evaluateBoard(board) : totalScore / emptyCount;
    }
    
    private double evaluateBoard(Board board) {
        double score = 0;
        
        score += calculateMonotonicity(board) * MONOTONICITY_WEIGHT;
        score += calculateSmoothness(board) * SMOOTHNESS_WEIGHT;
        score += board.getEmptyCellCount() * EMPTY_WEIGHT;
        score += maxTileInCorner(board) * MAX_TILE_WEIGHT;
        
        return score;
    }
    
    private double calculateMonotonicity(Board board) {
        double score = 0;
        int size = board.getSize();
        
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
    
    private double calculateSmoothness(Board board) {
        double score = 0;
        int size = board.getSize();
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Tile tile = board.getTile(row, col);
                if (tile != null) {
                    int value = tile.getValue();
                    
                    if (col < size - 1) {
                        Tile right = board.getTile(row, col + 1);
                        if (right != null) {
                            score -= Math.abs(Math.log(value) / Math.log(2) - 
                                            Math.log(right.getValue()) / Math.log(2));
                        }
                    }
                    
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
    
    private double maxTileInCorner(Board board) {
        int size = board.getSize();
        int maxValue = board.getHighestTile();
        
        boolean inCorner = false;
        
        if (board.getTile(0, 0) != null && board.getTile(0, 0).getValue() == maxValue) {
            inCorner = true;
        }
        if (board.getTile(0, size - 1) != null && board.getTile(0, size - 1).getValue() == maxValue) {
            inCorner = true;
        }
        if (board.getTile(size - 1, 0) != null && board.getTile(size - 1, 0).getValue() == maxValue) {
            inCorner = true;
        }
        if (board.getTile(size - 1, size - 1) != null && board.getTile(size - 1, size - 1).getValue() == maxValue) {
            inCorner = true;
        }
        
        return inCorner ? maxValue : 0;
    }
}