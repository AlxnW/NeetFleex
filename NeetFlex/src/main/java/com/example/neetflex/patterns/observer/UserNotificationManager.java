package com.example.neetflex.patterns.observer;

import com.example.neetflex.model.user.User;
import com.example.neetflex.repositories.UserRepository;
import com.example.neetflex.service.EmailService;
import com.example.neetflex.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages user notifications and handles registration of users for notifications.
 */
@Service
public class UserNotificationManager {
    private final NotificationService notificationService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final Map<Long, EmailNotificationObserver> userObservers = new HashMap<>();

    @Autowired
    public UserNotificationManager(NotificationService notificationService, UserService userService, UserRepository userRepository, EmailService emailService) {
        this.notificationService = notificationService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    /**
     * Registers a user for notifications.
     *
     * @param user The user to register
     */
    public void registerUser(User user) {
        if (user == null || user.getId() == null) {
            System.out.println("[Manager: UserNotificationManager] Cannot register null user or user without ID");
            return;
        }

        if (!userObservers.containsKey(user.getId())) {
            EmailNotificationObserver observer = new EmailNotificationObserver(user, emailService);
            userObservers.put(user.getId(), observer);
            notificationService.addObserver(observer);
            System.out.println("[Manager: UserNotificationManager] User registered for notifications: " + user.getUsername());
        }
    }

    /**
     * Unregisters a user from notifications.
     *
     * @param userId The ID of the user to unregister
     */
    public void unregisterUser(Long userId) {
        EmailNotificationObserver observer = userObservers.remove(userId);
        if (observer != null) {
            notificationService.removeObserver(observer);
            System.out.println("[Manager: UserNotificationManager] User unregistered from notifications: " + userId);
        }
    }

    /**
     * Registers all users in the system for notifications.
     */
    public void registerAllUsers() {
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            registerUser(user);
        }
        System.out.println("[Manager: UserNotificationManager] Registered " + allUsers.size() + " users for notifications");
    }

    /**
     * Sends a notification to all registered users about new content.
     *
     * @param contentTitle The title of the new content
     * @param contentType The type of content (movie, series, etc.)
     */
    public void notifyAllUsersAboutNewContent(String contentTitle, String contentType) {
        if (userObservers.isEmpty()) {
            System.out.println("[Manager: UserNotificationManager] No users registered for notifications");
            return;
        }

        notificationService.sendNewContentNotification(contentTitle, contentType);
    }

    /**
     * Sends a system announcement to all registered users.
     *
     * @param subject The subject of the announcement
     * @param message The message of the announcement
     */
    public void sendSystemAnnouncementToAllUsers(String subject, String message) {
        if (userObservers.isEmpty()) {
            System.out.println("[Manager: UserNotificationManager] No users registered for notifications");
            return;
        }

        notificationService.sendSystemAnnouncement(subject, message);
    }

    /**
     * Sends a notification to a specific user.
     *
     * @param userId The ID of the user to notify
     * @param notification The notification to send
     */
    public void notifyUser(Long userId, Notification notification) {
        EmailNotificationObserver observer = userObservers.get(userId);
        if (observer != null) {
            observer.update(notification);
        } else {
            System.out.println("[Manager: UserNotificationManager] User not registered for notifications: " + userId);
        }
    }
}
