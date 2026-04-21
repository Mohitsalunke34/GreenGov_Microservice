package com.example.demo.dto;



import lombok.Data;

@Data
public class ApplicationDTO {

    private Long applicationId;
    private Long programId;
    private Long applicantId;
    private String status; // APPROVED / PENDING / REJECTED
}
