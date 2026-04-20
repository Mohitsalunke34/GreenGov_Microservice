package com.cognizant.greengov.profile.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {

	private Long id;
	private String username;
	private String email;
	private String primaryRole;
	private boolean active;
	private LocalDateTime createdAt;
	private LocalDateTime lastLoginAt;
}