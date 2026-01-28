package com.example.neetflex.patterns.memento;

import com.example.neetflex.enums.ContentType;
import java.time.LocalDateTime;

/**
 * Memento: Stores the state of the PlaybackSession at a point in time.
 * Should be immutable or have restricted access after creation.
 */
public final class PlaybackMemento { // Final to prevent subclassing
    // The state fields - make them final or accessible only via getters
    private final Long contentId;
    private final ContentType contentType;
    private final int positionSeconds;
    private final LocalDateTime timestamp; // When was this state saved?

    public PlaybackMemento(Long contentId, ContentType contentType, int positionSeconds) {
        this.contentId = contentId;
        this.contentType = contentType;
        this.positionSeconds = positionSeconds;
        this.timestamp = LocalDateTime.now();
        System.out.println("[Memento] State captured at " + timestamp + ": Content " + contentId + " (" + contentType + ") at " + positionSeconds + "s");
    }

    // Getters to allow the Originator to retrieve state during restoration
    public Long getContentId() { return contentId; }
    public ContentType getContentType() { return contentType; }
    public int getPositionSeconds() { return positionSeconds; }
    public LocalDateTime getTimestamp() { return timestamp; }
}