package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnergyProgramResponseDto {

	private Long programId;
	private String title;
	private String description;
	private LocalDate startDate;
	private LocalDate endDate;

	private BigDecimal budget;
	private BigDecimal remainingProgramBudget; // ✅ FIXED NAME

	private String status;
}