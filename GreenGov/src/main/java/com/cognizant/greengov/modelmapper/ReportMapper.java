package com.cognizant.greengov.modelmapper;

import com.cognizant.greengov.dto.complianceauditdto.ReportResponseDTO;
import com.cognizant.greengov.model.compliance_audit.Report;

public class ReportMapper {

	private ReportMapper() {
	}

	public static ReportResponseDTO toDTO(Report entity) {
		ReportResponseDTO dto = new ReportResponseDTO();
		dto.setReportId(entity.getId());
		dto.setScope(entity.getScope().name());
		dto.setGeneratedDate(entity.getGeneratedDate());
		dto.setTitle(entity.getTitle());
		dto.setDescription(entity.getDescription());
		dto.setFormat(entity.getFormat());
		dto.setStatus(entity.getStatus().name());
		dto.setFileUrl(entity.getFileUrl());

		if (entity.getGeneratedBy() != null) {
			dto.setGeneratedByUserId(entity.getGeneratedBy().getId());
		}
		return dto;
	}
}