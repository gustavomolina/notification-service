package com.notification.service.service;

import com.notification.service.model.Message;
import com.notification.service.model.Notification;
import com.notification.service.model.User;
import com.notification.service.model.enums.Category;
import com.notification.service.model.enums.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class EmailNotificationServiceTest {

    @InjectMocks
    private EmailNotificationService emailNotificationService;

    private User validUser;
    private User userWithoutEmail;
    private User userWithoutEmailChannel;
    private Message message;
    private Notification validNotification;
    private Notification invalidChannelNotification;

    @BeforeEach
    void setUp() {
        // Set up a valid user with email and EMAIL channel
        Set<Channel> validChannels = new HashSet<>();
        validChannels.add(Channel.EMAIL);
        
        Set<Category> subscriptions = new HashSet<>();
        subscriptions.add(Category.SPORTS);
        
        validUser = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .channels(validChannels)
                .subscriptions(subscriptions)
                .build();
        
        // Set up a user without email
        userWithoutEmail = User.builder()
                .id(2L)
                .name("No Email User")
                .channels(validChannels)
                .subscriptions(subscriptions)
                .build();
        
        // Set up a user without EMAIL channel
        Set<Channel> smsOnlyChannels = new HashSet<>();
        smsOnlyChannels.add(Channel.SMS);
        
        userWithoutEmailChannel = User.builder()
                .id(3L)
                .name("SMS Only User")
                .email("sms@example.com")
                .channels(smsOnlyChannels)
                .subscriptions(subscriptions)
                .build();
        
        // Set up a message
        message = Message.builder()
                .id(1L)
                .category(Category.SPORTS)
                .content("Test message content")
                .build();
        
        // Set up a valid notification
        validNotification = Notification.builder()
                .id(1L)
                .message(message)
                .user(validUser)
                .channel(Channel.EMAIL)
                .sent(false)
                .build();
        
        // Set up an invalid channel notification
        invalidChannelNotification = Notification.builder()
                .id(2L)
                .message(message)
                .user(validUser)
                .channel(Channel.SMS)
                .sent(false)
                .build();
    }

    @Test
    void canSendToUser_WithValidUser_ReturnsTrue() {
        assertTrue(emailNotificationService.canSendToUser(validUser));
    }

    @Test
    void canSendToUser_WithNullUser_ReturnsFalse() {
        assertFalse(emailNotificationService.canSendToUser(null));
    }

    @Test
    void canSendToUser_WithUserWithoutEmail_ReturnsFalse() {
        assertFalse(emailNotificationService.canSendToUser(userWithoutEmail));
    }

    @Test
    void canSendToUser_WithUserWithoutEmailChannel_ReturnsFalse() {
        assertFalse(emailNotificationService.canSendToUser(userWithoutEmailChannel));
    }

    @Test
    void send_WithValidNotification_ReturnsTrue() {
        boolean result = emailNotificationService.send(validNotification);
        
        assertTrue(result);
        assertTrue(validNotification.isSent());
        assertNotNull(validNotification.getSentAt());
    }

    @Test
    void send_WithInvalidChannelNotification_ReturnsFalse() {
        boolean result = emailNotificationService.send(invalidChannelNotification);
        
        assertFalse(result);
        assertFalse(invalidChannelNotification.isSent());
        assertNull(invalidChannelNotification.getSentAt());
    }

    @Test
    void send_WithInvalidUser_ReturnsFalse() {
        Notification notification = Notification.builder()
                .id(3L)
                .message(message)
                .user(userWithoutEmail)
                .channel(Channel.EMAIL)
                .sent(false)
                .build();
        
        boolean result = emailNotificationService.send(notification);
        
        assertFalse(result);
        assertFalse(notification.isSent());
        assertNull(notification.getSentAt());
    }
}