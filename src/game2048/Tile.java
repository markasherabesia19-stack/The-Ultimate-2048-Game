package game2048;

public class Tile {
    private int value;
    private boolean merged;
    
    public Tile(int value) {
        this.value = value;
        this.merged = false;
    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int value) {
        this.value = value;
    }
    
    public void doubleValue() {
        this.value *= 2;
    }
    
    public boolean isMerged() {
        return merged;
    }
    
    public void setMerged(boolean merged) {
        this.merged = merged;
    }
    
    public void resetMerged() {
        this.merged = false;
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
    
    public Tile copy() {
        Tile copy = new Tile(this.value);
        copy.merged = this.merged;
        return copy;
    }
}