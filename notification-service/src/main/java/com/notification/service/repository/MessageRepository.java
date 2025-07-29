package com.notification.service.repository;

import com.notification.service.model.Message;
import com.notification.service.model.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    List<Message> findByCategory(Category category);
    
    List<Message> findByCreatedAtAfter(LocalDateTime dateTime);
    
    List<Message> findByCategoryAndCreatedAtBetween(Category category, LocalDateTime start, LocalDateTime end);
}