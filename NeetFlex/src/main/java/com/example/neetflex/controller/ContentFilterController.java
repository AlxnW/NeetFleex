package com.example.neetflex.controller;

import com.example.neetflex.dto.ContentResponseDTO;
import com.example.neetflex.enums.ContentType;
import com.example.neetflex.service.impl.ContentFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller that exposes endpoints for filtering content using the Chain of Responsibility pattern.
 */
@RestController
@RequestMapping("/api/content/filter")
public class ContentFilterController {

    private final ContentFilterService contentFilterService;

    @Autowired
    public ContentFilterController(ContentFilterService contentFilterService) {
        this.contentFilterService = contentFilterService;
    }

    /**
     * Filters content based on various criteria using the Chain of Responsibility pattern.
     *
     * @param username The username of the user requesting the content
     * @param contentType The type of content to filter (MOVIE or SERIES), can be null for all types
     * @param genres The list of genres to filter by, can be null or empty for all genres
     * @return The filtered list of content
     */
    @GetMapping
    public List<ContentResponseDTO> filterContent(
            @RequestParam String username,
            @RequestParam(required = false) ContentType contentType,
            @RequestParam(required = false) List<String> genres) {
        
        System.out.println("[Chain of Responsibility] Filtering content for user: " + username);
        if (contentType != null) {
            System.out.println("[Chain of Responsibility] Content type filter: " + contentType);
        }
        if (genres != null && !genres.isEmpty()) {
            System.out.println("[Chain of Responsibility] Genre filters: " + genres);
        }
        
        return contentFilterService.filterContent(username, contentType, genres);
    }

    /**
     * Example usage of the Chain of Responsibility pattern.
     * This endpoint demonstrates how to use the pattern with a specific example.
     *
     * @return A message describing the example
     */
    @GetMapping("/example")
    public String getExample() {
        return "Chain of Responsibility Pattern Example:\n\n" +
               "1. GET /api/content/filter?username=john&contentType=MOVIE\n" +
               "   - Filters movies based on John's subscription level\n\n" +
               "2. GET /api/content/filter?username=jane&genres=Action,Comedy\n" +
               "   - Filters content in Action and Comedy genres based on Jane's subscription level\n\n" +
               "3. GET /api/content/filter?username=bob&contentType=SERIES&genres=Drama\n" +
               "   - Filters series in Drama genre based on Bob's subscription level";
    }
}