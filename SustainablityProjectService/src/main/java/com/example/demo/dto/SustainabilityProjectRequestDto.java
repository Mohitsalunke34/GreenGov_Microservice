package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SustainabilityProjectRequestDto {

	@NotBlank(message = "Title is mandatory")
	@Size(max = 200, message = "Title cannot exceed 200 characters")
	private String title;

	private String description;

	@NotNull(message = "Start date is mandatory")
	private LocalDate startDate;

	private LocalDate endDate;

	@NotNull(message = "Budget is mandatory")
	@DecimalMin(value = "0.0", inclusive = false, message = "Budget must be a positive value")
	private BigDecimal budget;
	
	private Long participantId;
}