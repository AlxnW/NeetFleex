package com.example.neetflex.patterns.iterator;

import com.example.neetflex.model.contents.Episode;
import com.example.neetflex.model.contents.Series;

public class IteratorUsage {

    public void listEpisodes(Series series) {
        if (series == null) {
            System.out.println("[Iterator Example] Cannot list episodes, series is null.");
            return;
        }
        System.out.println("\n[Iterator Example] Listing episodes for Series: '" + series.getTitle() + "' (using external SeriesEpisodeIterator)");

        // --- Cannot use enhanced for-loop directly on Series ---

        System.out.println("\n--- Using the external SeriesEpisodeIterator ---");
        // *** Create an instance of the external iterator, passing the series ***
        Iterator<Episode> iterator = new SeriesEpisodeIterator(series);
        int count = 0;
        try {
            while (iterator.hasNext()) {
                Episode episode = iterator.next();
                System.out.println("  -> Found (via external iterator): " + episode.getEpisodeTitle());
                count++;
                // iterator.remove(); // Would throw UnsupportedOperationException with current implementation
            }
            if (count == 0) {
                System.out.println("  (No episodes found via external iterator)");
            }
        } catch (Exception e) {
            // Catch potential NoSuchElementException or other issues
            System.err.println("  Error during external iterator usage: " + e.getMessage());
        }
        System.out.println("[Iterator Example] Finished listing episodes.");
    }

    // Helper method to create a dummy series
    public static Series createDummySeries() {
        Series s = new Series("The Iteration Chronicles");
        s.addEpisode(new Episode("Chapter 1: The Beginning"));
        s.getEpisodes().get(0).setEpisodeNumber(1);
        s.addEpisode(new Episode("Chapter 2: The Middle"));
        s.getEpisodes().get(1).setEpisodeNumber(2);
        s.addEpisode(new Episode("Chapter 3: The End"));
        s.getEpisodes().get(2).setEpisodeNumber(3);
        return s;
    }

//    // Example main
//    public static void main(String[] args) {
//        IteratorUsage usage = new IteratorUsage();
//        Series testSeries = createDummySeries();
//        usage.listEpisodes(testSeries);
//
//        Series emptySeries = new Series("Empty Show");
//        usage.listEpisodes(emptySeries);
//    }
}