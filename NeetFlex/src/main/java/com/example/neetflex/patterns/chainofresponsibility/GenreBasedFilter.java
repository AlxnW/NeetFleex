package com.example.neetflex.patterns.chainofresponsibility;

import com.example.neetflex.model.contents.Content;
import com.example.neetflex.model.user.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Concrete filter that filters content based on specified genres.
 * Only content matching the preferred genres will be included in the result.
 */
public class GenreBasedFilter extends AbstractContentFilter {
    
    private final List<String> preferredGenres;
    
    /**
     * Constructor that accepts a list of preferred genres.
     * 
     * @param preferredGenres The list of genres to filter by
     */
    public GenreBasedFilter(List<String> preferredGenres) {
        this.preferredGenres = preferredGenres;
    }
    
    @Override
    protected List<Content> doFilter(List<Content> contentList, User user) {
        System.out.println("[Chain of Responsibility] Applying genre-based filter for genres: " + preferredGenres);
        
        // If no preferred genres are specified, return all content
        if (preferredGenres == null || preferredGenres.isEmpty()) {
            return contentList;
        }
        
        // Filter content by preferred genres
        return contentList.stream()
                .filter(content -> preferredGenres.contains(content.getGenre()))
                .collect(Collectors.toList());
    }
}