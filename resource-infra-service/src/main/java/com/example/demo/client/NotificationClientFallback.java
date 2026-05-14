package com.example.demo.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.example.demo.dto.NotificationRequestDTO;

@Component
public class NotificationClientFallback implements NotificationClient {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationClientFallback.class);

    @Override
    public void createNotification(NotificationRequestDTO request) {
        // This runs if the Notification Service is DOWN
        logger.error("Fallback triggered: Notification Service is unavailable. " +
                     "Queuing alert for User {} locally: {}", request.getUserId(), request.getMessage());
        
        // Logic here could be: Save to a local 'failed_notifications' table or just log it.
    }
}