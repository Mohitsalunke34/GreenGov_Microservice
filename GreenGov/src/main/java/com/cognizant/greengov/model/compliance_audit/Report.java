// Report
package com.cognizant.greengov.model.compliance_audit;

import java.time.Instant;

import com.cognizant.greengov.model.Enums.ReportScope;
import com.cognizant.greengov.model.Enums.ReportStatus;
import com.cognizant.greengov.model.incentive_subsidy.Incentive;
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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "report", indexes = { @Index(name = "idx_report_scope", columnList = "scope"),
		@Index(name = "idx_report_generated_date", columnList = "generated_date") })
public class Report extends CreatedUpdatedLogs {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "report_id")
	private Long id;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "scope", nullable = false, length = 30)
	private ReportScope scope;

	// Concrete optional links (align to scope)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
	private SustainabilityProject project;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "program_id")
	private EnergyProgram program;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "incentive_id")
	private Incentive incentive;

	@NotNull
	@Column(name = "generated_date", nullable = false)
	private Instant generatedDate;

	@NotNull
	@Size(min = 3, max = 150)
	@Column(name = "title", nullable = false, length = 150)
	private String title;

	@Size(max = 500)
	@Column(name = "description", length = 500)
	private String description;

	@Lob
	@Column(name = "metrics")
	private String metrics;
	@Size(max = 30)
	@Column(name = "format", length = 30)
	private String format;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 30)
	private ReportStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "generated_by_user_id")
	private UserAccount generatedBy;

	@Size(max = 500)
	@Column(name = "file_url", length = 500)
	private String fileUrl;
}