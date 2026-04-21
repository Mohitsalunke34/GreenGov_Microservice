package com.example.demo.model;

import java.time.Instant;

import com.example.demo.model.Enums.AuditStatus;
import com.example.demo.model.Enums.ReportScope;

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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "audit", indexes = { @Index(name = "idx_audit_scope", columnList = "scope"),
		@Index(name = "idx_audit_status", columnList = "status"),
		@Index(name = "idx_audit_compliance", columnList = "compliance_id") })
public class Audit extends CreatedUpdatedLogs {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "audit_id")
	private Long id;

	// Audit officer (UserAccount ID)
	@NotNull
	@Column(name = "officer_user_id", nullable = false)
	private Long officerUserId;

	// Compliance record being audited (internal reference)
	@NotNull
	@Column(name = "compliance_id", nullable = false)
	private Long complianceId;

	@Lob
	@Column(name = "findings")
	private String findings;

	@NotNull
	@Column(name = "opened_date", nullable = false)
	private Instant openedDate;

	@Column(name = "closed_date")
	private Instant closedDate;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 30)
	private AuditStatus status = AuditStatus.PLANNED;

	@Min(1)
	@Max(5)
	@Column(name = "severity")
	private Integer severity;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "scope", nullable = false, length = 30)
	private ReportScope scope;
}
