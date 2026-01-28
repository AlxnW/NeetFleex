package com.example.neetflex.patterns.notifier;

import com.example.neetflex.config.EmailConfig;
import com.example.neetflex.enums.ContentType;
import com.example.neetflex.model.contents.Movie;
import com.example.neetflex.model.user.User;
import com.example.neetflex.patterns.observer.*;
import com.example.neetflex.service.EmailService;
import com.example.neetflex.service.impl.EmailServiceImpl;

/**
 * Demo class to demonstrate the usage of the Notifier Pattern.
 * This class simulates how the pattern would be used in a real application.
 */
public class NotifierPatternDemo {

    public static void main(String[] args) {
        System.out.println("Starting Notifier Pattern Demo...");

        // Step 1: Create the notification service (Subject)
        NotificationService notificationService = new NotificationService();
        System.out.println("NotificationService created.");

        // Create EmailConfig with the correct values
        EmailConfig emailConfig = new EmailConfig();
        emailConfig.setHost("smtp.gmail.com");
        emailConfig.setPort(587);
        emailConfig.setUsername("alinpoiata2006@gmail.com");
        emailConfig.setPassword("pqhf hzjf tdcv gugg");
        emailConfig.setProtocol("smtp");
        emailConfig.setDefaultSender("alinpoiata2006@gmail.com");
        emailConfig.setAuth(true);
        emailConfig.setStarttlsEnable(true);

        // Create EmailService
        EmailService emailService = new EmailServiceImpl(emailConfig);
        System.out.println("EmailService created with config: " + emailConfig.getUsername());

        // Step 2: Create a user notification manager
        // In a real application, this would be autowired by Spring
        UserNotificationManager userNotificationManager = new UserNotificationManager(
                notificationService, 
                null,  // UserService would be autowired in a real app
                null,  // UserRepository would be autowired in a real app
                emailService); // Pass the EmailService
        System.out.println("UserNotificationManager created.");

        // Step 3: Create a content notification service
        ContentNotificationService contentNotificationService = new ContentNotificationService(userNotificationManager);
        System.out.println("ContentNotificationService created.");

        // Step 4: Create some sample users
        User user1 = createSampleUser(1L, "sbogdan.calin", "sbogdan.calin@gmail.com");
        User user2 = createSampleUser(2L, "formysound", "formysound@gmail.com");
        System.out.println("Sample users created.");

        // Step 5: Register users for notifications
        userNotificationManager.registerUser(user1);
        userNotificationManager.registerUser(user2);
        System.out.println("Users registered for notifications.");

        // Step 6: Create a sample movie
        Movie newMovie = createSampleMovie(1L, "The Matrix", "A computer hacker learns about the true nature of reality.");
        System.out.println("Sample movie created.");

        // Step 7: Send notifications
        System.out.println("\n--- Sending New Content Notification ---");
        contentNotificationService.notifyNewContent(newMovie);

        System.out.println("\n--- Sending Content Update Notification ---");
        contentNotificationService.notifyContentUpdate(newMovie);

        System.out.println("\n--- Sending System Announcement ---");
        contentNotificationService.sendSystemAnnouncement(
                "System Maintenance", 
                "The system will be down for maintenance on Saturday from 2-4 AM.");

        // Step 8: Create a direct notification and send it to a specific user
        System.out.println("\n--- Sending Direct Notification to User ---");
        Notification accountNotification = new Notification(
                "Account Update", 
                "Your account settings have been updated.", 
                NotificationType.ACCOUNT_UPDATE);
        userNotificationManager.notifyUser(user1.getId(), accountNotification);

        // Step 9: Unregister a user
        System.out.println("\n--- Unregistering User ---");
        userNotificationManager.unregisterUser(user2.getId());

        // Step 10: Send another notification (only user1 should receive it)
        System.out.println("\n--- Sending Notification After Unregistering User2 ---");
        contentNotificationService.sendSystemAnnouncement(
                "New Feature", 
                "We've added a new feature to our platform!");

        System.out.println("\nNotifier Pattern Demo completed.");
    }

    /**
     * Helper method to create a sample user.
     */
    private static User createSampleUser(Long id, String username, String email) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName("Sample");
        user.setLastName("User");
        return user;
    }

    /**
     * Helper method to create a sample movie.
     */
    private static Movie createSampleMovie(Long id, String title, String description) {
        Movie movie = new Movie();
        movie.setId(id);
        movie.setTitle(title);
        movie.setDescription(description);
        movie.setDirector("Sample Director");
        movie.setGenre("Action");
        movie.setYearReleased(2023);
        return movie;
    }
}
