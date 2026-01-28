package com.example.neetflex.patterns.command;

import com.example.neetflex.enums.ContentType;
import com.example.neetflex.model.user.User;
import com.example.neetflex.service.impl.ContentService;
import com.example.neetflex.service.impl.UserService;

/**
 * Concrete Command: Encapsulates the request to remove from watchlist.
 */
public class RemoveFromWatchlistCommand implements Command {
    ContentService contentService;


    // Parameters needed for the action
    private final String userName;
    private final String contentName;
    private final ContentType contentType;

    public RemoveFromWatchlistCommand(String contentName, String userName, ContentType contentType, ContentService contentService) {
        this.contentName = contentName;
        this.userName = userName;
        this.contentType = contentType;
        this.contentService = contentService;

    }



    @Override
    public void execute() {
        System.out.println("[Command WatchlistCommand ] Executing...");
        contentService.removeContentFromWatchlist(contentName, userName, contentType); // Delegate to receiver
        System.out.println("[Command:  WatchlistCommand ] Execution finished.");
    }
}