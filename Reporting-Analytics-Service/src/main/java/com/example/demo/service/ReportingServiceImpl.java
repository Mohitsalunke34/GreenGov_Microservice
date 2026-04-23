package com.example.demo.service;


import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.demo.client.ComplianceClient;
import com.example.demo.client.IncentiveClient;
import com.example.demo.client.ProgramClient;
import com.example.demo.client.ProjectClient;
import com.example.demo.dto.ReportAnalyticsDTO;
import com.example.demo.model.ReportScope;
import com.example.demo.model.Reports;
import com.example.demo.repo.ReportRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportingServiceImpl implements ReportingService {

    private final ReportRepository reportRepository;

    private final ProgramClient programClient;
    private final IncentiveClient incentiveClient;
    private final ProjectClient projectClient;
    private final ComplianceClient complianceClient;

    @Override
    public Reports generateReport(ReportScope scope) {

        log.info("Generating report for scope {}", scope);

        Reports report = new Reports();
        report.setScope(scope);
        report.setGeneratedDate(LocalDateTime.now());

        if (scope == ReportScope.PROGRAM) {

            Map<String, Object> data =
                    programClient.getProgramReportMetrics();

            report.setTotalPrograms(
                ((Number) data.get("totalPrograms")).intValue());

            report.setActivePrograms(
                ((Number) data.get("activePrograms")).intValue());

            report.setTotalProgramBudget(
                ((Number) data.get("totalBudget")).doubleValue());

            report.setRemainingProgramBudget(
                ((Number) data.get("remainingBudget")).doubleValue());
        }

        if (scope == ReportScope.INCENTIVE) {

            Map<String, Object> data =
                    incentiveClient.getIncentiveReportMetrics();

            report.setTotalIncentives(
                ((Number) data.get("totalIncentives")).intValue());

            report.setApprovedIncentives(
                ((Number) data.get("approvedIncentives")).intValue());

            report.setTotalIncentiveAmount(
                ((Number) data.get("totalAmount")).doubleValue());

            report.setTotalDisbursedAmount(
                ((Number) data.get("disbursedAmount")).doubleValue());
        }


        if (scope == ReportScope.PROJECT) {

            Map<String, Object> data =
                    projectClient.getProjectReportMetrics();

            report.setTotalProjects(
                ((Number) data.get("totalProjects")).intValue());

            report.setActiveProjects(
                ((Number) data.get("activeProjects")).intValue());

            report.setCompletedProject(
                ((Number) data.get("completedProjects")).intValue());
        }


        if (scope == ReportScope.COMPLIANCE) {

            Map<String, Object> data =
                    complianceClient.getComplianceReportMetrics();

            report.setTotalAudits(
                ((Number) data.get("totalAudits")).intValue());

            report.setCompliantCount(
                ((Number) data.get("compliant")).intValue());

            report.setNonCompliantCount(
                ((Number) data.get("nonCompliant")).intValue());
        }


        Reports saved = reportRepository.save(report);
        log.info("Report generated successfully with ID {}", saved.getReportId());

        return saved;
    }

    @Override
    public List<Reports> getReportsByScope(ReportScope scope) {
        return reportRepository.findByScope(scope);
    }

    @Override
    public Reports getReportById(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() ->
                        new RuntimeException("Report not found with ID " + reportId));
    }

    @Override
    public Map<ReportScope, Reports> getSummaryReports() {

        Map<ReportScope, Reports> summary =
                new EnumMap<>(ReportScope.class);

        summary.put(ReportScope.PROGRAM,
        		generateReport(ReportScope.PROGRAM));

        summary.put(ReportScope.INCENTIVE,
                generateReport(ReportScope.INCENTIVE));

        summary.put(ReportScope.PROJECT,
                generateReport(ReportScope.PROJECT));

        summary.put(ReportScope.COMPLIANCE,
                generateReport(ReportScope.COMPLIANCE));

        return summary;
    }

    @Override
    public ReportAnalyticsDTO getAnalytics() {

        Reports program =
                getLatestReport(ReportScope.PROGRAM);
        Reports incentive =
                getLatestReport(ReportScope.INCENTIVE);
        Reports project =
                getLatestReport(ReportScope.PROJECT);
        Reports compliance =
                getLatestReport(ReportScope.COMPLIANCE);

        // ================= PROGRAM =================
        double utilizationPercent =
                program != null && program.getTotalPrograms() > 0
                        ? (program.getActivePrograms() * 100.0 / program.getTotalPrograms())
                        : 0.0;

        // ================= INCENTIVE =================
        double approvalRate =
                incentive != null && incentive.getTotalIncentives() > 0
                        ? (incentive.getApprovedIncentives() * 100.0 / incentive.getTotalIncentives())
                        : 0.0;

        double avgDisbursement =
                incentive != null && incentive.getApprovedIncentives() > 0
                        ? (incentive.getTotalDisbursedAmount() / incentive.getApprovedIncentives())
                        : 0.0;

        // ================= PROJECT =================
        double projectCompletionRate =
                project != null && project.getTotalProjects() > 0
                        ? (project.getCompletedProject() * 100.0 / project.getTotalProjects())
                        : 0.0;

        // ================= COMPLIANCE =================
        double complianceRate =
                compliance != null && compliance.getTotalAudits() > 0
                        ? (compliance.getCompliantCount() * 100.0 / compliance.getTotalAudits())
                        : 0.0;

        return new ReportAnalyticsDTO(

                // PROGRAM
                program != null ? program.getTotalPrograms() : 0,
                program != null ? program.getActivePrograms() : 0,
                program != null ? program.getTotalProgramBudget() : 0.0,
                utilizationPercent,

                // INCENTIVE
                incentive != null ? incentive.getTotalIncentives() : 0,
                incentive != null ? incentive.getApprovedIncentives() : 0,
                approvalRate,
                avgDisbursement,

                // PROJECT
                project != null ? project.getTotalProjects() : 0,
                project != null ? project.getActiveProjects() : 0,
                project != null ? project.getCompletedProject() : 0,
                projectCompletionRate,

                // COMPLIANCE
                compliance != null ? compliance.getTotalAudits() : 0,
                compliance != null ? compliance.getCompliantCount() : 0,
                compliance != null ? compliance.getNonCompliantCount() : 0,
                complianceRate
        );
    }

    private Reports getLatestReport(ReportScope scope) {

        List<Reports> reports =
                reportRepository.findByScope(scope);

        return reports.isEmpty()
                ? null
                : reports.get(reports.size() - 1);
    }
}
