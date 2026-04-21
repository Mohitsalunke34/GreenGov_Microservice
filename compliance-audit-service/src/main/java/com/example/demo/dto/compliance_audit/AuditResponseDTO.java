package com.example.demo.dto.compliance_audit;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuditResponseDTO {

    private Long id;

    private Long complianceId;
    private Long officerUserId;

    private String status;       // PLANNED / IN_PROGRESS / COMPLETED
    private String scope;        // COMPLIANCE / PROGRAM / PROJECT
    private Integer severity;

    private String findings;

    private Instant openedDate;
    private Instant closedDate;
}