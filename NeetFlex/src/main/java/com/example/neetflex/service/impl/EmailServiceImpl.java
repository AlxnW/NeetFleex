package com.example.neetflex.service.impl;

import com.example.neetflex.config.EmailConfig;
import com.example.neetflex.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * Implementation of the EmailService interface.
 * This implementation uses JavaMail to send actual emails.
 */
@Service
public class EmailServiceImpl implements EmailService {

    private final EmailConfig emailConfig;
    private final JavaMailSender mailSender;

    @Autowired
    public EmailServiceImpl(EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
        this.mailSender = createMailSender();
    }

    private JavaMailSender createMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailConfig.getHost());
        mailSender.setPort(emailConfig.getPort());
        mailSender.setUsername(emailConfig.getUsername());
        mailSender.setPassword(emailConfig.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", emailConfig.getProtocol());
        props.put("mail.smtp.auth", String.valueOf(emailConfig.isAuth()));
        props.put("mail.smtp.starttls.enable", String.valueOf(emailConfig.isStarttlsEnable()));
        props.put("mail.debug", "true"); // Enable for debugging

        return mailSender;
    }

    @Override
    public boolean sendEmail(String to, String subject, String body) {
        try {
            // Log the email details before sending
            System.out.println("\n=== SENDING REAL EMAIL ===");
            System.out.println("From: " + emailConfig.getUsername() + " <" + emailConfig.getDefaultSender() + ">");
            System.out.println("To: " + to);
            System.out.println("Subject: " + subject);
            System.out.println("Using SMTP Server: " + emailConfig.getHost() + ":" + emailConfig.getPort());

            // Create a simple mail message
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailConfig.getDefaultSender());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            // Send the email
            mailSender.send(message);

            System.out.println("Email sent successfully!");
            System.out.println("=== END OF EMAIL SENDING ===\n");

            return true;
        } catch (Exception e) {
            System.err.println("[Service: EmailService] Failed to send email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getSenderEmail() {
        return emailConfig.getDefaultSender();
    }
}
