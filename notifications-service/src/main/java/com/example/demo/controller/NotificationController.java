package com.example.demo.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.example.demo.dto.NotificationRequestDTO;
import com.example.demo.model.Notification;
import com.example.demo.service.NotificationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
	private final NotificationService service;

	/**
	 * US: Triggered by other microservices (Resource/Project) to send an alert.
	 * POST /api/notifications/trigger
	 */
	@PostMapping("/trigger")
	public ResponseEntity<Notification> trigger(@RequestBody @Valid NotificationRequestDTO dto) {
		logger.info("REST request to trigger notification for User ID: {}", dto.getUserId());
		Notification response = service.createNotification(dto);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	/**
	 * US: Fetch user notification history (paginated). GET
	 * /api/notifications/user/{userId}
	 */
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<Notification>> getAllByUserId(@PathVariable Long userId,
			@PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

		logger.debug("REST request to get notifications for User: {}", userId);
		return ResponseEntity.ok(service.getUserNotifications(userId, pageable).getContent());
	}

	/**
	 * US: Mark a specific notification as READ. PATCH /api/notifications/{id}/read
	 */
	@PatchMapping("/{id}/read")
	public ResponseEntity<Notification> markRead(@PathVariable Long id) {
		logger.info("REST request to mark Notification ID: {} as READ", id);
		return ResponseEntity.ok(service.markAsRead(id));
	}

	/**
	 * US: Bulk update all notifications for a user. PATCH
	 * /api/notifications/user/{userId}/mark-all-read
	 */
	@PatchMapping("/user/{userId}/mark-all-read")
	public ResponseEntity<Void> markAllRead(@PathVariable Long userId) {
		logger.info("REST request to mark all notifications as READ for User: {}", userId);
		service.markAllAsRead(userId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * US: Remove notification record. DELETE /api/notifications/{id}
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		logger.warn("REST request to DELETE notification ID: {}", id);
		service.deleteNotification(id);
		return ResponseEntity.noContent().build();
	}
}