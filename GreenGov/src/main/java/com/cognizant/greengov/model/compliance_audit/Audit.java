// Audit
package com.cognizant.greengov.model.compliance_audit;

import java.time.Instant;

import com.cognizant.greengov.model.Enums.AuditStatus;
import com.cognizant.greengov.model.Enums.ReportScope;
import com.cognizant.greengov.model.register_login.UserAccount;

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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "audit", indexes = { @Index(name = "idx_audit_scope", columnList = "scope"),
		@Index(name = "idx_audit_status", columnList = "status") })
public class Audit extends CreatedUpdatedLogs {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "audit_id")
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "officer_user_id", nullable = false)
	private UserAccount officer; // (Audit manager)

	// What is being audited — the COMPLIANCE work
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "compliance_id", nullable = false)
	private ComplianceRecord complianceRecord;

	@Lob
	@Column(name = "findings")
	private String findings;

	@NotNull
	@Column(name = "opened_date", nullable = false)
	private Instant openedDate;
	@Column(name = "closed_date")
	private Instant closedDate;

	// Audit Lifecycle
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