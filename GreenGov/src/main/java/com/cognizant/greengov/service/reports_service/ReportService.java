package com.cognizant.greengov.service.reports_service;

import java.time.Instant;
import java.util.List;

import com.cognizant.greengov.dto.complianceauditdto.ReportResponseDTO;
import com.cognizant.greengov.model.Enums.ReportScope;
import com.cognizant.greengov.model.Enums.ReportStatus;

public interface ReportService {

    List<ReportResponseDTO> filterReports(
            ReportScope scope,
            ReportStatus status,
            Instant startDate,
            Instant endDate,
            Long projectId,
            Long programId,
            Long incentiveId
    );

    ReportResponseDTO getReportById(Long id);
}