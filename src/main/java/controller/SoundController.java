package controller;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class SoundController {

    private Map<String, MediaPlayer> players;

    public SoundController() {
        players = new HashMap<>();
    }

    public boolean loadSound(String identifier, String resourcePath) {
        String lowerId = identifier.toLowerCase();
        if (players.containsKey(lowerId)) {
            return true;
        }
        try {
            URL resourceUrl = getClass().getResource(resourcePath);
            Media media = new Media(resourceUrl.toExternalForm());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            players.put(lowerId, mediaPlayer);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return false;
    }

    public void playSound(String identifier, boolean loop) {
        MediaPlayer player = players.get(identifier.toLowerCase());
        if (player != null) {
            player.setCycleCount(loop ? MediaPlayer.INDEFINITE : 1);
            if (player.getStatus() != MediaPlayer.Status.PLAYING) {
                player.play();
            } else {
                if (loop && player.getCycleCount() != MediaPlayer.INDEFINITE) {
                    player.setCycleCount(MediaPlayer.INDEFINITE);
                }
            }
        }
    }

    public void stopSound(String identifier) {
        MediaPlayer player = players.get(identifier.toLowerCase());
        if (player != null) {
            MediaPlayer.Status status = player.getStatus();
            if (status == MediaPlayer.Status.PLAYING || status == MediaPlayer.Status.PAUSED) {
                player.stop();
            }
        }
    }

    public void pauseSound(String identifier) {
        MediaPlayer player = players.get(identifier.toLowerCase());
        if (player != null && player.getStatus() == MediaPlayer.Status.PLAYING) {
            player.pause();
        }
    }

    public void setVolume(String identifier, double volume) {
        MediaPlayer player = players.get(identifier.toLowerCase());
        if (player != null) {
            double validVolume = Math.max(0.0, Math.min(1.0, volume));
            player.setVolume(validVolume);
        }
    }

    public boolean isPlaying(String identifier) {
        MediaPlayer player = players.get(identifier.toLowerCase());
        return player != null && player.getStatus() == MediaPlayer.Status.PLAYING;
    }

    public void stopAllSounds() {
        for (MediaPlayer player : players.values()) {
            MediaPlayer.Status status = player.getStatus();
            if (status == MediaPlayer.Status.PLAYING || status == MediaPlayer.Status.PAUSED) {
                player.stop();
            }
        }
    }

    /**
     * Giải phóng tài nguyên.
     */
    public void disposeAll() {
        stopAllSounds();
        for (MediaPlayer player : players.values()) {
            player.dispose();
        }
        players.clear();
    }
}