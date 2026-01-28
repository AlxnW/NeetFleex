package com.example.neetflex.patterns.chainofresponsibility;

import com.example.neetflex.model.contents.Content;
import com.example.neetflex.model.user.User;

import java.util.List;

/**
 * Interface for the Chain of Responsibility pattern.
 * Each filter in the chain can process content and pass it to the next filter.
 */
public interface ContentFilter {
    /**
     * Sets the next filter in the chain.
     * 
     * @param nextFilter The next filter to process content
     * @return The next filter for chaining
     */
    ContentFilter setNext(ContentFilter nextFilter);
    
    /**
     * Filters the content list based on specific criteria and passes the result to the next filter.
     * 
     * @param contentList The list of content to filter
     * @param user The user requesting the content
     * @return The filtered list of content
     */
    List<Content> filter(List<Content> contentList, User user);
}