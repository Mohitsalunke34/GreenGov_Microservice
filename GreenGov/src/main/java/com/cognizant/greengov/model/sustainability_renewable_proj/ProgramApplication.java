//What the citizen/business APPLIES for
//Examples:
//“ABC Solar Pvt Ltd applied to Solar Subsidy Program”
//One application → one applicant
//Exists only because a program exists

package com.cognizant.greengov.model.sustainability_renewable_proj;

import java.time.LocalDate;

import com.cognizant.greengov.model.incentive_subsidy.Incentive;
import com.cognizant.greengov.model.register_login.ParticipantProfile;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
	
	@OneToOne(mappedBy = "application", fetch = FetchType.LAZY)
	private Incentive incentive;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "applicant_id", nullable = false)
	private ParticipantProfile applicant;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "program_id", nullable = false)
	private EnergyProgram program;

	@NotNull
	private LocalDate submittedDate;
	@NotBlank
	private String status; // PENDING, VALIDATED, REJECTED
}