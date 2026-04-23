package com.example.demo.dto;
 
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
/**
* Data Transfer Object for sending notification requests from other
* microservices to the Notification-Service.
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequestDTO {
 
	@NotNull(message = "User ID is required for notification.")
	private Long userId;
 
	@NotBlank(message = "Message cannot be empty.")
	private String message;
 
	@NotBlank(message = "Category must be specified (e.g., RESOURCE_ALLOCATED).")
	private String category;
 
	@Email(message = "Please provide a valid email format.")
	@NotBlank(message = "Email is required for dispatch.")
	private String email;
 
	private Long entityId;
	private boolean sendEmail;
}