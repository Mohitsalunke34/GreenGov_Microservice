package com.example.demo.model;

public class Enums {
	public enum ComplianceResult {
		PASS, FAIL, NEEDS_REVIEW
	}

	public enum ComplianceSubjectType {
		PROJECT, PROGRAM, INCENTIVE
	}

	public enum ComplianceAuditStatus {
		PENDING, VERIFIED, FLAGGED
	}

	public enum AuditStatus {
		PLANNED, IN_PROGRESS, COMPLETED, CANCELLED
	}

	public enum ReportScope {
		PROJECT, PROGRAM, INCENTIVE, COMPLIANCE
	}
}
