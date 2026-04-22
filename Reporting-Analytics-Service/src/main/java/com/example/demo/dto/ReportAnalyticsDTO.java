package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportAnalyticsDTO {

    // ================= PROGRAM =================
    private Integer totalPrograms;
    private Integer activePrograms;
    private Double totalBudget;
    private Double utilizationPercent;

    // ================= INCENTIVE =================
    private Integer totalIncentives;
    private Integer approvedIncentives;
    private Double approvalRate;
    private Double avgDisbursement;

    // ================= PROJECT =================
    private Integer totalProjects;
    private Integer activeProjects;
    private Integer completedProjects;
    private Double projectCompletionRate;

    // ================= COMPLIANCE =================
    private Integer totalAudits;
    private Integer compliantCount;
    private Integer nonCompliantCount;
    private Double complianceRate;
}