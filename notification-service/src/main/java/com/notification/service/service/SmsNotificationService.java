package com.notification.service.service;

import com.notification.service.model.Notification;
import com.notification.service.model.User;
import com.notification.service.model.enums.Channel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SmsNotificationService implements NotificationService {

    @Override
    public boolean send(Notification notification) {
        if (!canSendToUser(notification.getUser()) || notification.getChannel() != Channel.SMS) {
            return false;
        }
        
        try {
            // In a real implementation, this would use an SMS gateway API
            // For this example, we'll just simulate sending an SMS
            
            String phoneNumber = notification.getUser().getPhoneNumber();
            String message = notification.getMessage().getContent();
            
            // Log the SMS sending (in a real implementation, this would be more robust)
            System.out.println("Sending SMS to " + phoneNumber + ": " + message);
            
            // Mark the notification as sent
            notification.setSent(true);
            notification.setSentAt(LocalDateTime.now());
            
            return true;
        } catch (Exception e) {
            // Log the error (in a real implementation, this would be more robust)
            System.err.println("Failed to send SMS notification: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean canSendToUser(User user) {
        return user != null 
               && user.getPhoneNumber() != null 
               && !user.getPhoneNumber().isEmpty()
               && user.getChannels() != null 
               && user.getChannels().contains(Channel.SMS);
    }
}