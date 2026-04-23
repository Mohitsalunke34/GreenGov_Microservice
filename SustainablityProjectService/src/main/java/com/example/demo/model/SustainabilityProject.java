package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sustainability_projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SustainabilityProject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long projectId;
	
	@Column(name = "participant_id", nullable = false)
    private Long participantId;

	@Column(nullable = false, length = 200)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(nullable = false)
	private LocalDate startDate;

	@Column
	private LocalDate endDate;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal budget;

	@Column(nullable = false, length = 30)
	private String status;
}