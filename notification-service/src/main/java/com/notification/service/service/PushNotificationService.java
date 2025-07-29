package com.notification.service.service;

import com.notification.service.model.Notification;
import com.notification.service.model.User;
import com.notification.service.model.enums.Channel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PushNotificationService implements NotificationService {

    @Override
    public boolean send(Notification notification) {
        if (!canSendToUser(notification.getUser()) || notification.getChannel() != Channel.PUSH_NOTIFICATION) {
            return false;
        }
        
        try {
            // In a real implementation, this would use a push notification service like Firebase Cloud Messaging
            // For this example, we'll just simulate sending a push notification
            
            String userId = notification.getUser().getId().toString();
            String title = notification.getMessage().getCategory().toString();
            String content = notification.getMessage().getContent();
            
            // Log the push notification sending (in a real implementation, this would be more robust)
            System.out.println("Sending push notification to user ID: " + userId);
            System.out.println("Title: " + title);
            System.out.println("Content: " + content);
            
            // Mark the notification as sent
            notification.setSent(true);
            notification.setSentAt(LocalDateTime.now());
            
            return true;
        } catch (Exception e) {
            // Log the error (in a real implementation, this would be more robust)
            System.err.println("Failed to send push notification: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean canSendToUser(User user) {
        return user != null 
               && user.getId() != null 
               && user.getChannels() != null 
               && user.getChannels().contains(Channel.PUSH_NOTIFICATION);
    }
}