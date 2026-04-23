package com.cognizant.greengov.dto.sustainability_dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class EnergyProgramDTO {

	private Long programId;

	// Program details
	private String title;
	private String description;

	// Timeline
	private LocalDate startDate;
	private LocalDate endDate;

	// Financials
	private BigDecimal budget;

	// Lifecycle
	private String status;

	// Ownership / audit
	private Long ownerUserId;
}