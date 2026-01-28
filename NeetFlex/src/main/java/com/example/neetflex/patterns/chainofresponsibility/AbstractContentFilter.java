package com.example.neetflex.patterns.chainofresponsibility;

import com.example.neetflex.model.contents.Content;
import com.example.neetflex.model.user.User;

import java.util.List;

/**
 * Abstract base class for content filters in the Chain of Responsibility pattern.
 * Provides common functionality for all concrete filters.
 */
public abstract class AbstractContentFilter implements ContentFilter {
    protected ContentFilter nextFilter;
    
    @Override
    public ContentFilter setNext(ContentFilter nextFilter) {
        this.nextFilter = nextFilter;
        return nextFilter;
    }
    
    @Override
    public List<Content> filter(List<Content> contentList, User user) {
        // If there's no next filter, return the filtered list
        if (nextFilter == null) {
            return doFilter(contentList, user);
        }
        
        // Otherwise, apply this filter and pass the result to the next filter
        return nextFilter.filter(doFilter(contentList, user), user);
    }
    
    /**
     * Concrete filtering logic to be implemented by subclasses.
     * 
     * @param contentList The list of content to filter
     * @param user The user requesting the content
     * @return The filtered list of content
     */
    protected abstract List<Content> doFilter(List<Content> contentList, User user);
}