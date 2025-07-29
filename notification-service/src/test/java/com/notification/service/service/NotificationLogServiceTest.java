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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationLogServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationLogService notificationLogService;

    private User user1;
    private User user2;
    private Message message;
    private Notification emailNotification;
    private Notification smsNotification;
    private Notification pushNotification;
    private Notification failedNotification;
    private List<Notification> allNotifications;

    @BeforeEach
    void setUp() {
        // Set up users
        user1 = User.builder()
                .id(1L)
                .name("User 1")
                .email("user1@example.com")
                .phoneNumber("+1234567890")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("User 2")
                .email("user2@example.com")
                .phoneNumber("+0987654321")
                .build();

        // Set up message
        message = Message.builder()
                .id(1L)
                .category(Category.SPORTS)
                .content("Sports news")
                .createdAt(LocalDateTime.now())
                .build();

        // Set up notifications
        LocalDateTime now = LocalDateTime.now();
        
        emailNotification = Notification.builder()
                .id(1L)
                .message(message)
                .user(user1)
                .channel(Channel.EMAIL)
                .sent(true)
                .createdAt(now.minusMinutes(5))
                .sentAt(now.minusMinutes(4))
                .build();

        smsNotification = Notification.builder()
                .id(2L)
                .message(message)
                .user(user1)
                .channel(Channel.SMS)
                .sent(true)
                .createdAt(now.minusMinutes(3))
                .sentAt(now.minusMinutes(2))
                .build();

        pushNotification = Notification.builder()
                .id(3L)
                .message(message)
                .user(user2)
                .channel(Channel.PUSH_NOTIFICATION)
                .sent(true)
                .createdAt(now.minusMinutes(1))
                .sentAt(now)
                .build();

        failedNotification = Notification.builder()
                .id(4L)
                .message(message)
                .user(user2)
                .channel(Channel.EMAIL)
                .sent(false)
                .createdAt(now.minusMinutes(6))
                .sentAt(null)
                .build();

        // Combine all notifications
        allNotifications = new ArrayList<>();
        allNotifications.add(emailNotification);
        allNotifications.add(smsNotification);
        allNotifications.add(pushNotification);
        allNotifications.add(failedNotification);
    }

    @Test
    void getAllNotifications_Success() {
        // Set up mock behavior
        Page<Notification> notificationPage = new PageImpl<>(allNotifications);
        when(notificationRepository.findAllOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(notificationPage);

        // Call the method under test
        Page<Notification> result = notificationLogService.getAllNotifications(0, 10);

        // Verify the result
        assertNotNull(result);
        assertEquals(4, result.getTotalElements());
        assertEquals(allNotifications, result.getContent());

        // Verify interactions with mocks
        verify(notificationRepository, times(1)).findAllOrderByCreatedAtDesc(any(Pageable.class));
    }

    @Test
    void getAllNotifications_EmptyList() {
        // Set up mock behavior
        Page<Notification> emptyPage = new PageImpl<>(Collections.emptyList());
        when(notificationRepository.findAllOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(emptyPage);

        // Call the method under test
        Page<Notification> result = notificationLogService.getAllNotifications(0, 10);

        // Verify the result
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());

        // Verify interactions with mocks
        verify(notificationRepository, times(1)).findAllOrderByCreatedAtDesc(any(Pageable.class));
    }

    @Test
    void getAllNotifications_CorrectPageable() {
        // Set up mock behavior
        when(notificationRepository.findAllOrderByCreatedAtDesc(any(Pageable.class))).thenAnswer(invocation -> {
            Pageable pageable = invocation.getArgument(0);
            assertEquals(2, pageable.getPageNumber());
            assertEquals(5, pageable.getPageSize());
            assertEquals(Sort.by("createdAt").descending(), pageable.getSort());
            return new PageImpl<>(Collections.emptyList());
        });

        // Call the method under test
        notificationLogService.getAllNotifications(2, 5);

        // Verify interactions with mocks
        verify(notificationRepository, times(1)).findAllOrderByCreatedAtDesc(any(Pageable.class));
    }

    @Test
    void getNotificationsForUser_Success() {
        // Set up mock behavior
        when(notificationRepository.findByUser(user1)).thenReturn(Arrays.asList(emailNotification, smsNotification));

        // Call the method under test
        List<Notification> result = notificationLogService.getNotificationsForUser(user1);

        // Verify the result
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(emailNotification));
        assertTrue(result.contains(smsNotification));

        // Verify interactions with mocks
        verify(notificationRepository, times(1)).findByUser(user1);
    }

    @Test
    void getNotificationsForUser_NoNotifications() {
        // Set up mock behavior
        when(notificationRepository.findByUser(any(User.class))).thenReturn(Collections.emptyList());

        // Call the method under test
        List<Notification> result = notificationLogService.getNotificationsForUser(user1);

        // Verify the result
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify interactions with mocks
        verify(notificationRepository, times(1)).findByUser(user1);
    }

    @Test
    void getNotificationsByChannel_Success() {
        // Set up mock behavior
        when(notificationRepository.findByChannel(Channel.EMAIL)).thenReturn(Arrays.asList(emailNotification, failedNotification));

        // Call the method under test
        List<Notification> result = notificationLogService.getNotificationsByChannel(Channel.EMAIL);

        // Verify the result
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(emailNotification));
        assertTrue(result.contains(failedNotification));

        // Verify interactions with mocks
        verify(notificationRepository, times(1)).findByChannel(Channel.EMAIL);
    }

    @Test
    void getNotificationsByChannel_NoNotifications() {
        // Set up mock behavior
        when(notificationRepository.findByChannel(any(Channel.class))).thenReturn(Collections.emptyList());

        // Call the method under test
        List<Notification> result = notificationLogService.getNotificationsByChannel(Channel.SMS);

        // Verify the result
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify interactions with mocks
        verify(notificationRepository, times(1)).findByChannel(Channel.SMS);
    }

    @Test
    void getNotificationsBySentStatus_Sent() {
        // Set up mock behavior
        when(notificationRepository.findAll()).thenReturn(allNotifications);

        // Call the method under test
        List<Notification> result = notificationLogService.getNotificationsBySentStatus(true);

        // Verify the result
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains(emailNotification));
        assertTrue(result.contains(smsNotification));
        assertTrue(result.contains(pushNotification));
        assertFalse(result.contains(failedNotification));

        // Verify interactions with mocks
        verify(notificationRepository, times(1)).findAll();
    }

    @Test
    void getNotificationsBySentStatus_NotSent() {
        // Set up mock behavior
        when(notificationRepository.findAll()).thenReturn(allNotifications);

        // Call the method under test
        List<Notification> result = notificationLogService.getNotificationsBySentStatus(false);

        // Verify the result
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(failedNotification));

        // Verify interactions with mocks
        verify(notificationRepository, times(1)).findAll();
    }

    @Test
    void countNotifications_Success() {
        // Set up mock behavior
        when(notificationRepository.count()).thenReturn(4L);

        // Call the method under test
        long result = notificationLogService.countNotifications();

        // Verify the result
        assertEquals(4L, result);

        // Verify interactions with mocks
        verify(notificationRepository, times(1)).count();
    }

    @Test
    void countSentNotifications_Success() {
        // Set up mock behavior
        when(notificationRepository.findAll()).thenReturn(allNotifications);

        // Call the method under test
        long result = notificationLogService.countSentNotifications();

        // Verify the result
        assertEquals(3L, result);

        // Verify interactions with mocks
        verify(notificationRepository, times(1)).findAll();
    }
}