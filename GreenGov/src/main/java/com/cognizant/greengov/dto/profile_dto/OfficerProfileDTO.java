package com.cognizant.greengov.dto.profile_dto;

import java.time.LocalDateTime;

import com.cognizant.greengov.model.Enums.OfficerType;
import com.cognizant.greengov.model.Enums.ProfileStatus;

import lombok.Data;

@Data
public class OfficerProfileDTO {

	private Long id;

	// Identity link
	private Long userId;

	// Officer details
	private OfficerType officerType;
	private String department;
	private String designation;
	private String officeCode;

	// Approval lifecycle
	private ProfileStatus status;
	private LocalDateTime submittedAt;
	private LocalDateTime approvedAt;
}