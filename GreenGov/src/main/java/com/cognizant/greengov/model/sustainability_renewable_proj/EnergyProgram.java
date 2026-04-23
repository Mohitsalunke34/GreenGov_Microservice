//What the government OFFERS
//Examples:
//Solar Subsidy Program 2025
//One program → many applicants
//Exists even if nobody applies
package com.cognizant.greengov.model.sustainability_renewable_proj;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.cognizant.greengov.model.compliance_audit.CreatedUpdatedLogs;
import com.cognizant.greengov.model.incentive_subsidy.Incentive;
import com.cognizant.greengov.model.register_login.UserAccount;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "energy_program")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnergyProgram extends CreatedUpdatedLogs {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long programId;

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
	
	
	@Column(name = "remaining_program_budget", precision = 19, scale = 2)
	private BigDecimal remainingProgramBudget;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_user_id")
	private UserAccount owner; // Many Energy program can be made by Single owner.
	@OneToMany(mappedBy = "program", fetch = FetchType.LAZY)
	private List<Incentive> incentives = new ArrayList<>();
}