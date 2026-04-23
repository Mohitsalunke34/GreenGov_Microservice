package com.cognizant.greengov.model.incentive_subsidy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.cognizant.greengov.model.compliance_audit.CreatedUpdatedLogs;
import com.cognizant.greengov.model.register_login.ParticipantProfile;
import com.cognizant.greengov.model.register_login.UserAccount;
import com.cognizant.greengov.model.sustainability_renewable_proj.EnergyProgram;
import com.cognizant.greengov.model.sustainability_renewable_proj.ProgramApplication;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "incentives", indexes = {
		@Index(name = "idx_incentive_program_beneficiary", columnList = "program_id,beneficiary_id") }, uniqueConstraints = {
				// One application → one incentive
				@UniqueConstraint(name = "uk_incentive_application", columnNames = { "application_id" }) })
@Getter
@Setter
@NoArgsConstructor
public class Incentive extends CreatedUpdatedLogs {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long incentiveId;

	/** One application leads to at most one incentive (owning side holds FK) */
	@NotNull
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_id", nullable = false, unique = true)
	private ProgramApplication application;

	/** Many incentives can be under the same program */
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "program_id", nullable = false)
	private EnergyProgram program;

	/** The citizen/business receiving this incentive */
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "beneficiary_id", nullable = false)
	private ParticipantProfile beneficiary;

	/** Officer (user) who approved/sanctioned the incentive (optional) */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "approved_by_user_id")
	private UserAccount approvedBy;

	@NotNull
	@Column(nullable = false)
	private Double amount;

	@NotNull
	@Column(name = "sanctioned_date", nullable = false)
	private LocalDate sanctionedDate;

	/** APPROVED, SCHEDULED, PARTIALLY_DISBURSED, COMPLETE, etc. */
	@NotNull
	@Column(nullable = false, length = 30)
	private String status;

	@Column(nullable = false)
	private Double remainingAmount;

	/** Inverse side; Disbursement owns the FK via disbursements.incentive_id */
	@OneToMany(mappedBy = "incentive", fetch = FetchType.LAZY)
	@OrderBy("paymentDate ASC")
	private List<Disbursement> disbursements = new ArrayList<>();
}