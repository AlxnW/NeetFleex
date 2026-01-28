package com.example.neetflex.patterns.command;

import com.example.neetflex.enums.ContentType;
import com.example.neetflex.service.impl.ContentService;
import com.example.neetflex.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Concrete Command: Encapsulates the request to add to watchlist.
 */

public class AddToWatchlistCommand implements Command {



    ContentService contentService;


    // Parameters needed for the action
    private String userName;
    private String contentName;
    private ContentType contentType;

    public AddToWatchlistCommand(String contentName, String userName,ContentType contentType, ContentService contentService) {
        this.contentName = contentName;
        this.userName = userName;
        this.contentType = contentType;
        this.contentService = contentService;

    }



    @Override
    public void execute() {
        System.out.println("[Command: AddToWatchlist] Executing...");
        contentService.addContentToWatchlist(contentName, userName, contentType); // Delegate to receiver
        System.out.println("[Command: AddToWatchlist] Execution finished.");
    }

    // public void undo() {
    //     System.out.println("[Command: AddToWatchlist] Undoing...");
    //     receiver.removeContentFromWatchlist(user, content.getId(), contentType);
    //     System.out.println("[Command: AddToWatchlist] Undo finished.");
    // }
}