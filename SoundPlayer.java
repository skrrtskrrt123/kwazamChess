/**
 * Utility class for playing game sound effects
 * @author Aleesya
 */
package util;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

// Handles playing different sound effects for game events
public class SoundPlayer {
    private static Clip backgroundClip = null;
    private static boolean isBackgroundPlaying = false;

    public static void playSound(String soundType) {
        String soundFile = switch (soundType) {
                case "move" -> "/sounds/move-self.wav";
                case "capture" -> "/sounds/capture.wav";
                case "game over" -> "/sounds/notify.wav";
                case "background" -> "/sounds/background.wav";
                default -> null;
            };

        if (soundFile == null) return;

        try {
            URL soundURL = SoundPlayer.class.getResource(soundFile);
            if (soundURL == null) return;
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            // If it's background music, handle it separately
            if (soundType.equals("background")) {
                if (!isBackgroundPlaying) {
                    if (backgroundClip != null) {
                        backgroundClip.stop();
                    }
                    backgroundClip = clip;
                    backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
                    isBackgroundPlaying = true;
                }
            } else {
                clip.start();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public static void stopBackgroundMusic() {
        if (backgroundClip != null) {
            backgroundClip.stop();
            isBackgroundPlaying = false;
        }
    }
}