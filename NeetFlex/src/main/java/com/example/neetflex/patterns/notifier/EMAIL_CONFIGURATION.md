# Email Configuration for Notifier Pattern

## Overview
This document explains how to configure the email credentials for the Notifier Pattern implementation. The pattern uses these credentials to send email notifications to users.

## Configuration Steps

### 1. Set Up Email Credentials in application.properties

The email configuration is stored in the `application.properties` file. You need to update the following properties with your actual email service credentials:

```properties
# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your.email@gmail.com
spring.mail.password=your-app-password
spring.mail.protocol=smtp
spring.mail.default-sender=your.email@gmail.com
spring.mail.auth=true
spring.mail.starttls-enable=true
```

Replace the placeholder values with your actual email service details:

- `spring.mail.host`: Your SMTP server host (e.g., smtp.gmail.com for Gmail)
- `spring.mail.port`: Your SMTP server port (typically 587 for TLS or 465 for SSL)
- `spring.mail.username`: Your email address
- `spring.mail.password`: Your email password or app-specific password
- `spring.mail.default-sender`: The email address that will appear as the sender

### 2. Gmail-Specific Setup

If you're using Gmail, you'll need to:

1. Enable "Less secure app access" in your Google account settings, OR
2. Create an "App Password" if you have 2-factor authentication enabled:
   - Go to your Google Account > Security > App passwords
   - Select "Mail" and your device
   - Click "Generate"
   - Use the generated password in your application.properties

### 3. Testing Your Configuration

You can test your email configuration by:

1. Running the application
2. Registering a user for notifications
3. Triggering a notification (e.g., adding new content)

The system will use your configured email credentials to send the notification.

## Implementation Details

The email sending functionality is implemented in the following classes:

1. `EmailConfig`: Loads email configuration from application.properties
2. `EmailService`: Interface defining email operations
3. `EmailServiceImpl`: Implementation that sends emails using the configured credentials
4. `EmailNotificationObserver`: Uses the EmailService to send notifications to users

## Example Usage

```java
// In your application code:

// 1. Get a user
User user = userService.findByUsername("john.doe");

// 2. Register the user for notifications
userNotificationManager.registerUser(user);

// 3. Send a notification
contentNotificationService.notifyNewContent(newMovie);
```

When a notification is triggered, the system will use the configured email credentials to send an email to the user.