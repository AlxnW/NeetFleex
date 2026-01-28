package com.example.neetflex.patterns.observer;

import com.example.neetflex.enums.ContentType;
import com.example.neetflex.model.contents.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for sending notifications about content-related events.
 * This service integrates with the ContentService to send notifications
 * when new content is added or updated.
 */
@Service
public class ContentNotificationService {
    private final UserNotificationManager notificationManager;

    @Autowired
    public ContentNotificationService(UserNotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    /**
     * Notifies all users about new content that has been added.
     *
     * @param content The new content that was added
     */
    public void notifyNewContent(Content content) {
        String contentType = content.getType() == ContentType.MOVIE ? "movie" : "series";
        notificationManager.notifyAllUsersAboutNewContent(content.getTitle(), contentType);
        System.out.println("[Service: ContentNotificationService] Notified all users about new " + contentType + ": " + content.getTitle());
    }

    /**
     * Notifies all users about content that has been updated.
     *
     * @param content The content that was updated
     */
    public void notifyContentUpdate(Content content) {
        String contentType = content.getType() == ContentType.MOVIE ? "movie" : "series";
        String subject = "Content Update";
        String message = "The " + contentType + " '" + content.getTitle() + "' has been updated.";

        Notification notification = new Notification(subject, message, NotificationType.CONTENT_UPDATE);
        notificationManager.sendSystemAnnouncementToAllUsers(subject, message);

        System.out.println("[Service: ContentNotificationService] Notified all users about updated " + contentType + ": " + content.getTitle());
    }

    /**
     * Sends a system announcement to all users.
     *
     * @param subject The subject of the announcement
     * @param message The message of the announcement
     */
    public void sendSystemAnnouncement(String subject, String message) {
        notificationManager.sendSystemAnnouncementToAllUsers(subject, message);
        System.out.println("[Service: ContentNotificationService] Sent system announcement: " + subject);
    }
}
