package com.cognizant.greengov.dto.notifications;
 
import com.cognizant.greengov.model.notification.Notification;

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
 
    @NotBlank(message = "Email is required")

    @Email(message = "Invalid email format")

    private String email; // This was missing!
 
    private Long entityId;
 
    @NotBlank(message = "Message content is required")

    private String message;
 
    @NotNull(message = "Category is required")

    private Notification.Category category; // Use the Enum type here to avoid the "not applicable" error

}
 