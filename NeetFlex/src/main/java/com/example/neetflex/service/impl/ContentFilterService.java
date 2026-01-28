package com.example.neetflex.service.impl;

import com.example.neetflex.dto.ContentResponseDTO;
import com.example.neetflex.enums.ContentType;
import com.example.neetflex.model.contents.Content;
import com.example.neetflex.model.user.User;
import com.example.neetflex.patterns.chainofresponsibility.ContentFilterChain;
import com.example.neetflex.patterns.chainofresponsibility.ContentTypeFilter;
import com.example.neetflex.patterns.chainofresponsibility.GenreBasedFilter;
import com.example.neetflex.patterns.chainofresponsibility.SubscriptionBasedFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service that uses the Chain of Responsibility pattern to filter content.
 */
@Service
public class ContentFilterService {

    private final ContentService contentService;
    private final UserService userService;

    @Autowired
    public ContentFilterService(ContentService contentService, UserService userService) {
        this.contentService = contentService;
        this.userService = userService;
    }

    /**
     * Filters content based on various criteria using the Chain of Responsibility pattern.
     *
     * @param username The username of the user requesting the content
     * @param contentType The type of content to filter (MOVIE or SERIES), can be null for all types
     * @param genres The list of genres to filter by, can be null or empty for all genres
     * @return The filtered list of content
     */
    public List<ContentResponseDTO> filterContent(String username, ContentType contentType, List<String> genres) {
        // Get the user
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        // Get all content
        List<Content> allContent = new ArrayList<>();

        // Convert ContentResponseDTO to Content (this is a simplification, in a real application
        // you would need to properly convert between these types)
        if (contentType == null || contentType == ContentType.MOVIE) {
            allContent.addAll(contentService.getAllPopularMovies().stream()
                    .map(this::convertToContent)
                    .collect(Collectors.toList()));
        }

        if (contentType == null || contentType == ContentType.SERIES) {
            allContent.addAll(contentService.getAllPopularSeries().stream()
                    .map(this::convertToContent)
                    .collect(Collectors.toList()));
        }

        // Create and configure the filter chain
        ContentFilterChain filterChain = new ContentFilterChain();

        // Add subscription-based filter (always applied)
        filterChain.addFilter(new SubscriptionBasedFilter());

        // Add content type filter if specified
        if (contentType != null) {
            filterChain.addFilter(new ContentTypeFilter(contentType));
        }

        // Add genre-based filter if specified
        if (genres != null && !genres.isEmpty()) {
            filterChain.addFilter(new GenreBasedFilter(genres));
        }

        // Apply the filter chain
        List<Content> filteredContent = filterChain.applyFilters(allContent, user);

        // Convert back to DTOs (again, this is a simplification)
        return filteredContent.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converts a ContentResponseDTO to a Content object.
     * This is a simplified implementation and would need to be properly implemented
     * in a real application.
     */
    private Content convertToContent(ContentResponseDTO dto) {
        // This is a placeholder implementation
        // In a real application, you would need to properly convert between these types
        return null;
    }

    /**
     * Converts a Content object to a ContentResponseDTO.
     * This is a simplified implementation and would need to be properly implemented
     * in a real application.
     */
    private ContentResponseDTO convertToDTO(Content content) {
        // This is a placeholder implementation
        // In a real application, you would need to properly convert between these types
        return null;
    }
}
