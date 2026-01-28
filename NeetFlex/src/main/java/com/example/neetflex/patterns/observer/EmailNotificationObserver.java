package com.example.neetflex.patterns.observer;

import com.example.neetflex.model.user.User;
import com.example.neetflex.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Concrete Observer: Sends email notifications to users.
 */
@Component
public class EmailNotificationObserver implements NotificationObserver {
    private User user;
    private EmailService emailService;

    @Autowired
    public EmailNotificationObserver(EmailService emailService) {
        this.emailService = emailService;
    }

    public EmailNotificationObserver(User user) {
        this.user = user;
    }

    public EmailNotificationObserver(User user, EmailService emailService) {
        this.user = user;
        this.emailService = emailService;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void update(Notification notification) {
        if (user == null) {
            System.out.println("[Observer: EmailNotificationObserver] No user set, cannot send notification");
            return;
        }

        System.out.println("[Observer: EmailNotificationObserver] Preparing email for " + user.getEmail());

        if (emailService != null) {
            // Use the email service to send the actual email
            boolean sent = emailService.sendEmail(
                user.getEmail(), 
                notification.getSubject(), 
                notification.getMessage()
            );

            if (sent) {
                System.out.println("[Observer: EmailNotificationObserver] Email sent successfully to " + user.getEmail());
            } else {
                System.out.println("[Observer: EmailNotificationObserver] Failed to send email to " + user.getEmail());
            }
        } else {
            // Fallback to simulation if email service is not available
            System.out.println(" -> Subject: " + notification.getSubject());
            System.out.println(" -> Body: " + notification.getMessage());
            System.out.println(" -> Notification Type: " + notification.getType());
            System.out.println(" -> Timestamp: " + notification.getTimestamp());
            System.out.println("[Observer: EmailNotificationObserver] Email simulation complete (no EmailService available).");
        }
    }
}
