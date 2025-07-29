package com.notification.service.dto;

import com.notification.service.model.Notification;
import com.notification.service.model.enums.Category;
import com.notification.service.model.enums.Channel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationLogResponse {

    private Long id;
    private Long messageId;
    private Category messageCategory;
    private String messageContent;
    private Long userId;
    private String userName;
    private String userEmail;
    private String userPhoneNumber;
    private Channel channel;
    private boolean sent;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;

    public static NotificationLogResponse fromNotification(Notification notification) {
        return NotificationLogResponse.builder()
                .id(notification.getId())
                .messageId(notification.getMessage().getId())
                .messageCategory(notification.getMessage().getCategory())
                .messageContent(notification.getMessage().getContent())
                .userId(notification.getUser().getId())
                .userName(notification.getUser().getName())
                .userEmail(notification.getUser().getEmail())
                .userPhoneNumber(notification.getUser().getPhoneNumber())
                .channel(notification.getChannel())
                .sent(notification.isSent())
                .createdAt(notification.getCreatedAt())
                .sentAt(notification.getSentAt())
                .build();
    }
}