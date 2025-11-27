package com.studydocs.service;

import com.studydocs.model.entity.Notification;
import com.studydocs.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    
    public Notification createNotification(Notification notification) {
        notification.setIsRead(false);
        return notificationRepository.save(notification);
    }
    
    public Page<Notification> getNotificationsByUser(Integer userId, Pageable pageable) {
        return notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId, pageable);
    }
    
    public void markAsRead(Integer notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        });
    }
    
    public void markAllAsRead(Integer userId) {
        Page<Notification> notifications = notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(
            userId, Pageable.unpaged());
        notifications.forEach(notification -> {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        });
    }
    
    public long countUnreadNotifications(Integer userId) {
        return notificationRepository.countByUser_UserIdAndIsRead(userId, false);
    }
    
    public Optional<Notification> findById(Integer id) {
        return notificationRepository.findById(id);
    }
}

