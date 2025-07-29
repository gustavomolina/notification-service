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
public class SmsNotificationServiceTest {

    @InjectMocks
    private SmsNotificationService smsNotificationService;

    private User validUser;
    private User userWithoutPhoneNumber;
    private User userWithoutSmsChannel;
    private Message message;
    private Notification validNotification;
    private Notification invalidChannelNotification;

    @BeforeEach
    void setUp() {
        // Set up a valid user with phone number and SMS channel
        Set<Channel> validChannels = new HashSet<>();
        validChannels.add(Channel.SMS);
        
        Set<Category> subscriptions = new HashSet<>();
        subscriptions.add(Category.SPORTS);
        
        validUser = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .phoneNumber("+1234567890")
                .channels(validChannels)
                .subscriptions(subscriptions)
                .build();
        
        // Set up a user without phone number
        userWithoutPhoneNumber = User.builder()
                .id(2L)
                .name("No Phone User")
                .email("nophone@example.com")
                .channels(validChannels)
                .subscriptions(subscriptions)
                .build();
        
        // Set up a user without SMS channel
        Set<Channel> emailOnlyChannels = new HashSet<>();
        emailOnlyChannels.add(Channel.EMAIL);
        
        userWithoutSmsChannel = User.builder()
                .id(3L)
                .name("Email Only User")
                .email("email@example.com")
                .phoneNumber("+0987654321")
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
                .channel(Channel.SMS)
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
        assertTrue(smsNotificationService.canSendToUser(validUser));
    }

    @Test
    void canSendToUser_WithNullUser_ReturnsFalse() {
        assertFalse(smsNotificationService.canSendToUser(null));
    }

    @Test
    void canSendToUser_WithUserWithoutPhoneNumber_ReturnsFalse() {
        assertFalse(smsNotificationService.canSendToUser(userWithoutPhoneNumber));
    }

    @Test
    void canSendToUser_WithUserWithoutSmsChannel_ReturnsFalse() {
        assertFalse(smsNotificationService.canSendToUser(userWithoutSmsChannel));
    }

    @Test
    void canSendToUser_WithUserWithEmptyPhoneNumber_ReturnsFalse() {
        User userWithEmptyPhoneNumber = User.builder()
                .id(4L)
                .name("Empty Phone User")
                .email("emptyphone@example.com")
                .phoneNumber("")
                .channels(validUser.getChannels())
                .subscriptions(validUser.getSubscriptions())
                .build();
        
        assertFalse(smsNotificationService.canSendToUser(userWithEmptyPhoneNumber));
    }

    @Test
    void send_WithValidNotification_ReturnsTrue() {
        boolean result = smsNotificationService.send(validNotification);
        
        assertTrue(result);
        assertTrue(validNotification.isSent());
        assertNotNull(validNotification.getSentAt());
    }

    @Test
    void send_WithInvalidChannelNotification_ReturnsFalse() {
        boolean result = smsNotificationService.send(invalidChannelNotification);
        
        assertFalse(result);
        assertFalse(invalidChannelNotification.isSent());
        assertNull(invalidChannelNotification.getSentAt());
    }

    @Test
    void send_WithInvalidUser_ReturnsFalse() {
        Notification notification = Notification.builder()
                .id(3L)
                .message(message)
                .user(userWithoutPhoneNumber)
                .channel(Channel.SMS)
                .sent(false)
                .build();
        
        boolean result = smsNotificationService.send(notification);
        
        assertFalse(result);
        assertFalse(notification.isSent());
        assertNull(notification.getSentAt());
    }
}