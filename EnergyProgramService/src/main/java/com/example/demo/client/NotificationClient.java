package com.example.demo.client;
 
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.demo.dto.NotificationRequestDTO;
 
@FeignClient(name = "NOTIFICATIONS-SERVICE")
public interface NotificationClient {
 
    @PostMapping("/api/notifications/trigger")
    void createNotification(@RequestBody NotificationRequestDTO request);
}