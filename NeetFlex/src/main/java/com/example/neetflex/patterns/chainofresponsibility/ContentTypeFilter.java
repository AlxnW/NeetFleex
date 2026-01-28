package com.example.neetflex.patterns.chainofresponsibility;

import com.example.neetflex.enums.ContentType;
import com.example.neetflex.model.contents.Content;
import com.example.neetflex.model.user.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Concrete filter that filters content based on content type (movie or series).
 */
public class ContentTypeFilter extends AbstractContentFilter {
    
    private final ContentType preferredType;
    
    /**
     * Constructor that accepts a preferred content type.
     * 
     * @param preferredType The content type to filter by (MOVIE or SERIES)
     */
    public ContentTypeFilter(ContentType preferredType) {
        this.preferredType = preferredType;
    }
    
    @Override
    protected List<Content> doFilter(List<Content> contentList, User user) {
        System.out.println("[Chain of Responsibility] Applying content type filter for type: " + preferredType);
        
        // If no preferred type is specified, return all content
        if (preferredType == null) {
            return contentList;
        }
        
        // Filter content by type
        return contentList.stream()
                .filter(content -> content.getType() == preferredType)
                .collect(Collectors.toList());
    }
}