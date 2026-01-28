package com.example.neetflex.patterns.memento;

import com.example.neetflex.enums.ContentType;

import java.util.HashMap;
import java.util.Map;

/**
 * Caretaker: Manages saved Mementos. It doesn't know about their internal state.
 * This uses a simple in-memory map; a real one might use a database.
 */
public class PlaybackHistoryCaretaker {
    // Map to store mementos, keyed by something unique (e.g., user:content_id:type)
    private Map<String, PlaybackMemento> savedStates = new HashMap<>();

    public void saveMemento(String key, PlaybackMemento memento) {
        System.out.println("[Caretaker] Saving memento with key: " + key);
        savedStates.put(key, memento);
        // In reality: Persist memento data (position) to DB using PlaybackContentRepository
    }

    public PlaybackMemento getMemento(String key) {
        System.out.println("[Caretaker] Retrieving memento with key: " + key);
        PlaybackMemento memento = savedStates.get(key);
        if (memento == null) {
            System.out.println("[Caretaker] No memento found for key: " + key);
            // In reality: Try fetching from DB using PlaybackContentRepository
        }
        return memento;
    }

    // Helper to create a key (example)
    public static String createKey(String username, Long contentId, ContentType type) {
        return username + ":" + contentId + ":" + type;
    }
}