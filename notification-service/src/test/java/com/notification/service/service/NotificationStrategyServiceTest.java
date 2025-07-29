package com.notification.service.service;

import com.notification.service.model.Message;
import com.notification.service.model.Notification;
import com.notification.service.model.User;
import com.notification.service.model.enums.Category;
import com.notification.service.model.enums.Channel;
import com.notification.service.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationStrategyServiceTest {

    @Mock
    private EmailNotificationService emailNotificationService;

    @Mock
    private SmsNotificationService smsNotificationService;

    @Mock
    private PushNotificationService pushNotificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationStrategyService notificationStrategyService;

    private Message sportsMessage;
    private Message financeMessage;
    private User userWithAllChannels;
    private User userWithEmailOnly;
    private User userWithSmsOnly;
    private User userWithNoSubscriptions;
    private User userWithSportsSubscription;
    private User userWithFinanceSubscription;

    @BeforeEach
    void setUp() {
        // Set up messages
        sportsMessage = Message.builder()
                .id(1L)
                .category(Category.SPORTS)
                .content("Sports news")
                .build();

        financeMessage = Message.builder()
                .id(2L)
                .category(Category.FINANCE)
                .content("Finance news")
                .build();

        // Set up users with different channel preferences
        Set<Channel> allChannels = new HashSet<>();
        allChannels.add(Channel.EMAIL);
        allChannels.add(Channel.SMS);
        allChannels.add(Channel.PUSH_NOTIFICATION);

        Set<Channel> emailOnly = new HashSet<>();
        emailOnly.add(Channel.EMAIL);

        Set<Channel> smsOnly = new HashSet<>();
        smsOnly.add(Channel.SMS);

        // Set up users with different subscription preferences
        Set<Category> sportsSubscription = new HashSet<>();
        sportsSubscription.add(Category.SPORTS);

        Set<Category> financeSubscription = new HashSet<>();
        financeSubscription.add(Category.FINANCE);

        Set<Category> allSubscriptions = new HashSet<>();
        allSubscriptions.add(Category.SPORTS);
        allSubscriptions.add(Category.FINANCE);
        allSubscriptions.add(Category.MOVIES);

        // Create users
        userWithAllChannels = User.builder()
                .id(1L)
                .name("All Channels User")
                .email("all@example.com")
                .phoneNumber("+1234567890")
                .channels(allChannels)
                .subscriptions(allSubscriptions)
                .build();

        userWithEmailOnly = User.builder()
                .id(2L)
                .name("Email Only User")
                .email("email@example.com")
                .channels(emailOnly)
                .subscriptions(allSubscriptions)
                .build();

        userWithSmsOnly = User.builder()
                .id(3L)
                .name("SMS Only User")
                .phoneNumber("+0987654321")
                .channels(smsOnly)
                .subscriptions(allSubscriptions)
                .build();

        userWithNoSubscriptions = User.builder()
                .id(4L)
                .name("No Subscriptions User")
                .email("nosub@example.com")
                .phoneNumber("+1122334455")
                .channels(allChannels)
                .subscriptions(Collections.emptySet())
                .build();

        userWithSportsSubscription = User.builder()
                .id(5L)
                .name("Sports Fan")
                .email("sports@example.com")
                .phoneNumber("+5566778899")
                .channels(allChannels)
                .subscriptions(sportsSubscription)
                .build();

        userWithFinanceSubscription = User.builder()
                .id(6L)
                .name("Finance Fan")
                .email("finance@example.com")
                .phoneNumber("+9988776655")
                .channels(allChannels)
                .subscriptions(financeSubscription)
                .build();

        // Set up mock behavior for notification services
        when(emailNotificationService.canSendToUser(userWithAllChannels)).thenReturn(true);
        when(emailNotificationService.canSendToUser(userWithEmailOnly)).thenReturn(true);
        when(emailNotificationService.canSendToUser(userWithSmsOnly)).thenReturn(false);
        when(emailNotificationService.canSendToUser(userWithSportsSubscription)).thenReturn(true);
        when(emailNotificationService.canSendToUser(userWithFinanceSubscription)).thenReturn(true);

        when(smsNotificationService.canSendToUser(userWithAllChannels)).thenReturn(true);
        when(smsNotificationService.canSendToUser(userWithEmailOnly)).thenReturn(false);
        when(smsNotificationService.canSendToUser(userWithSmsOnly)).thenReturn(true);
        when(smsNotificationService.canSendToUser(userWithSportsSubscription)).thenReturn(true);
        when(smsNotificationService.canSendToUser(userWithFinanceSubscription)).thenReturn(true);

        when(pushNotificationService.canSendToUser(userWithAllChannels)).thenReturn(true);
        when(pushNotificationService.canSendToUser(userWithEmailOnly)).thenReturn(false);
        when(pushNotificationService.canSendToUser(userWithSmsOnly)).thenReturn(false);
        when(pushNotificationService.canSendToUser(userWithSportsSubscription)).thenReturn(true);
        when(pushNotificationService.canSendToUser(userWithFinanceSubscription)).thenReturn(true);

        // Set up mock behavior for notification repository
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void processNotifications_WithNoUsers_ReturnsEmptyList() {
        List<Notification> result = notificationStrategyService.processNotifications(sportsMessage, Collections.emptyList());
        
        assertTrue(result.isEmpty());
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void processNotifications_WithNullUsers_ReturnsEmptyList() {
        List<Notification> result = notificationStrategyService.processNotifications(sportsMessage, null);
        
        assertTrue(result.isEmpty());
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void processNotifications_WithNoSubscribedUsers_ReturnsEmptyList() {
        List<Notification> result = notificationStrategyService.processNotifications(
                sportsMessage, 
                Collections.singletonList(userWithFinanceSubscription)
        );
        
        assertTrue(result.isEmpty());
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void processNotifications_WithUserWithNoSubscriptions_ReturnsEmptyList() {
        List<Notification> result = notificationStrategyService.processNotifications(
                sportsMessage, 
                Collections.singletonList(userWithNoSubscriptions)
        );
        
        assertTrue(result.isEmpty());
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void processNotifications_WithSubscribedUserWithAllChannels_SendsToAllChannels() {
        // Set up mock behavior for notification services to return success
        when(emailNotificationService.send(any(Notification.class))).thenReturn(true);
        when(smsNotificationService.send(any(Notification.class))).thenReturn(true);
        when(pushNotificationService.send(any(Notification.class))).thenReturn(true);
        
        List<Notification> result = notificationStrategyService.processNotifications(
                sportsMessage, 
                Collections.singletonList(userWithAllChannels)
        );
        
        assertEquals(3, result.size());
        verify(emailNotificationService, times(1)).send(any(Notification.class));
        verify(smsNotificationService, times(1)).send(any(Notification.class));
        verify(pushNotificationService, times(1)).send(any(Notification.class));
        verify(notificationRepository, times(6)).save(any(Notification.class)); // 3 before send + 3 after send
    }

    @Test
    void processNotifications_WithSubscribedUserWithEmailOnly_SendsToEmailOnly() {
        // Set up mock behavior for notification services to return success
        when(emailNotificationService.send(any(Notification.class))).thenReturn(true);
        
        List<Notification> result = notificationStrategyService.processNotifications(
                sportsMessage, 
                Collections.singletonList(userWithEmailOnly)
        );
        
        assertEquals(1, result.size());
        verify(emailNotificationService, times(1)).send(any(Notification.class));
        verify(smsNotificationService, never()).send(any(Notification.class));
        verify(pushNotificationService, never()).send(any(Notification.class));
        verify(notificationRepository, times(2)).save(any(Notification.class)); // 1 before send + 1 after send
    }

    @Test
    void processNotifications_WithFailedDelivery_DoesNotAddToResult() {
        // Set up mock behavior for notification services to return failure
        when(emailNotificationService.send(any(Notification.class))).thenReturn(false);
        when(smsNotificationService.send(any(Notification.class))).thenReturn(true);
        when(pushNotificationService.send(any(Notification.class))).thenReturn(false);
        
        List<Notification> result = notificationStrategyService.processNotifications(
                sportsMessage, 
                Collections.singletonList(userWithAllChannels)
        );
        
        assertEquals(1, result.size()); // Only SMS succeeded
        verify(emailNotificationService, times(1)).send(any(Notification.class));
        verify(smsNotificationService, times(1)).send(any(Notification.class));
        verify(pushNotificationService, times(1)).send(any(Notification.class));
        verify(notificationRepository, times(4)).save(any(Notification.class)); // 3 before send + 1 after send (for SMS)
    }

    @Test
    void processNotifications_WithMultipleUsers_ProcessesAllUsers() {
        // Set up mock behavior for notification services to return success
        when(emailNotificationService.send(any(Notification.class))).thenReturn(true);
        when(smsNotificationService.send(any(Notification.class))).thenReturn(true);
        when(pushNotificationService.send(any(Notification.class))).thenReturn(true);
        
        List<User> users = Arrays.asList(
                userWithSportsSubscription,
                userWithFinanceSubscription,
                userWithNoSubscriptions
        );
        
        List<Notification> result = notificationStrategyService.processNotifications(sportsMessage, users);
        
        assertEquals(3, result.size()); // Only userWithSportsSubscription gets notifications (3 channels)
        verify(notificationRepository, times(6)).save(any(Notification.class)); // 3 before send + 3 after send
    }
}