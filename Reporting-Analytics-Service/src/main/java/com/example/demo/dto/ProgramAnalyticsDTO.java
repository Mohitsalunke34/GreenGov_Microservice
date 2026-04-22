package com.example.demo.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProgramAnalyticsDTO {

    private Integer totalPrograms;          // COUNT(*)
    private Integer activePrograms;          // status = ACTIVE

    private BigDecimal totalBudget;          // SUM(budget)
    private BigDecimal remainingBudget;      // SUM(remainingProgramBudget)
}