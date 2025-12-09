package game2048;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class MusicPlayer {
    private Clip currentClip;
    private String currentTrack;
    private boolean isMuted = false;
    private float volume = 0.7f; // 70% volume by default
    
    // Music file paths - WAV FORMAT
    private static final String MENU_MUSIC = "components/music/Satellite.wav";
    private static final String GAMEPLAY_MUSIC = "components/music/Beachhouse.wav";
    
    public MusicPlayer() {
        System.out.println("🎵 Music Player initialized!");
    }
    
    /**
     * Play menu music (for splash screen, instructions, name input)
     */
    public void playMenuMusic() {
        playMusic(MENU_MUSIC);
    }
    
    /**
     * Play gameplay music (for main game screen)
     */
    public void playGameplayMusic() {
        playMusic(GAMEPLAY_MUSIC);
    }
    
    /**
     * Play a specific music file
     */
    private void playMusic(String filePath) {
        // Don't restart if same track is already playing
        if (currentTrack != null && currentTrack.equals(filePath) && 
            currentClip != null && currentClip.isRunning()) {
            System.out.println("♪ Already playing: " + new File(filePath).getName());
            return;
        }
        
        // Stop current music
        stopMusic();
        
        try {
            File musicFile = new File(filePath);
            
            if (!musicFile.exists()) {
                System.out.println("❌ Music file not found: " + filePath);
                System.out.println("📁 Looking at: " + musicFile.getAbsolutePath());
                System.out.println("💡 Make sure your WAV files are in the components/music/ folder!");
                return;
            }
            
            System.out.println("🎵 Loading: " + musicFile.getName() + "...");
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            currentClip = AudioSystem.getClip();
            currentClip.open(audioStream);
            
            // Set volume
            setVolume(volume);
            
            // Loop continuously
            currentClip.loop(Clip.LOOP_CONTINUOUSLY);
            currentClip.start();
            
            currentTrack = filePath;
            
            System.out.println("✅ Now playing: " + musicFile.getName());
            
        } catch (UnsupportedAudioFileException e) {
            System.out.println("❌ Unsupported audio format: " + filePath);
            System.out.println("💡 Make sure the file is in WAV format!");
        } catch (IOException e) {
            System.out.println("❌ Error reading music file: " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.out.println("❌ Audio line unavailable: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Stop the currently playing music
     */
    public void stopMusic() {
        if (currentClip != null) {
            currentClip.stop();
            currentClip.close();
            currentClip = null;
            System.out.println("⏹️ Music stopped");
        }
        currentTrack = null;
    }
    
    /**
     * Pause the music
     */
    public void pauseMusic() {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
            System.out.println("⏸️ Music paused");
        }
    }
    
    /**
     * Resume the music
     */
    public void resumeMusic() {
        if (currentClip != null && !currentClip.isRunning()) {
            currentClip.start();
            System.out.println("▶️ Music resumed");
        }
    }
    
    /**
     * Toggle mute
     */
    public void toggleMute() {
        isMuted = !isMuted;
        if (currentClip != null) {
            try {
                FloatControl volumeControl = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);
                if (isMuted) {
                    volumeControl.setValue(volumeControl.getMinimum());
                    System.out.println("🔇 Music muted");
                } else {
                    setVolume(volume);
                    System.out.println("🔊 Music unmuted");
                }
            } catch (Exception e) {
                System.out.println("❌ Could not toggle mute: " + e.getMessage());
            }
        }
    }
    
    /**
     * Set volume (0.0f to 1.0f)
     */
    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        
        if (currentClip != null && !isMuted) {
            try {
                FloatControl volumeControl = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);
                float min = volumeControl.getMinimum();
                float max = volumeControl.getMaximum();
                float range = max - min;
                float gain = min + (range * this.volume);
                volumeControl.setValue(gain);
                System.out.println("🔊 Volume set to: " + (int)(volume * 100) + "%");
            } catch (Exception e) {
                System.out.println("❌ Could not set volume: " + e.getMessage());
            }
        }
    }
    
    /**
     * Get current volume
     */
    public float getVolume() {
        return volume;
    }
    
    /**
     * Check if music is playing
     */
    public boolean isPlaying() {
        return currentClip != null && currentClip.isRunning();
    }
    
    /**
     * Check if muted
     */
    public boolean isMuted() {
        return isMuted;
    }
}