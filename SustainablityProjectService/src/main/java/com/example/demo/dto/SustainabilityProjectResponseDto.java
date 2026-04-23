package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SustainabilityProjectResponseDto {

	private Long projectId;

	private String title;

	private String description;

	private LocalDate startDate;

	private LocalDate endDate;

	private BigDecimal budget;

	private String status;
	
	private Long participantId;
	
	
}