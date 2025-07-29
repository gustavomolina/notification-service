package com.notification.service.service;

import com.notification.service.model.Message;
import com.notification.service.model.Notification;
import com.notification.service.model.User;
import com.notification.service.model.enums.Category;
import com.notification.service.repository.MessageRepository;
import com.notification.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final NotificationStrategyService notificationStrategyService;

    /**
     * Creates a new message and sends notifications to all eligible users
     *
     * @param category The category of the message
     * @param content The content of the message
     * @return The created message
     */
    @Transactional
    public Message createMessage(Category category, String content) {
        // Create and save the message
        Message message = Message.builder()
                .category(category)
                .content(content)
                .build();
        
        message = messageRepository.save(message);
        
        // Find all users subscribed to this category
        List<User> subscribedUsers = userRepository.findBySubscription(category);
        
        // Process notifications for these users
        List<Notification> sentNotifications = notificationStrategyService.processNotifications(message, subscribedUsers);
        
        // Log the results
        System.out.println("Message created: " + message.getId());
        System.out.println("Notifications sent: " + sentNotifications.size());
        
        return message;
    }

    /**
     * Retrieves all messages, ordered by creation date (newest first)
     *
     * @return A list of all messages
     */
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    /**
     * Retrieves a message by its ID
     *
     * @param id The ID of the message
     * @return The message, or null if not found
     */
    public Message getMessageById(Long id) {
        return messageRepository.findById(id).orElse(null);
    }

    /**
     * Retrieves messages by category
     *
     * @param category The category to filter by
     * @return A list of messages in the specified category
     */
    public List<Message> getMessagesByCategory(Category category) {
        return messageRepository.findByCategory(category);
    }
}