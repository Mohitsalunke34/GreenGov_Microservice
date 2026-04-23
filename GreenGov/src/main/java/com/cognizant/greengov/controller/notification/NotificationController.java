package com.cognizant.greengov.controller.notification;
 
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.greengov.dto.notifications.NotificationRequestDTO;
import com.cognizant.greengov.model.notification.Notification;
import com.cognizant.greengov.service.notification_service.NotificationServiceInterface;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
 
@RestController

@RequestMapping("/api/notifications")

@RequiredArgsConstructor // Automatically injects NotificationService via constructor

public class NotificationController {
 
    private final NotificationServiceInterface service;
 
    /**

     * Triggers a new notification and background email alert.

     */

    @PostMapping("/trigger")

    public ResponseEntity<Notification> trigger(@RequestBody @Valid NotificationRequestDTO dto) {

        // Method name updated to match ServiceImp: createNotification

        return ResponseEntity.status(HttpStatus.CREATED).body(service.createNotification(dto));

    }
 
    /**

     * Fetches all notifications for a specific user with Pagination.

     * Default: Page 0, Size 10, Sorted by newest first.

     */

    @GetMapping("/user/{userId}")

    public ResponseEntity<List<Notification>> getAll(

            @PathVariable Long userId,

            @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        // Extracting content from the Page object for the List return type

        return ResponseEntity.ok(service.getUserNotifications(userId, pageable).getContent());

    }
 
    /**

     * Marks a single notification as read.

     */

    @PatchMapping("/{id}/read")

    public ResponseEntity<Notification> markRead(@PathVariable Long id) {

        return ResponseEntity.ok(service.markAsRead(id));

    }
 
    /**

     * Marks all notifications for a specific user as read (Bulk Update).

     */

    @PatchMapping("/user/{userId}/mark-all-read")

    public ResponseEntity<Void> markAllRead(@PathVariable Long userId) {

        service.markAllAsRead(userId);

        return ResponseEntity.noContent().build();

    }
 
    /**

     * Deletes a notification by ID.

     */

    @DeleteMapping("/{id}")

    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.deleteNotification(id);

        return ResponseEntity.noContent().build();

    }

}
 