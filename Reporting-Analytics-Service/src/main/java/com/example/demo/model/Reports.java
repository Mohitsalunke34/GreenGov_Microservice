package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reports {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Enumerated(EnumType.STRING)
    private ReportScope scope;

    /* ================= PROGRAM ANALYTICS ================= */
    private Integer totalPrograms;
    private Integer activePrograms;
    private Double totalProgramBudget;
    private Double remainingProgramBudget;

    /* ================= INCENTIVE ANALYTICS ================= */
    private Integer totalIncentives;
    private Integer approvedIncentives;
    private Double totalIncentiveAmount;
    private Double totalDisbursedAmount;

    /* ================= PROJECT ANALYTICS ================= */
    private Integer totalProjects;
    private Integer activeProjects;
    private Integer completedProject;

    /* ================= COMPLIANCE ANALYTICS ================= */
    private Integer totalAudits;
    private Integer compliantCount;
    private Integer nonCompliantCount;

    private LocalDateTime generatedDate;
}
