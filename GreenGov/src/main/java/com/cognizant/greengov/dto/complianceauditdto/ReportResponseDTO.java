package com.cognizant.greengov.dto.complianceauditdto;

import java.time.Instant;

import lombok.Data;

@Data
public class ReportResponseDTO {

    private Long reportId;
    private String scope;
    private String status;

    private Instant generatedDate;

    private String title;
    private String description;
    private String format;
    private String fileUrl;

    private Long generatedByUserId;
    private String generatedByUsername;

    private Long projectId;
    private String projectName;

    private Long programId;
    private String programName;

    private Long incentiveId;
    private String incentiveName;
}