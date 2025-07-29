package com.notification.service.service;

import com.notification.service.model.Notification;
import com.notification.service.model.User;
import com.notification.service.model.enums.Channel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailNotificationService implements NotificationService {

    @Override
    public boolean send(Notification notification) {
        if (!canSendToUser(notification.getUser()) || notification.getChannel() != Channel.EMAIL) {
            return false;
        }
        
        try {
            // In a real implementation, this would use an email service like JavaMail or a third-party API
            // For this example, we'll just simulate sending an email
            
            String email = notification.getUser().getEmail();
            String subject = "Notification: " + notification.getMessage().getCategory().toString();
            String content = notification.getMessage().getContent();
            
            // Log the email sending (in a real implementation, this would be more robust)
            System.out.println("Sending email to " + email);
            System.out.println("Subject: " + subject);
            System.out.println("Content: " + content);
            
            // Mark the notification as sent
            notification.setSent(true);
            notification.setSentAt(LocalDateTime.now());
            
            return true;
        } catch (Exception e) {
            // Log the error (in a real implementation, this would be more robust)
            System.err.println("Failed to send email notification: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean canSendToUser(User user) {
        return user != null 
               && user.getEmail() != null 
               && !user.getEmail().isEmpty()
               && user.getChannels() != null 
               && user.getChannels().contains(Channel.EMAIL);
    }
}