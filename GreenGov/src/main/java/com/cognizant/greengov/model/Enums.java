package com.cognizant.greengov.model;

public class Enums {
	public enum ComplianceSubjectType {
		PROJECT, PROGRAM, INCENTIVE
	}

	public enum PrimaryRole {
		CITIZEN, BUSINESS_OWNER, OFFICER
	}

	public enum ComplianceAuditStatus {
		PENDING, VERIFIED, FLAGGED
	}

	public enum ProfileStatus {
		PENDING, APPROVED, REJECTED
	}

	public enum ComplianceResult {
		PASS, FAIL, NEEDS_REVIEW
	}

	public enum AuditStatus {
		PLANNED, IN_PROGRESS, COMPLETED , CANCELLED
	}

	public enum ReportScope {
		PROJECT, PROGRAM, INCENTIVE, COMPLIANCE
	}

	public enum ReportStatus {
		GENERATED, FAILED, ARCHIVED,PENDING
	}

	public enum VerificationStatus {
		PENDING, VERIFIED, REJECTED
	}

	public enum EntityType {
		CITIZEN, BUSINESS
	}

	public enum DocumentType {
		ID_PROOF, ADDRESS_PROOF, BUSINESS_REG_CERT, OFFICER_APPOINTMENT, GOVT_ID_CARD
	}

	public enum OfficerType {
		DISBURSEMENT_OFFICER, COMPLIANCE_OFFICER, AUDIT_MANAGER, PROGRAM_MANAGER,ENVIRONMENT_OFFICER
	}
	
}