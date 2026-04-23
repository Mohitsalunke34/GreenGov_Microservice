package com.cognizant.greengov.service.notification_service;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cognizant.greengov.dto.notifications.NotificationRequestDTO;
import com.cognizant.greengov.model.notification.Notification;
import com.cognizant.greengov.model.register_login.UserAccount;
import com.cognizant.greengov.repository.notification_repo.NotificationRepository;
import com.cognizant.greengov.repository.register_login_repo.UserAccountRepository;

import jakarta.validation.ValidationException;
 
/**
* Implementation of the Notification Service for the GreenGov platform.
* Manages the creation, retrieval, and status tracking of system alerts, 
* including asynchronous email dispatch.
*/
@Service
public class NotificationServiceImp implements NotificationServiceInterface {
 
	private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImp.class);
 
	@Autowired
	private NotificationRepository notificationRepository;
 
	@Autowired
	private UserAccountRepository userRepository;
 
	@Autowired
	private JavaMailSender mailSender;
 
	/**
	 * Creates a notification record in the database and triggers an email alert.
	 * @param request DTO containing recipient ID, message, and category.
	 * @return The saved Notification entity.
	 * @throws RuntimeException if the user is not found.
	 */
	@Override
	@Transactional
	public Notification createNotification(NotificationRequestDTO request) {
		logger.info("Initiating notification process for User ID: {} | Category: {}", request.getUserId(),
				request.getCategory());
 
		validateNotificationRequest(request);
 
		UserAccount user = userRepository.findById(request.getUserId()).orElseThrow(() -> {
			logger.error("Notification failed: User ID {} not found in system.", request.getUserId());
			return new RuntimeException("User not found with ID: " + request.getUserId());
		});
 
		Notification notification = Notification.builder().user(user).entityId(request.getEntityId())
				.message(request.getMessage()).category(request.getCategory()).status(Notification.Status.SENT).build();
 
		Notification saved = notificationRepository.save(notification);
		logger.debug("Internal notification record saved with ID: {}", saved.getNotificationId());
 
		// Dispatch email independently of the database transaction
		this.sendEmailAsync(request.getEmail(), request.getMessage());
 
		return saved;
	}
 
	/**
	 * Dispatches an email asynchronously to avoid blocking the main thread.
	 * @param email The recipient's email address.
	 * @param messageContent The content of the alert.
	 */
	@Async
	protected void sendEmailAsync(String email, String messageContent) {
		if (email == null || email.isBlank()) {
			logger.warn("Skipping email dispatch: No email address provided in request.");
			return;
		}
 
		try {
			logger.debug("Attempting to dispatch email to: {}", email);
			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setTo(email);
			mail.setSubject("GreenGov: New System Alert");
			mail.setText(messageContent);
			mailSender.send(mail);
			logger.info("Email alert successfully dispatched to: {}", email);
		} catch (Exception e) {
			logger.error("SMTP Error: Failed to send email to {}. Reason: {}", email, e.getMessage());
		}
	}
 
	/**
	 * Retrieves a paginated list of notifications for a specific user.
	 * @param userId The ID of the recipient.
	 * @param pageable Pagination and sorting information.
	 * @return A page of notifications.
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<Notification> getUserNotifications(Long userId, Pageable pageable) {
		logger.debug("Fetching paginated notifications for User ID: {}", userId);
		return notificationRepository.findByUser_Id(userId, pageable);
	}
 
	/**
	 * Updates the status of a specific notification to 'READ'.
	 * @param notificationId The ID of the notification.
	 * @return The updated Notification entity.
	 */
	@Override
	@Transactional
	public Notification markAsRead(Long notificationId) {
		logger.info("Marking Notification ID {} as READ", notificationId);
		return notificationRepository.findById(notificationId).map(n -> {
			n.setStatus(Notification.Status.READ);
			return notificationRepository.save(n);
		}).orElseThrow(() -> {
			logger.error("MarkAsRead failed: Notification ID {} not found.", notificationId);
			return new RuntimeException("Notification not found ID: " + notificationId);
		});
	}
 
	/**
	 * Marks all existing notifications for a user as 'READ' in bulk.
	 * @param userId The ID of the user.
	 */
	@Override
	@Transactional
	public void markAllAsRead(Long userId) {
		logger.info("Marking all notifications for User ID {} as READ", userId);
		notificationRepository.markAllAsReadByUserId(userId);
	}
 
	/**
	 * Retrieves all notifications across the platform for administrative auditing.
	 * @param pageable Pagination and sorting information.
	 * @return A page of all system notifications.
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<Notification> getAllNotifications(Pageable pageable) {
		logger.debug("Fetching global notification history (Admin view)");
		return notificationRepository.findAll(pageable);
	}
 
	/**
	 * Deletes a notification record permanently from the database.
	 * @param notificationId The ID of the notification to delete.
	 */
	@Override
	@Transactional
	public void deleteNotification(Long notificationId) {
		logger.warn("Request to permanently delete Notification ID: {}", notificationId);
		if (!notificationRepository.existsById(notificationId)) {
			logger.error("Deletion failed: Notification ID {} does not exist.", notificationId);
			throw new RuntimeException("Cannot delete: Notification not found ID: " + notificationId);
		}
		notificationRepository.deleteById(notificationId);
		logger.info("Notification ID {} purged from database.", notificationId);
	}
 
	/**
	 * Internal helper to validate incoming notification requests.
	 * @param request The DTO to validate.
	 */
	private void validateNotificationRequest(NotificationRequestDTO request) {
		if (request.getUserId() == null) {
			throw new ValidationException("User ID is required for notification.");
		}
		if (request.getMessage() == null || request.getMessage().isBlank()) {
			throw new ValidationException("Notification message cannot be empty.");
		}
		if (request.getCategory() == null) {
			throw new ValidationException("Notification category must be specified.");
		}
	}
}