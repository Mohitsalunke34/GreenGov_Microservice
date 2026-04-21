package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "energy_program")
public class EnergyProgram {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long programId;

	@NotBlank
	@Column(nullable = false, length = 200)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	@NotNull
	@Column(nullable = false)
	private LocalDate startDate;

	@Column
	private LocalDate endDate;

	@NotNull
	@DecimalMin("0.0")
	@Column(nullable = false)
	private BigDecimal budget;

	@NotBlank
	@Column(nullable = false)
	private String status;

	// ✅ Microservice rule: no User/Incentive entities here

	@Column(precision = 19, scale = 2)
	private BigDecimal remainingProgramBudget;

	// getters & setters
}
