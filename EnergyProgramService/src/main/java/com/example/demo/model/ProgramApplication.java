package com.example.demo.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "program_application", uniqueConstraints = @UniqueConstraint(name = "uk_applicant_program", columnNames = {
		"applicant_id", "program_id" }))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramApplication {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long applicationId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "program_id", nullable = false)
	private EnergyProgram program;

	@Column(name = "applicant_id", nullable = false)
	private Long applicantId;

	@Column(name = "incentive_id")
	private Long incentiveId;

	@Column(nullable = false)
	private LocalDate submittedDate;

	@Column(nullable = false, length = 30)
	private String status;
}