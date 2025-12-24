package game2048;

import java.io.*;
import java.util.*;

/**
 * LeaderboardManager handles loading, saving, and managing the game's leaderboard data.
 * Stores the top 10 player scores with their names, scores, highest tiles, and timestamps in a serialized file.
 */
public class LeaderboardManager {
    private static final String LEADERBOARD_FILE = "leaderboard.dat";
    private static final int MAX_ENTRIES = 10;
    
    public static class LeaderboardEntry implements Serializable, Comparable<LeaderboardEntry> {
        private static final long serialVersionUID = 1L;
        
        String playerName;
        int score;
        int highestTile;
        long timestamp;
        
        public LeaderboardEntry(String playerName, int score, int highestTile) {
            this.playerName = playerName;
            this.score = score;
            this.highestTile = highestTile;
            this.timestamp = System.currentTimeMillis();
        }
        
        @Override
        public int compareTo(LeaderboardEntry other) {
            // Sort by score (descending), then by highest tile (descending)
            if (this.score != other.score) {
                return Integer.compare(other.score, this.score);
            }
            return Integer.compare(other.highestTile, this.highestTile);
        }
    }
    
    // Load leaderboard from file
    public static List<LeaderboardEntry> loadLeaderboard() {
        File file = new File(LEADERBOARD_FILE);
        
        System.out.println("=== LOADING LEADERBOARD ===");
        System.out.println("File path: " + file.getAbsolutePath());
        System.out.println("File exists: " + file.exists());
        
        if (!file.exists()) {
            System.out.println("No leaderboard file found. Returning empty list.");
            return new ArrayList<>();
        }
        
        System.out.println("File size: " + file.length() + " bytes");
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            @SuppressWarnings("unchecked")
            List<LeaderboardEntry> entries = (List<LeaderboardEntry>) ois.readObject();
            System.out.println("Leaderboard loaded successfully: " + entries.size() + " entries");
           
            for (int i = 0; i < entries.size(); i++) {
                LeaderboardEntry e = entries.get(i);
                System.out.println("  " + (i+1) + ". " + e.playerName + " - " + e.score + " - Tile: " + e.highestTile);
            }
            
            return entries;
        } catch (EOFException e) {
            System.out.println("Leaderboard file is empty or corrupted. Creating new.");
            file.delete();
            return new ArrayList<>();
        } catch (Exception e) {
            System.out.println("Error loading leaderboard: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // Save leaderboard to file
    public static void saveLeaderboard(List<LeaderboardEntry> entries) {
        System.out.println("=== SAVING LEADERBOARD ===");
        System.out.println("Entries to save: " + entries.size());
        
        for (int i = 0; i < entries.size(); i++) {
            LeaderboardEntry e = entries.get(i);
            System.out.println("  " + (i+1) + ". " + e.playerName + " - " + e.score + " - Tile: " + e.highestTile);
        }
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LEADERBOARD_FILE))) {
            oos.writeObject(entries);
            oos.flush();
            System.out.println("Leaderboard saved successfully!");
            
            // Verify the file was created
            File file = new File(LEADERBOARD_FILE);
            System.out.println("File size after save: " + file.length() + " bytes");
            
        } catch (Exception e) {
            System.out.println("Error saving leaderboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Add a new entry to the leaderboard
    public static int addEntry(String playerName, int score, int highestTile) {
        System.out.println("\n=== ADDING ENTRY TO LEADERBOARD ===");
        System.out.println("Player: " + playerName);
        System.out.println("Score: " + score);
        System.out.println("Highest Tile: " + highestTile);
    
        List<LeaderboardEntry> entries = loadLeaderboard();
        System.out.println("Current entries before adding: " + entries.size());
    
        LeaderboardEntry newEntry = new LeaderboardEntry(playerName, score, highestTile);
        
        // Store the timestamp for accurate rank finding
        long entryTimestamp = newEntry.timestamp;
        
        entries.add(newEntry);
        System.out.println("Entry added. Total entries: " + entries.size());
    
        // Sort entries
        Collections.sort(entries);
        System.out.println("Entries sorted.");
    
        // Find rank before trimming
        int rank = -1;
        for (int i = 0; i < entries.size(); i++) {
            LeaderboardEntry entry = entries.get(i);
            // Match by name, score, and timestamp (within 1 second)
            if (entry.playerName.equals(playerName) && 
                entry.score == score && 
                entry.highestTile == highestTile &&
                Math.abs(entry.timestamp - entryTimestamp) < 1000) {
                rank = i + 1;
                System.out.println(playerName + " ranked at #" + rank);
                break;
            }
        }
        
        if (rank == -1) {
            System.out.println("WARNING: Could not find entry in sorted list!");
        }
        
        // Determine if made leaderboard (top 10)
        boolean madeLeaderboard = (rank > 0 && rank <= MAX_ENTRIES);
        
        if (madeLeaderboard) {
            System.out.println("Made the top 10 leaderboard at rank #" + rank + "!");
        } else {
            System.out.println("Did not make top 10 (rank: " + rank + ")");
        }
    
        // Keep only top 10 entries BEFORE saving
        if (entries.size() > MAX_ENTRIES) {
            System.out.println("Trimming from " + entries.size() + " to " + MAX_ENTRIES);
            entries = new ArrayList<>(entries.subList(0, MAX_ENTRIES));
        }
    
        // Save the final leaderboard (only top 10)
        saveLeaderboard(entries);
    
        System.out.println("=================================\n");
    
        // Return rank (or -1 if didn't make top 10)
        return madeLeaderboard ? rank : -1;
    }
    
    // Clear all leaderboard data
    public static void clearLeaderboard() {
        System.out.println("=== CLEARING LEADERBOARD ===");
        File file = new File(LEADERBOARD_FILE);
        if (file.exists()) {
            boolean deleted = file.delete();
            System.out.println("Leaderboard file deleted: " + deleted);
        }
        // Create empty leaderboard
        saveLeaderboard(new ArrayList<>());
        System.out.println("Empty leaderboard created.");
    }
    
    // Check if a score qualifies for the leaderboard
    public static boolean qualifiesForLeaderboard(int score) {
        List<LeaderboardEntry> entries = loadLeaderboard();
        
        if (entries.size() < MAX_ENTRIES) {
            return true; // Always qualifies if not full
        }
        
        // Check if score is higher than the lowest entry
        return score > entries.get(entries.size() - 1).score;
    }
    
    // Get leaderboard rank for a score (returns -1 if doesn't qualify)
    public static int getLeaderboardRank(int score) {
        List<LeaderboardEntry> entries = loadLeaderboard();
        
        // Find the rank of this exact score
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).score == score) {
                return i + 1; // Return the actual position in the list
            }
        }
        
        return -1; // Doesn't qualify or not found
    }
}