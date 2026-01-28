package com.example.neetflex.patterns.memento;

import com.example.neetflex.enums.ContentType;
import lombok.Data;
import lombok.Getter;

/**
 * Originator: The object whose state needs to be saved/restored.
 */
@Data
public class PlaybackSession {
    // Internal state
    private Long contentId;
    private ContentType contentType;
    @Getter
    private int currentPositionSeconds;
    @Getter
    private String userWatching;

    public PlaybackSession(String userWatching, Long contentId, ContentType contentType) {
        this.userWatching = userWatching;
        this.contentId = contentId;
        this.contentType = contentType;
        this.currentPositionSeconds = 0; // Start at the beginning
        System.out.println("[Originator: PlaybackSession] Created for " + userWatching + " on Content " + contentId + " (" + contentType + ")");
    }

    // Method to change state
    public void advancePlayback(int seconds) {
        this.currentPositionSeconds += seconds;
        System.out.println("[Originator: PlaybackSession] Advanced playback to " + this.currentPositionSeconds + "s");
    }

    // Method to save the current state into a Memento
    public PlaybackMemento saveStateToMemento() {
        System.out.println("[Originator: PlaybackSession] Saving current state (Position: " + currentPositionSeconds + "s)...");
        return new PlaybackMemento(this.contentId, this.contentType, this.currentPositionSeconds);
    }

    // Method to restore state from a Memento
    public void restoreStateFromMemento(PlaybackMemento memento) {
        if (memento != null) {
            // Optional: Check if memento matches current session context if needed
            System.out.println("[Originator: PlaybackSession] Restoring state from Memento (saved at " + memento.getTimestamp() + ")...");
            this.contentId = memento.getContentId(); // Usually constant for a session
            this.contentType = memento.getContentType(); // Usually constant
            this.currentPositionSeconds = memento.getPositionSeconds(); // The key state to restore
            System.out.println("[Originator: PlaybackSession] State restored. Current position: " + this.currentPositionSeconds + "s");
        } else {
            System.err.println("[Originator: PlaybackSession] Cannot restore from null memento!");
        }
    }

    //Add other necessary methods (play, pause, getters/setters)
}