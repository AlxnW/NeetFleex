package com.example.neetflex.patterns.command;

import com.example.neetflex.enums.ContentType;
import com.example.neetflex.service.impl.ContentService;
import org.aspectj.bridge.ICommand;

public class RemoveFromPlaylistCommand implements Command {

    ContentService contentService;


    // Parameters needed for the action
    private final String userName;
    private final String contentName;
    private final ContentType contentType;

    public RemoveFromPlaylistCommand(String contentName, String userName, ContentType contentType, ContentService contentService) {
        this.contentName = contentName;
        this.userName = userName;
        this.contentType = contentType;
        this.contentService = contentService;

    }



    @Override
    public void execute() {
        System.out.println("[Command PlaybackCommand ] Executing...");
        contentService.removeContentFromPlaylist(contentName, userName, contentType); // Delegate to receiver
        System.out.println("[Command:  PlaybackCommand ] Execution finished.");
    }

    // public void undo() {
    //     System.out.println("[Command:  PlaybackCommand ] Undoing...");
    //     receiver.removeContentFromWatchlist(user, content.getId(), contentType);
    //     System.out.println("[Command:  PlaybackCommand ] Undo finished.");
    // }
    
}
