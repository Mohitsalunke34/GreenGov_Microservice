package com.example.demo.dto;


import java.time.LocalDate;

import lombok.Data;

@Data
public class DisbursementResponseDTO {

    private Long disbursementId;
    private Long incentiveId;
    private Long officerUserId;
    private Double amount;
    private LocalDate paymentDate;
    private String status;
}