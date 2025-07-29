package com.notification.service.repository;

import com.notification.service.model.User;
import com.notification.service.model.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    @Query("SELECT u FROM User u JOIN u.subscriptions s WHERE s = :category")
    List<User> findBySubscription(@Param("category") Category category);
    
    List<User> findByEmail(String email);
}