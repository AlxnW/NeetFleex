package com.example.neetflex.patterns.chainofresponsibility;

import com.example.neetflex.model.contents.Content;
import com.example.neetflex.model.user.User;

import java.util.List;

/**
 * Class that manages the chain of content filters.
 * Provides a convenient way to build and use the filter chain.
 */
public class ContentFilterChain {
    
    private ContentFilter firstFilter;
    private ContentFilter lastFilter;
    
    /**
     * Adds a filter to the end of the chain.
     * 
     * @param filter The filter to add
     * @return This chain for method chaining
     */
    public ContentFilterChain addFilter(ContentFilter filter) {
        System.out.println("[Chain of Responsibility] Adding filter: " + filter.getClass().getSimpleName());
        
        if (firstFilter == null) {
            firstFilter = filter;
            lastFilter = filter;
        } else {
            lastFilter.setNext(filter);
            lastFilter = filter;
        }
        
        return this;
    }
    
    /**
     * Applies all filters in the chain to the content list.
     * 
     * @param contentList The list of content to filter
     * @param user The user requesting the content
     * @return The filtered list of content
     */
    public List<Content> applyFilters(List<Content> contentList, User user) {
        System.out.println("[Chain of Responsibility] Applying filter chain to " + contentList.size() + " content items");
        
        if (firstFilter == null) {
            return contentList;
        }
        
        return firstFilter.filter(contentList, user);
    }
    
    /**
     * Resets the filter chain, removing all filters.
     */
    public void reset() {
        System.out.println("[Chain of Responsibility] Resetting filter chain");
        firstFilter = null;
        lastFilter = null;
    }
}