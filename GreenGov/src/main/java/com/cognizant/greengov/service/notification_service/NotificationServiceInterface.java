package com.cognizant.greengov.service.notification_service;
 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.cognizant.greengov.dto.notifications.NotificationRequestDTO;
import com.cognizant.greengov.model.notification.Notification;
 
public interface NotificationServiceInterface {
    Notification createNotification(NotificationRequestDTO request);
    Page<Notification> getUserNotifications(Long userId, Pageable pageable);
    Notification markAsRead(Long notificationId);
    void markAllAsRead(Long userId);
    Page<Notification> getAllNotifications(Pageable pageable);
    void deleteNotification(Long notificationId);
}