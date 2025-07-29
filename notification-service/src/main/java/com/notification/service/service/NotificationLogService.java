package com.notification.service.service;

import com.notification.service.model.Notification;
import com.notification.service.model.User;
import com.notification.service.model.enums.Channel;
import com.notification.service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationLogService {

    private final NotificationRepository notificationRepository;

    /**
     * Retrieves all notifications, ordered by creation date (newest first)
     *
     * @param page The page number (0-based)
     * @param size The page size
     * @return A page of notifications
     */
    public Page<Notification> getAllNotifications(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return notificationRepository.findAllOrderByCreatedAtDesc(pageable);
    }

    /**
     * Retrieves notifications for a specific user
     *
     * @param user The user to retrieve notifications for
     * @return A list of notifications for the user
     */
    public List<Notification> getNotificationsForUser(User user) {
        return notificationRepository.findByUser(user);
    }

    /**
     * Retrieves notifications sent through a specific channel
     *
     * @param channel The channel to filter by
     * @return A list of notifications sent through the channel
     */
    public List<Notification> getNotificationsByChannel(Channel channel) {
        return notificationRepository.findByChannel(channel);
    }

    /**
     * Retrieves notifications by sent status
     *
     * @param sent The sent status to filter by
     * @return A list of notifications with the specified sent status
     */
    public List<Notification> getNotificationsBySentStatus(boolean sent) {
        return notificationRepository.findAll().stream()
                .filter(notification -> notification.isSent() == sent)
                .toList();
    }

    /**
     * Counts the total number of notifications
     *
     * @return The total number of notifications
     */
    public long countNotifications() {
        return notificationRepository.count();
    }

    /**
     * Counts the number of successfully sent notifications
     *
     * @return The number of successfully sent notifications
     */
    public long countSentNotifications() {
        return notificationRepository.findAll().stream()
                .filter(Notification::isSent)
                .count();
    }
}