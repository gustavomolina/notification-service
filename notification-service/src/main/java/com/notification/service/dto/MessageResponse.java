package com.notification.service.dto;

import com.notification.service.model.Message;
import com.notification.service.model.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    private Long id;
    private Category category;
    private String content;
    private LocalDateTime createdAt;
    private int notificationsSent;

    public static MessageResponse fromMessage(Message message, int notificationsSent) {
        return MessageResponse.builder()
                .id(message.getId())
                .category(message.getCategory())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .notificationsSent(notificationsSent)
                .build();
    }
}