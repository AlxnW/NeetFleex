package com.example.neetflex.patterns.observer;

import java.time.LocalDateTime;

/**
 * Represents a notification message that can be sent to users.
 */
public class Notification {
    private String subject;
    private String message;
    private LocalDateTime timestamp;
    private NotificationType type;

    public Notification(String subject, String message, NotificationType type) {
        this.subject = subject;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.type = type;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public NotificationType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "subject='" + subject + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", type=" + type +
                '}';
    }
}