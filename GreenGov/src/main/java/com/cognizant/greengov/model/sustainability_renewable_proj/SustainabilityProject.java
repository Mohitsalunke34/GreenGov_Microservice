// SustainabilityProject (optionally owned by participant)
package com.cognizant.greengov.model.sustainability_renewable_proj;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.cognizant.greengov.model.register_login.ParticipantProfile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

	@NotBlank
	@Column(nullable = false, length = 200)
	private String title;
	@Column(columnDefinition = "TEXT")
	private String description;
	@NotNull
	private LocalDate startDate;
	private LocalDate endDate;
	@NotNull
	@DecimalMin("0.0")
	private BigDecimal budget;
	@NotBlank
	private String status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_participant_id")
	private ParticipantProfile owner;
}