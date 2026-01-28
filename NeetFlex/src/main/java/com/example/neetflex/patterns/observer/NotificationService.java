package com.example.neetflex.patterns.observer;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete Subject: Manages notification observers and sends notifications.
 */
@Service
public class NotificationService implements NotificationSubject {
    // List to hold all registered observers
    private List<NotificationObserver> observers = new ArrayList<>();

    @Override
    public synchronized void addObserver(NotificationObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
            System.out.println("[Service: NotificationService] Observer added: " + observer.getClass().getSimpleName());
        }
    }

    @Override
    public synchronized void removeObserver(NotificationObserver observer) {
        observers.remove(observer);
        System.out.println("[Service: NotificationService] Observer removed: " + observer.getClass().getSimpleName());
    }

    @Override
    public void notifyObservers(Notification notification) {
        System.out.println("[Service: NotificationService] Notifying " + observers.size() + 
                " observers about: " + notification.getSubject());
        
        // Create a copy to avoid issues if observers modify the list during notification
        List<NotificationObserver> observersToNotify = new ArrayList<>(observers);
        for (NotificationObserver observer : observersToNotify) {
            observer.update(notification);
        }
        
        System.out.println("[Service: NotificationService] Notification complete.");
    }

    /**
     * Creates and sends a notification about new content.
     * 
     * @param contentTitle The title of the new content
     * @param contentType The type of content (movie, series, etc.)
     */
    public void sendNewContentNotification(String contentTitle, String contentType) {
        String subject = "New " + contentType + " Added!";
        String message = "Check out our new " + contentType + ": " + contentTitle;
        
        Notification notification = new Notification(subject, message, NotificationType.NEW_CONTENT);
        notifyObservers(notification);
    }

    /**
     * Creates and sends a system announcement notification.
     * 
     * @param subject The subject of the announcement
     * @param message The message of the announcement
     */
    public void sendSystemAnnouncement(String subject, String message) {
        Notification notification = new Notification(subject, message, NotificationType.SYSTEM_ANNOUNCEMENT);
        notifyObservers(notification);
    }

    /**
     * Creates and sends a subscription update notification.
     * 
     * @param username The username of the user
     * @param subscriptionType The type of subscription
     */
    public void sendSubscriptionUpdateNotification(String username, String subscriptionType) {
        String subject = "Subscription Update";
        String message = "Hello " + username + ", your subscription has been updated to " + subscriptionType;
        
        Notification notification = new Notification(subject, message, NotificationType.SUBSCRIPTION_UPDATE);
        notifyObservers(notification);
    }
}