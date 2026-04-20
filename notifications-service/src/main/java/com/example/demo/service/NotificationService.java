package com.example.demo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.dto.NotificationRequestDTO;
import com.example.demo.model.Notification;

public interface NotificationService {
    Notification createNotification(NotificationRequestDTO request);
    Page<Notification> getUserNotifications(Long userId, Pageable pageable);
    Notification markAsRead(Long notificationId);
    void markAllAsRead(Long userId);
    Page<Notification> getAllNotifications(Pageable pageable);
    void deleteNotification(Long notificationId);
}