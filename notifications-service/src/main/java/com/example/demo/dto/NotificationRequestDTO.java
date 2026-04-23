package com.example.demo.dto;

import com.example.demo.model.Notification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequestDTO {

	@NotNull(message = "User ID is required")
	private Long userId;

	
	@Email(message = "Invalid email format")
	private String email;

	private Long entityId;

	@NotBlank(message = "Message content is required")
	private String message;

	@NotNull(message = "Category is required")
	private Notification.Category category;
	
	private boolean sendEmail;
}