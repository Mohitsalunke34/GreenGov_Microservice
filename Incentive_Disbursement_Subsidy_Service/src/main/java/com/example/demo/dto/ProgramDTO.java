package com.example.demo.dto;

import java.math.BigDecimal;

import lombok.Data;


@Data
public class ProgramDTO {

    private Long programId;
    private String title;

    private String status; // ACTIVE / INACTIVE / CLOSED

    private BigDecimal budget;
    private BigDecimal remainingProgramBudget;
}
