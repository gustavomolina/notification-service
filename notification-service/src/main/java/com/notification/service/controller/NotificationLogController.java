package com.notification.service.controller;

import com.notification.service.dto.NotificationLogResponse;
import com.notification.service.model.Notification;
import com.notification.service.service.NotificationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class NotificationLogController {

    private final NotificationLogService notificationLogService;

    /**
     * Retrieves all notifications with pagination
     *
     * @param page The page number (0-based)
     * @param size The page size
     * @return A page of notifications
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<Notification> notificationsPage = notificationLogService.getAllNotifications(page, size);
        
        List<NotificationLogResponse> notifications = notificationsPage.getContent().stream()
                .map(NotificationLogResponse::fromNotification)
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("notifications", notifications);
        response.put("currentPage", notificationsPage.getNumber());
        response.put("totalItems", notificationsPage.getTotalElements());
        response.put("totalPages", notificationsPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves notification statistics
     *
     * @return Statistics about notifications
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getNotificationStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalNotifications", notificationLogService.countNotifications());
        stats.put("sentNotifications", notificationLogService.countSentNotifications());
        stats.put("failedNotifications", notificationLogService.countNotifications() - notificationLogService.countSentNotifications());
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Retrieves notifications by sent status
     *
     * @param sent The sent status to filter by
     * @return A list of notifications with the specified sent status
     */
    @GetMapping("/status/{sent}")
    public ResponseEntity<List<NotificationLogResponse>> getNotificationsBySentStatus(@PathVariable boolean sent) {
        List<Notification> notifications = notificationLogService.getNotificationsBySentStatus(sent);
        
        List<NotificationLogResponse> responses = notifications.stream()
                .map(NotificationLogResponse::fromNotification)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
}