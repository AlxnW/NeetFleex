package com.example.neetflex.patterns.composite;

import com.example.neetflex.enums.ContentType;
import lombok.Getter;

/**
 * Leaf class in the Composite pattern.
 * Represents a single media item (movie or episode) that can be played.
 */
@Getter
public class MediaItem implements MediaComponent {
    private final String title;
    private final String url;
    private final ContentType type;
    
    public MediaItem(String title, String url, ContentType type) {
        this.title = title;
        this.url = url;
        this.type = type;
        System.out.println("[Composite: Leaf] Created MediaItem: " + title + " (" + type + ")");
    }
    
    @Override
    public void play() {
        System.out.println("[Composite: Leaf] Playing " + type + ": " + title + " from " + url);
    }
}