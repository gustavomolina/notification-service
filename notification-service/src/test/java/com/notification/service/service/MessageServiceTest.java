package com.notification.service.service;

import com.notification.service.model.Message;
import com.notification.service.model.Notification;
import com.notification.service.model.User;
import com.notification.service.model.enums.Category;
import com.notification.service.repository.MessageRepository;
import com.notification.service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationStrategyService notificationStrategyService;

    @InjectMocks
    private MessageService messageService;

    private Message sportsMessage;
    private Message financeMessage;
    private List<User> subscribedUsers;
    private List<Notification> sentNotifications;

    @BeforeEach
    void setUp() {
        // Set up test messages
        sportsMessage = Message.builder()
                .id(1L)
                .category(Category.SPORTS)
                .content("Sports news")
                .createdAt(LocalDateTime.now())
                .build();

        financeMessage = Message.builder()
                .id(2L)
                .category(Category.FINANCE)
                .content("Finance news")
                .createdAt(LocalDateTime.now())
                .build();

        // Set up subscribed users
        subscribedUsers = new ArrayList<>();
        subscribedUsers.add(User.builder().id(1L).name("User 1").build());
        subscribedUsers.add(User.builder().id(2L).name("User 2").build());

        // Set up sent notifications
        sentNotifications = new ArrayList<>();
        sentNotifications.add(Notification.builder().id(1L).build());
        sentNotifications.add(Notification.builder().id(2L).build());
    }

    @Test
    void createMessage_Success() {
        // Set up mock behavior
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message message = invocation.getArgument(0);
            if (message.getId() == null) {
                message.setId(1L);
            }
            return message;
        });
        when(userRepository.findBySubscription(Category.SPORTS)).thenReturn(subscribedUsers);
        when(notificationStrategyService.processNotifications(any(Message.class), anyList())).thenReturn(sentNotifications);

        // Call the method under test
        Message result = messageService.createMessage(Category.SPORTS, "Sports news");

        // Verify the result
        assertNotNull(result);
        assertEquals(Category.SPORTS, result.getCategory());
        assertEquals("Sports news", result.getContent());

        // Verify interactions with mocks
        verify(messageRepository, times(1)).save(any(Message.class));
        verify(userRepository, times(1)).findBySubscription(Category.SPORTS);
        verify(notificationStrategyService, times(1)).processNotifications(any(Message.class), eq(subscribedUsers));
    }

    @Test
    void createMessage_NoSubscribedUsers() {
        // Set up mock behavior
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message message = invocation.getArgument(0);
            if (message.getId() == null) {
                message.setId(1L);
            }
            return message;
        });
        when(userRepository.findBySubscription(Category.MOVIES)).thenReturn(Collections.emptyList());

        // Call the method under test
        Message result = messageService.createMessage(Category.MOVIES, "Movies news");

        // Verify the result
        assertNotNull(result);
        assertEquals(Category.MOVIES, result.getCategory());
        assertEquals("Movies news", result.getContent());

        // Verify interactions with mocks
        verify(messageRepository, times(1)).save(any(Message.class));
        verify(userRepository, times(1)).findBySubscription(Category.MOVIES);
        verify(notificationStrategyService, times(1)).processNotifications(any(Message.class), eq(Collections.emptyList()));
    }

    @Test
    void getAllMessages_Success() {
        // Set up mock behavior
        when(messageRepository.findAll()).thenReturn(Arrays.asList(sportsMessage, financeMessage));

        // Call the method under test
        List<Message> result = messageService.getAllMessages();

        // Verify the result
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(sportsMessage, result.get(0));
        assertEquals(financeMessage, result.get(1));

        // Verify interactions with mocks
        verify(messageRepository, times(1)).findAll();
    }

    @Test
    void getAllMessages_EmptyList() {
        // Set up mock behavior
        when(messageRepository.findAll()).thenReturn(Collections.emptyList());

        // Call the method under test
        List<Message> result = messageService.getAllMessages();

        // Verify the result
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify interactions with mocks
        verify(messageRepository, times(1)).findAll();
    }

    @Test
    void getMessageById_Found() {
        // Set up mock behavior
        when(messageRepository.findById(1L)).thenReturn(Optional.of(sportsMessage));

        // Call the method under test
        Message result = messageService.getMessageById(1L);

        // Verify the result
        assertNotNull(result);
        assertEquals(sportsMessage, result);

        // Verify interactions with mocks
        verify(messageRepository, times(1)).findById(1L);
    }

    @Test
    void getMessageById_NotFound() {
        // Set up mock behavior
        when(messageRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Call the method under test
        Message result = messageService.getMessageById(999L);

        // Verify the result
        assertNull(result);

        // Verify interactions with mocks
        verify(messageRepository, times(1)).findById(999L);
    }

    @Test
    void getMessagesByCategory_Found() {
        // Set up mock behavior
        when(messageRepository.findByCategory(Category.SPORTS)).thenReturn(Collections.singletonList(sportsMessage));

        // Call the method under test
        List<Message> result = messageService.getMessagesByCategory(Category.SPORTS);

        // Verify the result
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sportsMessage, result.get(0));

        // Verify interactions with mocks
        verify(messageRepository, times(1)).findByCategory(Category.SPORTS);
    }

    @Test
    void getMessagesByCategory_NotFound() {
        // Set up mock behavior
        when(messageRepository.findByCategory(Category.MOVIES)).thenReturn(Collections.emptyList());

        // Call the method under test
        List<Message> result = messageService.getMessagesByCategory(Category.MOVIES);

        // Verify the result
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify interactions with mocks
        verify(messageRepository, times(1)).findByCategory(Category.MOVIES);
    }
}