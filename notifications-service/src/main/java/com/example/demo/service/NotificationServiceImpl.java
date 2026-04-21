package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.NotificationRequestDTO;
import com.example.demo.exception.NotificationNotFoundException; // Updated Import
import com.example.demo.model.Notification;
import com.example.demo.repository.NotificationRepository;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of the Notification Service. Decoupled from User Service for
 * Microservice architecture.
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

	private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

	private final NotificationRepository notificationRepository;
	private final JavaMailSender mailSender;

	@Override
	@Transactional
	public Notification createNotification(NotificationRequestDTO request) {
		logger.info("Processing notification for User ID: {} | Category: {}", request.getUserId(),
				request.getCategory());

		validateNotificationRequest(request);

		Notification notification = Notification.builder().userId(request.getUserId()).entityId(request.getEntityId())
				.message(request.getMessage()).category(request.getCategory()).status(Notification.Status.SENT).build();

		Notification saved = notificationRepository.save(notification);

		// Dispatch email asynchronously
		this.sendEmailAsync(request.getEmail(), request.getMessage());

		return saved;
	}

	@Async
	protected void sendEmailAsync(String email, String messageContent) {
		if (email == null || email.isBlank()) {
			logger.warn("No email address provided. Skipping SMTP dispatch.");
			return;
		}

		try {
			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setTo(email);
			mail.setSubject("GreenGov: System Alert");
			mail.setText(messageContent);
			mailSender.send(mail);
			logger.info("Email successfully sent to: {}", email);
		} catch (Exception e) {
			logger.error("SMTP Error for {}: {}", email, e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Notification> getUserNotifications(Long userId, Pageable pageable) {
		return notificationRepository.findByUserId(userId, pageable);
	}

	@Override
	@Transactional
	public Notification markAsRead(Long notificationId) {
		return notificationRepository.findById(notificationId).map(n -> {
			n.setStatus(Notification.Status.READ);
			return notificationRepository.save(n);
		}).orElseThrow(() -> new NotificationNotFoundException("Notification ID " + notificationId + " not found."));
	}

	@Override
	@Transactional
	public void markAllAsRead(Long userId) {
		logger.info("Marking all alerts as READ for User: {}", userId);
		notificationRepository.markAllAsReadByUserId(userId);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Notification> getAllNotifications(Pageable pageable) {
		return notificationRepository.findAll(pageable);
	}

	@Override
	@Transactional
	public void deleteNotification(Long notificationId) {
		if (!notificationRepository.existsById(notificationId)) {
			// Fixed the empty throw here
			throw new NotificationNotFoundException("Notification ID " + notificationId + " not found.");
		}
		notificationRepository.deleteById(notificationId);
	}

	private void validateNotificationRequest(NotificationRequestDTO request) {
		if (request.getUserId() == null) {
			throw new ValidationException("User ID is required.");
		}
		if (request.getMessage() == null || request.getMessage().isBlank()) {
			throw new ValidationException("Message content cannot be empty.");
		}
		if (request.getCategory() == null) {
			throw new ValidationException("Category is required.");
		}
	}
}