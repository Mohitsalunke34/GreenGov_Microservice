package com.example.demo.dto.compliance_audit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComplianceRecordCreateRequestDTO {

	/** PROJECT / PROGRAM / INCENTIVE */
	@NotBlank(message = "subjectType is required")
	private String subjectType;

	/** ID of Project / Program / Incentive (from other microservice) */
	@NotNull(message = "subjectId is required")
	private Long subjectId;


	/** PASS / FAIL / NEEDS_REVIEW */
	@NotBlank(message = "result is required")
	private String result;

	/** Optional notes entered by compliance officer */
	private String notes;

	/** Optional evidence link */
	private String evidenceURL;
}
