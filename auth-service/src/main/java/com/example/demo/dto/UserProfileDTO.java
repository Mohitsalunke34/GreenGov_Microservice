package com.example.demo.dto;

import java.time.LocalDateTime;

import com.example.demo.model.Enums.PrimaryRole;

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
	private PrimaryRole primaryRole;
	private boolean active;
	private LocalDateTime createdAt;
	private LocalDateTime lastLoginAt;
}