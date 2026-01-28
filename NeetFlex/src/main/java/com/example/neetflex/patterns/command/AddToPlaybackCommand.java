package com.example.neetflex.patterns.command;

import com.example.neetflex.enums.ContentType;
import com.example.neetflex.service.impl.ContentService;

public class AddToPlaybackCommand implements Command {


    ContentService contentService;


    // Parameters needed for the action
    private String userName;
    private String contentName;
    private ContentType contentType;

    public AddToPlaybackCommand(String contentName, String userName, ContentType contentType, ContentService contentService) {
        this.contentName = contentName;
        this.userName = userName;
        this.contentType = contentType;
        this.contentService = contentService;

    }



    @Override
    public void execute() {
        System.out.println("[Command PlaybackCommand ] Executing...");
        contentService.addContentToPlaylist(contentName, userName, contentType); // Delegate to receiver
        System.out.println("[Command:  PlaybackCommand ] Execution finished.");
    }

    // public void undo() {
    //     System.out.println("[Command:  PlaybackCommand ] Undoing...");
    //     receiver.removeContentFromWatchlist(user, content.getId(), contentType);
    //     System.out.println("[Command:  PlaybackCommand ] Undo finished.");
    // }
}
