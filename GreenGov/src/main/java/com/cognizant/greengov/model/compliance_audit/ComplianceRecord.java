// ComplianceRecord
package com.cognizant.greengov.model.compliance_audit;

import java.time.Instant;

import com.cognizant.greengov.model.Enums.ComplianceAuditStatus;
import com.cognizant.greengov.model.Enums.ComplianceResult;
import com.cognizant.greengov.model.Enums.ComplianceSubjectType;
import com.cognizant.greengov.model.incentive_subsidy.Incentive;
import com.cognizant.greengov.model.register_login.ParticipantProfile;
import com.cognizant.greengov.model.register_login.UserAccount;
import com.cognizant.greengov.model.sustainability_renewable_proj.EnergyProgram;
import com.cognizant.greengov.model.sustainability_renewable_proj.SustainabilityProject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "compliance_record", indexes = {
		@Index(name = "idx_compl_subject", columnList = "subject_type,subject_id"),
		@Index(name = "idx_compl_recorded_date", columnList = "recorded_date") })
public class ComplianceRecord extends CreatedUpdatedLogs {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "compliance_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "subject_type", nullable = false, length = 30)
	private ComplianceSubjectType subjectType;

	@NotNull
	@Column(name = "subject_id", nullable = false)
	private Long subjectId; // maintains generic compatibility

	// Concrete links (only one of these should be non-null depending on
	// subjectType)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
	private SustainabilityProject project;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "program_id")
	private EnergyProgram program;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "incentive_id")
	private Incentive incentive;

	// Assuming each participant has applied to a program / project or Incentive for
	// testing
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "participant_id", nullable = false)
	private ParticipantProfile participant;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "result", nullable = false, length = 30)
	private ComplianceResult result;

	@NotNull
	@Column(name = "recorded_date", nullable = false)
	private Instant recordedDate;

	@Lob
	@Column(name = "notes")
	private String notes;
	@Size(max = 300)
	@Column(name = "evidence_url", length = 300)
	private String evidenceURL;

	// Who recorded/approved this compliance
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "compliance_manager_user_id", nullable = false)
	private UserAccount complianceManager;

	// For auditor to verify the compliance workings
	@Enumerated(EnumType.STRING)
	@Column(name = "audit_status", nullable = false)
	private ComplianceAuditStatus auditStatus = ComplianceAuditStatus.PENDING;

}