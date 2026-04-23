package com.cognizant.greengov.dto.complianceauditdto;

import java.time.Instant;

import com.cognizant.greengov.model.Enums.AuditStatus;

import lombok.Data;

@Data
public class AuditResponseDTO {

	private Long auditId;

	private Long officerUserId;

	private Long complianceId;

	private Instant openedDate;
	private Instant closedDate;

	private AuditStatus status;

	private Integer severity;
}