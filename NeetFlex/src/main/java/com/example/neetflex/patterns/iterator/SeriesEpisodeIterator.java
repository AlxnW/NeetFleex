package com.example.neetflex.patterns.iterator;

import com.example.neetflex.model.contents.Episode;
import com.example.neetflex.model.contents.Series;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * An external Iterator specifically designed to iterate over the Episodes of a Series object.
 */
public class SeriesEpisodeIterator implements Iterator<Episode> {

    private List<Episode> episodesToIterate;
    private int currentPosition = 0; // Index of the next element to return

    /**
     * Constructor requires the Series object to iterate over.
     * @param series The Series whose episodes will be iterated.
     */
    public SeriesEpisodeIterator(Series series) {
        if (series != null && series.getEpisodes() != null) {
            // Get the list of episodes from the series.
            // IMPORTANT: This might trigger lazy loading if episodes are LAZY fetched
            // and the session is not active. Handle potential exceptions or ensure
            // the collection is loaded before creating the iterator.
            try {
                // Create a copy to avoid issues if the original list is modified externally
                // during iteration (ConcurrentModificationException). If the original list
                // is guaranteed not to change, you could use the original directly.
                this.episodesToIterate = new ArrayList<>(series.getEpisodes());
                System.out.println("[SeriesEpisodeIterator] Initialized for Series '" + series.getTitle() + "' with " + this.episodesToIterate.size() + " episodes.");
            } catch (Exception e) {
                // Handle potential LazyInitializationException more gracefully
                System.err.println("[SeriesEpisodeIterator] Error initializing iterator, possibly due to lazy loading: " + e.getMessage());
                this.episodesToIterate = Collections.emptyList(); // Use an empty list on error
            }
        } else {
            System.out.println("[SeriesEpisodeIterator] Initialized with null series or null/empty episodes list.");
            this.episodesToIterate = Collections.emptyList(); // Use an empty list
        }
    }

    /**
     * Checks if there are more episodes to iterate over.
     * @return true if the iteration has more elements, false otherwise.
     */
    @Override
    public boolean hasNext() {
        // Check if the current position is within the bounds of the list
        boolean hasMore = currentPosition < episodesToIterate.size();
        // System.out.println("[SeriesEpisodeIterator] hasNext() at position " + currentPosition + "? " + hasMore); // Debug print
        return hasMore;
    }

    /**
     * Returns the next episode in the iteration.
     * @return The next Episode.
     * @throws NoSuchElementException if the iteration has no more elements.
     */
    @Override
    public Episode next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more episodes in this series iteration.");
        }
        // Get the episode at the current position and increment the position for the next call
        Episode nextEpisode = episodesToIterate.get(currentPosition++);
        // System.out.println("[SeriesEpisodeIterator] next() returning episode at index " + (currentPosition - 1) + ": " + nextEpisode.getEpisodeTitle()); // Debug print
        return nextEpisode;
    }

    /**
     * Removes from the underlying collection the last element returned by this iterator (Optional operation).
     * This implementation does NOT support removal.
     * @throws UnsupportedOperationException always.
     */
    @Override
    public void remove() {
        // Typically, external iterators based on copies don't support removal,
        // or if they operate on the original list, care must be taken.
        throw new UnsupportedOperationException("Remove operation is not supported by SeriesEpisodeIterator.");
    }
}