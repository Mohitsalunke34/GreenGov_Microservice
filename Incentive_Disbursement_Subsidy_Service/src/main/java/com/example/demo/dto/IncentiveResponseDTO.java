package com.example.demo.dto;



import java.time.LocalDate;

import lombok.Data;

@Data
public class IncentiveResponseDTO {

    private Long incentiveId;
    private Long applicationId;
    private Long programId;
    private Long beneficiaryId;

    private Double amount;           // total
    private Double remainingAmount;  // ✅ expose remaining

    private LocalDate sanctionedDate;
    private String status;
    private Long approvedByUserId;
}
