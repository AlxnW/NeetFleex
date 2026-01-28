package com.example.neetflex.patterns.adapter;

import com.example.neetflex.model.contents.Content;
import com.example.neetflex.model.contents.Movie;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Local content provider that directly implements the IContentProvider interface.
 * This represents our system's native content provider.
 */
@Component
public class ContentProviderLocal implements IContentProvider {
    // Simulate a database of local content
    private final Map<String, Content> localContentDatabase = new HashMap<>();

    public ContentProviderLocal() {
        // Initialize with some sample content
        initializeLocalContent();
        System.out.println("[Local Provider] ContentProviderLocal initialized with " + 
                           localContentDatabase.size() + " local content items.");
    }

    private void initializeLocalContent() {
        // Add some sample movies
        addMovie("1", "The Avengers", "Action", "https://local-content.com/the-avengers");
        addMovie("2", "Titanic", "Drama", "https://local-content.com/titanic");
        addMovie("3", "Jurassic Park", "Adventure", "https://local-content.com/jurassic-park");
        addMovie("4", "The Lion King", "Animation", "https://local-content.com/the-lion-king");
    }

    private void addMovie(String id, String title, String genre, String videoUrl) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setGenre(genre);
        movie.setVideoUrl(videoUrl);
        localContentDatabase.put(id, movie);
    }

    /**
     * Gets content information by ID.
     * @param id The content ID
     * @return A string with content information
     */
    @Override
    public String getContent(String id) {
        Content content = localContentDatabase.get(id);
        if (content != null) {
            return "Playing local content: " + content.getTitle() + " (" + content.getGenre() + ")";
        }
        return "Local content not found with id: " + id;
    }

    /**
     * Finds content by ID.
     * @param id The content ID
     * @return A Content object
     */
    @Override
    public Content findContentbyId(String id) {
        Content content = localContentDatabase.get(id);
        if (content != null) {
            System.out.println("[Local Provider] Found local content: " + content.getTitle());
        } else {
            System.out.println("[Local Provider] Local content not found with ID: " + id);
        }
        return content;
    }

    /**
     * Lists all available content IDs.
     * @return An array of content IDs
     */
    public String[] listAvailableIds() {
        return localContentDatabase.keySet().toArray(new String[0]);
    }
}
