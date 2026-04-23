package com.cognizant.greengov.dto.sustainability_dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ProgramApplicationResponseDTO {

    private Long applicationId;
    private Long programId;
    private Long applicantId;
    private LocalDate submittedDate;
    private String status;
}