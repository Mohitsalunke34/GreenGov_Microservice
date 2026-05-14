package com.example.demo.dto.compliance_audit;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComplianceResponseDTO {

	private Long id;

	private String subjectType; // PROJECT / PROGRAM / INCENTIVE
	private Long subjectId;

	private String result; // PASS / FAIL / NEEDS_REVIEW
	private String auditStatus; // PENDING / VERIFIED / FLAGGED

	private String notes;
	private String evidenceURL;

	private Instant recordedDate;
}