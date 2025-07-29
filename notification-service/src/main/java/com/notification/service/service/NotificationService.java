package com.notification.service.service;

import com.notification.service.model.Notification;
import com.notification.service.model.User;

public interface NotificationService {
    
    /**
     * Sends a notification to a user
     * 
     * @param notification The notification to send
     * @return true if the notification was sent successfully, false otherwise
     */
    boolean send(Notification notification);
    
    /**
     * Checks if a user can receive notifications through this service's channel
     * 
     * @param user The user to check
     * @return true if the user can receive notifications through this channel, false otherwise
     */
    boolean canSendToUser(User user);
}