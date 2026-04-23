package com.cognizant.greengov.controller.reports;

import java.time.Instant;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.greengov.dto.complianceauditdto.ReportResponseDTO;
import com.cognizant.greengov.model.Enums.ReportScope;
import com.cognizant.greengov.model.Enums.ReportStatus;
import com.cognizant.greengov.service.reports_service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public List<ReportResponseDTO> filterReports(
            @RequestParam(required = false) ReportScope scope,
            @RequestParam(required = false) ReportStatus status,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant endDate,

            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long programId,
            @RequestParam(required = false) Long incentiveId
    ) {
        return reportService.filterReports(
                scope, status, startDate, endDate,
                projectId, programId, incentiveId
        );
    }

    @GetMapping("/{id}")
    public ReportResponseDTO getReportById(@PathVariable Long id) {
        return reportService.getReportById(id);
    }
}