package com.example.neetflex.service;

/**
 * Interface for email service operations.
 * This is a simplified version that doesn't rely on external mail libraries.
 */
public interface EmailService {
    /**
     * Sends an email to a recipient.
     *
     * @param to      The recipient's email address
     * @param subject The email subject
     * @param body    The email body content
     * @return true if the email was sent successfully, false otherwise
     */
    boolean sendEmail(String to, String subject, String body);

    /**
     * Gets the email address used to send notifications.
     * 
     * @return The sender's email address
     */
    String getSenderEmail();
}
