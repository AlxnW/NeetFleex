package com.example.neetflex.patterns.adapter;

import com.example.neetflex.enums.ContentType;
import com.example.neetflex.model.contents.Content;
import com.example.neetflex.model.contents.Movie;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * External content provider with a different interface than our system expects.
 * This simulates a third-party API for content.
 */
public class ContentProviderExternal {
    // Simulate a database of external content
    private final Map<String, ExternalContent> contentDatabase = new HashMap<>();
    private final Random random = new Random();

    // Sample external content for demonstration
    private final String[] externalTitles = {
        "The Matrix", "Inception", "Interstellar", "The Shawshank Redemption",
        "The Godfather", "Pulp Fiction", "The Dark Knight", "Fight Club"
    };

    private final String[] externalGenres = {
        "Action", "Sci-Fi", "Drama", "Thriller", "Crime", "Adventure"
    };

    public ContentProviderExternal() {
        // Initialize with some sample content
        for (int i = 0; i < externalTitles.length; i++) {
            String id = String.valueOf(1000 + i);
            String title = externalTitles[i];
            String genre = externalGenres[random.nextInt(externalGenres.length)];
            String url = "https://external-content.com/" + title.toLowerCase().replace(" ", "-");

            contentDatabase.put(id, new ExternalContent(id, title, genre, url));
        }
    }

    /**
     * Gets content information by name, ID, and URL.
     * @param id The content ID
     * @param nameOfContent The content name
     * @param url The content URL
     * @return A string with content information
     */
    public String getContentByName(String id, String nameOfContent, String url) {
        ExternalContent content = contentDatabase.get(id);
        if (content != null) {
            return "Found external content: " + content.title + " (" + content.genre + ") at " + content.url;
        }
        return "External content not found with id: " + id;
    }

    /**
     * Fetches content from a URL.
     * @param id The content ID
     * @param nameOfContent The content name
     * @param url The content URL
     * @return A Content object
     */
    public Content fetchContentFromUrl(String id, String nameOfContent, String url) {
        ExternalContent externalContent = contentDatabase.get(id);
        if (externalContent != null) {
            // Convert external content to our internal Content model
            Movie movie = new Movie();
            movie.setTitle(externalContent.title);
            movie.setGenre(externalContent.genre);
            movie.setVideoUrl(externalContent.url);
            return movie;
        }
        return null;
    }

    /**
     * Lists all available content IDs.
     * @return An array of content IDs
     */
    public String[] listAvailableContentIds() {
        return contentDatabase.keySet().toArray(new String[0]);
    }

    /**
     * Internal class to represent external content.
     */
    private static class ExternalContent {
        String id;
        String title;
        String genre;
        String url;

        ExternalContent(String id, String title, String genre, String url) {
            this.id = id;
            this.title = title;
            this.genre = genre;
            this.url = url;
        }
    }
}
