package com.notification.service.repository;

import com.notification.service.model.Notification;
import com.notification.service.model.User;
import com.notification.service.model.enums.Channel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByUser(User user);
    
    List<Notification> findByUserAndSent(User user, boolean sent);
    
    List<Notification> findByChannel(Channel channel);
    
    List<Notification> findByChannelAndSent(Channel channel, boolean sent);
    
    @Query("SELECT n FROM Notification n ORDER BY n.createdAt DESC")
    Page<Notification> findAllOrderByCreatedAtDesc(Pageable pageable);
    
    List<Notification> findBySentAndCreatedAtBefore(boolean sent, LocalDateTime dateTime);
}