package com.notification.service.controller;

import com.notification.service.dto.MessageRequest;
import com.notification.service.dto.MessageResponse;
import com.notification.service.model.Message;
import com.notification.service.repository.NotificationRepository;
import com.notification.service.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final NotificationRepository notificationRepository;

    /**
     * Creates a new message and sends notifications to eligible users
     *
     * @param request The message request containing category and content
     * @return The created message with notification count
     */
    @PostMapping
    public ResponseEntity<MessageResponse> createMessage(@Valid @RequestBody MessageRequest request) {
        Message message = messageService.createMessage(request.getCategory(), request.getContent());
        
        // Count the number of notifications sent for this message
        long notificationsSent = notificationRepository.findAll().stream()
                .filter(n -> n.getMessage().getId().equals(message.getId()) && n.isSent())
                .count();
        
        MessageResponse response = MessageResponse.fromMessage(message, (int) notificationsSent);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves all messages
     *
     * @return A list of all messages
     */
    @GetMapping
    public ResponseEntity<List<MessageResponse>> getAllMessages() {
        List<Message> messages = messageService.getAllMessages();
        
        List<MessageResponse> responses = messages.stream()
                .map(message -> {
                    // Count the number of notifications sent for this message
                    long notificationsSent = notificationRepository.findAll().stream()
                            .filter(n -> n.getMessage().getId().equals(message.getId()) && n.isSent())
                            .count();
                    
                    return MessageResponse.fromMessage(message, (int) notificationsSent);
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    /**
     * Retrieves a message by its ID
     *
     * @param id The ID of the message
     * @return The message, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<MessageResponse> getMessageById(@PathVariable Long id) {
        Message message = messageService.getMessageById(id);
        
        if (message == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Count the number of notifications sent for this message
        long notificationsSent = notificationRepository.findAll().stream()
                .filter(n -> n.getMessage().getId().equals(message.getId()) && n.isSent())
                .count();
        
        MessageResponse response = MessageResponse.fromMessage(message, (int) notificationsSent);
        return ResponseEntity.ok(response);
    }
}