package com.example.demo.dto.compliance_audit;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuditCreateRequestDTO {

	/** Existing ComplianceRecord ID */
	@NotNull(message = "complianceId is required")
	private Long complianceId;
}