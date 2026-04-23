package com.cognizant.greengov.dto.complianceauditdto;

import java.time.Instant;

import lombok.Data;

@Data
public class ComplianceRecordCreateRequestDTO {

	private String subjectType;
	private Long subjectId;
	private Long participantId;
	private String evidenceURL;

	private Long projectId;
	private Long programId;
	private Long incentiveId;

	private String result;
	private String notes;
	private Instant recordedDate;
}