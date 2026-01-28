package com.example.neetflex.patterns.observer;

/**
 * Observer Interface: All concrete notification observers must implement this.
 */
public interface NotificationObserver {
    /**
     * Method called by the Subject when a notification needs to be sent.
     * 
     * @param notification The notification to be processed
     */
    void update(Notification notification);
}