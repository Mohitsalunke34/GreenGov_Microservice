package com.cognizant.greengov.service.reports_service.Impl;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cognizant.greengov.dto.complianceauditdto.ReportResponseDTO;
import com.cognizant.greengov.model.Enums.ReportScope;
import com.cognizant.greengov.model.Enums.ReportStatus;
import com.cognizant.greengov.model.compliance_audit.Report;
import com.cognizant.greengov.repository.reports_repo.ReportRepository;
import com.cognizant.greengov.service.reports_service.ReportService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    @Override
    public List<ReportResponseDTO> filterReports(
            ReportScope scope,
            ReportStatus status,
            Instant startDate,
            Instant endDate,
            Long projectId,
            Long programId,
            Long incentiveId
    ) {

        List<Report> reports = reportRepository.findAll();

        return reports.stream()
                .filter(r -> scope == null || r.getScope() == scope)
                .filter(r -> status == null || r.getStatus() == status)
                .filter(r -> startDate == null || r.getGeneratedDate().isAfter(startDate))
                .filter(r -> endDate == null || r.getGeneratedDate().isBefore(endDate))
                .filter(r -> projectId == null || 
                             (r.getProject() != null && r.getProject().getProjectId().equals(projectId)))
                .filter(r -> programId == null || 
                             (r.getProgram() != null && r.getProgram().getProgramId().equals(programId)))
                .filter(r -> incentiveId == null || 
                             (r.getIncentive() != null && r.getIncentive().getIncentiveId().equals(incentiveId)))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReportResponseDTO getReportById(Long id) {
        Report r = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with ID " + id));

        return mapToDTO(r);
    }

    private ReportResponseDTO mapToDTO(Report r) {
        ReportResponseDTO dto = new ReportResponseDTO();

        dto.setReportId(r.getId());
        dto.setScope(r.getScope().name());
        dto.setStatus(r.getStatus() != null ? r.getStatus().name() : null);

        dto.setGeneratedDate(r.getGeneratedDate());
        dto.setTitle(r.getTitle());
        dto.setDescription(r.getDescription());
        dto.setFormat(r.getFormat());
        dto.setFileUrl(r.getFileUrl());

        if (r.getGeneratedBy() != null) {
            dto.setGeneratedByUserId(r.getGeneratedBy().getId());
            dto.setGeneratedByUsername(r.getGeneratedBy().getUsername());
        }

        if (r.getProject() != null) {
            dto.setProjectId(r.getProject().getProjectId());
            dto.setProjectName(r.getProject().getTitle());
        }

        if (r.getProgram() != null) {
            dto.setProgramId(r.getProgram().getProgramId());
            dto.setProgramName(r.getProgram().getTitle());
        }

        if (r.getIncentive() != null) {
            dto.setIncentiveId(r.getIncentive().getIncentiveId());
        }

        return dto;
    }
}