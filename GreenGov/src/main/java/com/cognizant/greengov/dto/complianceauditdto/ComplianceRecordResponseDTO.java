package com.cognizant.greengov.dto.complianceauditdto;

import java.time.Instant;

import lombok.Data;

@Data
public class ComplianceRecordResponseDTO {

    private Long complianceId;
    private String subjectType;
    private Long subjectId;
    private Long participantId;

    private Long projectId;
    private Long programId;
    private Long incentiveId;


    private String result;
    private Instant recordedDate;
    private String notes;

    private Long complianceManagerUserId;
}