package game2048;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class to preload and cache all game images
 */
public class ImageLoader {
    private static ImageLoader instance;
    private Map<String, Image> imageCache;
    private boolean loaded = false;
    
    private ImageLoader() {
        imageCache = new HashMap<>();
    }
    
    public static ImageLoader getInstance() {
        if (instance == null) {
            instance = new ImageLoader();
        }
        return instance;
    }
    
    public void preloadImages(Runnable onComplete) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Load splash screen image
                loadImage("splashscreen", "components/images/splashscreen.png", 1120, 630);
                
                // Load background image
                loadImage("background", "components/images/background.png", 1280, 720);
                
                return null;
            }
            
            @Override
            protected void done() {
                loaded = true;
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        };
        
        worker.execute();
    }
    
    private void loadImage(String key, String path, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(path);
            if (icon.getIconWidth() > 0) {
                Image scaledImage = icon.getImage().getScaledInstance(
                    width, height, Image.SCALE_FAST
                );
                imageCache.put(key, scaledImage);
                System.out.println("Loaded: " + key);
            }
        } catch (Exception e) {
            System.out.println("Failed to load " + key + ": " + e.getMessage());
        }
    }
    
    public Image getImage(String key) {
        return imageCache.get(key);
    }
    
    public boolean isLoaded() {
        return loaded;
    }
}