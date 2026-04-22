package com.example.demo.dto;

import lombok.Data;

@Data
public class IncentiveAnalyticsDTO {

    private Integer totalIncentives;         // COUNT(*)
    private Integer approvedIncentives;      // status = APPROVED / PARTIALLY_DISBURSED / COMPLETED

    private Double totalSanctionedAmount;    // SUM(amount)
    private Double totalDisbursedAmount;     // SUM(amount - remainingAmount)
}