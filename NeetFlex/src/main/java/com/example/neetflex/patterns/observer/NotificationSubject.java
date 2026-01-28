package com.example.neetflex.patterns.observer;

/**
 * Subject Interface: Defines methods for managing notification observers.
 */
public interface NotificationSubject {
    /**
     * Adds an observer to the notification list.
     * 
     * @param observer The observer to add
     */
    void addObserver(NotificationObserver observer);
    
    /**
     * Removes an observer from the notification list.
     * 
     * @param observer The observer to remove
     */
    void removeObserver(NotificationObserver observer);
    
    /**
     * Notifies all observers about a new notification.
     * 
     * @param notification The notification to send
     */
    void notifyObservers(Notification notification);
}