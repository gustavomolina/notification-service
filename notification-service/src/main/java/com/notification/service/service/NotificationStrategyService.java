package com.notification.service.service;

import com.notification.service.model.Message;
import com.notification.service.model.Notification;
import com.notification.service.model.User;
import com.notification.service.model.enums.Channel;
import com.notification.service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationStrategyService {

    private final EmailNotificationService emailNotificationService;
    private final SmsNotificationService smsNotificationService;
    private final PushNotificationService pushNotificationService;
    private final NotificationRepository notificationRepository;
    
    /**
     * Creates and sends notifications for a message to all eligible users
     * 
     * @param message The message to send notifications for
     * @param users The list of users to notify
     * @return A list of successfully sent notifications
     */
    public List<Notification> processNotifications(Message message, List<User> users) {
        List<Notification> sentNotifications = new ArrayList<>();
        
        // Map notification services by channel for easy lookup
        Map<Channel, NotificationService> serviceMap = createServiceMap();
        
        for (User user : users) {
            // Skip users who aren't subscribed to this message's category
            if (user.getSubscriptions() == null || !user.getSubscriptions().contains(message.getCategory())) {
                continue;
            }
            
            // For each channel the user is subscribed to, create and send a notification
            for (Channel channel : user.getChannels()) {
                NotificationService service = serviceMap.get(channel);
                
                // Skip if no service is available for this channel or user can't receive via this channel
                if (service == null || !service.canSendToUser(user)) {
                    continue;
                }
                
                // Create a new notification
                Notification notification = Notification.builder()
                        .message(message)
                        .user(user)
                        .channel(channel)
                        .sent(false)
                        .build();
                
                // Save the notification to the database
                notification = notificationRepository.save(notification);
                
                // Try to send the notification
                boolean sent = service.send(notification);
                
                // If sent successfully, update the notification in the database and add to result list
                if (sent) {
                    notification = notificationRepository.save(notification);
                    sentNotifications.add(notification);
                }
            }
        }
        
        return sentNotifications;
    }
    
    /**
     * Creates a map of notification services by channel
     * 
     * @return A map of notification services by channel
     */
    private Map<Channel, NotificationService> createServiceMap() {
        List<NotificationService> services = List.of(
                emailNotificationService,
                smsNotificationService,
                pushNotificationService
        );
        
        return services.stream()
                .collect(Collectors.toMap(
                        service -> {
                            if (service instanceof EmailNotificationService) {
                                return Channel.EMAIL;
                            } else if (service instanceof SmsNotificationService) {
                                return Channel.SMS;
                            } else if (service instanceof PushNotificationService) {
                                return Channel.PUSH_NOTIFICATION;
                            }
                            throw new IllegalStateException("Unknown notification service type: " + service.getClass());
                        },
                        Function.identity()
                ));
    }
}