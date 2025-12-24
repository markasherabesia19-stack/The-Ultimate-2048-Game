package game2048;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

/**
 * MusicPlayer handles background music for the game.
 * Manages music playback for different game states (menu and gameplay) with volume control and mute functionality.
 */
public class MusicPlayer {
    private Clip currentClip;
    private String currentTrack;
    private boolean isMuted = false;
    private float volume = 0.9f; // 90% volume by default
    
    // Music file paths - WAV FORMAT
    private static final String MENU_MUSIC = "components/music/Satellite.wav";
    private static final String GAMEPLAY_MUSIC = "components/music/Beachhouse.wav";
    
    public MusicPlayer() {
        System.out.println("Music Player initialized!");
    }
    
    // Play menu music (for splash screen, instructions, name input)
    public void playMenuMusic() {
        playMusic(MENU_MUSIC);
    }
    
    // Play gameplay music (for main game screen)
    public void playGameplayMusic() {
        playMusic(GAMEPLAY_MUSIC);
    }
    
    // Play a specific music file
    private void playMusic(String filePath) {
        // Don't restart if same track is already playing
        if (currentTrack != null && currentTrack.equals(filePath) && 
            currentClip != null && currentClip.isRunning()) {
            System.out.println("Already playing: " + new File(filePath).getName());
            return;
        }
    
        // Stop current music
        stopMusic();
    
        AudioInputStream audioStream = null; // Declare outside try block so it can be close
    
        try {
            File musicFile = new File(filePath);
        
            if (!musicFile.exists()) {
                System.out.println("Music file not found: " + filePath);
                System.out.println("Looking at: " + musicFile.getAbsolutePath());
                System.out.println("Make sure your WAV files are in the components/music/ folder!");
                return;
            }
        
            System.out.println("Loading: " + musicFile.getName() + "...");
        
            audioStream = AudioSystem.getAudioInputStream(musicFile);
            currentClip = AudioSystem.getClip();
            currentClip.open(audioStream);
        
            audioStream.close();
            audioStream = null;
            System.out.println("Audio stream closed");
        
            // Set volume based on mute state
            if (isMuted) {
                setVolumeToMinimum();
            } else {
                setVolume(volume);
            }
        
            // Loop continuously
            currentClip.loop(Clip.LOOP_CONTINUOUSLY);
            currentClip.start();
        
            currentTrack = filePath;
        
            System.out.println("Now playing: " + musicFile.getName());
        
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Unsupported audio format: " + filePath);
            System.out.println("Make sure the file is in WAV format!");
        } catch (IOException e) {
            System.out.println("Error reading music file: " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.out.println("Audio line unavailable: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Always close the stream, even if there's an error
            if (audioStream != null) {
                try {
                    audioStream.close();
                    System.out.println("Audio stream closed in finally block");
                } catch (IOException e) {
                    System.out.println("Could not close audio stream: " + e.getMessage());
                }
            }
        }
    }

    // Stop the currently playing music
    public void stopMusic() {
        if (currentClip != null) {
            currentClip.stop();
            currentClip.close();
            currentClip = null;
            System.out.println("Music stopped");
        }
        currentTrack = null;
    }
    
    // Pause the music
    public void pauseMusic() {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
            System.out.println("Music paused");
        }
    }
    
    // Resume the music
    public void resumeMusic() {
        if (currentClip != null && !currentClip.isRunning()) {
            currentClip.start();
            System.out.println("Music resumed");
        }
    }
    
    // Toggle mute
    public void toggleMute() {
        isMuted = !isMuted;
        if (currentClip != null) {
            try {
                FloatControl volumeControl = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);
                if (isMuted) {
                    volumeControl.setValue(volumeControl.getMinimum());
                    System.out.println("Music muted");
                } else {
                    setVolume(volume);
                    System.out.println("Music unmuted");
                }
            } catch (Exception e) {
                System.out.println("Could not toggle mute: " + e.getMessage());
            }
        }
    }
    
    // Unmute the music (reset mute state)
    public void unmute() {
        if (isMuted) {
            isMuted = false;
            if (currentClip != null) {
                try {
                    setVolume(volume);
                    System.out.println("Music unmuted (reset)");
                } catch (Exception e) {
                    System.out.println("Could not unmute: " + e.getMessage());
                }
            }
        }
    }
    
    // Set volume to minimum (for muting)
    private void setVolumeToMinimum() {
        if (currentClip != null) {
            try {
                FloatControl volumeControl = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(volumeControl.getMinimum());
            } catch (Exception e) {
                System.out.println("Could not set volume to minimum: " + e.getMessage());
            }
        }
    }
    
    // Set volume (0.0f to 1.0f)
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
                System.out.println("Volume set to: " + (int)(volume * 100) + "%");
            } catch (Exception e) {
                System.out.println("Could not set volume: " + e.getMessage());
            }
        }
    }
    
    // Get current volume
    public float getVolume() {
        return volume;
    }
    
    // Check if music is playing
    public boolean isPlaying() {
        return currentClip != null && currentClip.isRunning();
    }
    
    // Check if muted
    public boolean isMuted() {
        return isMuted;
    }
}