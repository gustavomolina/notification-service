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
public class PushNotificationServiceTest {

    @InjectMocks
    private PushNotificationService pushNotificationService;

    private User validUser;
    private User userWithoutId;
    private User userWithoutPushChannel;
    private Message message;
    private Notification validNotification;
    private Notification invalidChannelNotification;

    @BeforeEach
    void setUp() {
        // Set up a valid user with ID and PUSH_NOTIFICATION channel
        Set<Channel> validChannels = new HashSet<>();
        validChannels.add(Channel.PUSH_NOTIFICATION);
        
        Set<Category> subscriptions = new HashSet<>();
        subscriptions.add(Category.SPORTS);
        
        validUser = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .channels(validChannels)
                .subscriptions(subscriptions)
                .build();
        
        // Set up a user without ID (this is a bit artificial since JPA entities typically have IDs,
        // but we're testing the service's behavior with a null ID)
        userWithoutId = User.builder()
                .name("No ID User")
                .email("noid@example.com")
                .channels(validChannels)
                .subscriptions(subscriptions)
                .build();
        
        // Set up a user without PUSH_NOTIFICATION channel
        Set<Channel> emailOnlyChannels = new HashSet<>();
        emailOnlyChannels.add(Channel.EMAIL);
        
        userWithoutPushChannel = User.builder()
                .id(3L)
                .name("Email Only User")
                .email("email@example.com")
                .channels(emailOnlyChannels)
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
                .channel(Channel.PUSH_NOTIFICATION)
                .sent(false)
                .build();
        
        // Set up an invalid channel notification
        invalidChannelNotification = Notification.builder()
                .id(2L)
                .message(message)
                .user(validUser)
                .channel(Channel.EMAIL)
                .sent(false)
                .build();
    }

    @Test
    void canSendToUser_WithValidUser_ReturnsTrue() {
        assertTrue(pushNotificationService.canSendToUser(validUser));
    }

    @Test
    void canSendToUser_WithNullUser_ReturnsFalse() {
        assertFalse(pushNotificationService.canSendToUser(null));
    }

    @Test
    void canSendToUser_WithUserWithoutId_ReturnsFalse() {
        assertFalse(pushNotificationService.canSendToUser(userWithoutId));
    }

    @Test
    void canSendToUser_WithUserWithoutPushChannel_ReturnsFalse() {
        assertFalse(pushNotificationService.canSendToUser(userWithoutPushChannel));
    }

    @Test
    void canSendToUser_WithUserWithNullChannels_ReturnsFalse() {
        User userWithNullChannels = User.builder()
                .id(4L)
                .name("Null Channels User")
                .email("nullchannels@example.com")
                .channels(null)
                .subscriptions(validUser.getSubscriptions())
                .build();
        
        assertFalse(pushNotificationService.canSendToUser(userWithNullChannels));
    }

    @Test
    void send_WithValidNotification_ReturnsTrue() {
        boolean result = pushNotificationService.send(validNotification);
        
        assertTrue(result);
        assertTrue(validNotification.isSent());
        assertNotNull(validNotification.getSentAt());
    }

    @Test
    void send_WithInvalidChannelNotification_ReturnsFalse() {
        boolean result = pushNotificationService.send(invalidChannelNotification);
        
        assertFalse(result);
        assertFalse(invalidChannelNotification.isSent());
        assertNull(invalidChannelNotification.getSentAt());
    }

    @Test
    void send_WithInvalidUser_ReturnsFalse() {
        Notification notification = Notification.builder()
                .id(3L)
                .message(message)
                .user(userWithoutId)
                .channel(Channel.PUSH_NOTIFICATION)
                .sent(false)
                .build();
        
        boolean result = pushNotificationService.send(notification);
        
        assertFalse(result);
        assertFalse(notification.isSent());
        assertNull(notification.getSentAt());
    }
}