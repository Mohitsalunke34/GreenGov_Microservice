package com.example.demo.model;

import java.time.Instant;

import com.example.demo.model.Enums.ComplianceAuditStatus;
import com.example.demo.model.Enums.ComplianceResult;
import com.example.demo.model.Enums.ComplianceSubjectType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "compliance_record", indexes = {
		@Index(name = "idx_compliance_subject", columnList = "subject_type,subject_id"),
		@Index(name = "idx_compliance_participant", columnList = "participant_id"),
		@Index(name = "idx_compliance_recorded_date", columnList = "recorded_date") })
public class ComplianceRecord extends CreatedUpdatedLogs {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "compliance_id")
	private Long id;

	// What is being checked (PROJECT / PROGRAM / INCENTIVE)
	@Enumerated(EnumType.STRING)
	@Column(name = "subject_type", nullable = false, length = 30)
	private ComplianceSubjectType subjectType;

	// ID from external microservice
	@NotNull
	@Column(name = "subject_id", nullable = false)
	private Long subjectId;


	// Compliance result
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

	// Compliance officer / manager (UserAccount ID)
	@NotNull
	@Column(name = "compliance_manager_user_id", nullable = false)
	private Long complianceManagerUserId;

	// Audit lifecycle state
	@Enumerated(EnumType.STRING)
	@Column(name = "audit_status", nullable = false, length = 30)
	private ComplianceAuditStatus auditStatus = ComplianceAuditStatus.PENDING;
}