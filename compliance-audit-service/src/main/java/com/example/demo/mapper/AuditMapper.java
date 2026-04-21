package com.example.demo.mapper;

import com.example.demo.dto.compliance_audit.AuditResponseDTO;
import com.example.demo.model.Audit;

public final class AuditMapper {

	public static AuditResponseDTO toDTO(Audit audit) {

		AuditResponseDTO dto = new AuditResponseDTO();
		dto.setId(audit.getId());
		dto.setComplianceId(audit.getComplianceId());
		dto.setOfficerUserId(audit.getOfficerUserId());
		dto.setStatus(audit.getStatus().name());
		dto.setScope(audit.getScope().name());
		dto.setSeverity(audit.getSeverity());
		dto.setFindings(audit.getFindings());
		dto.setOpenedDate(audit.getOpenedDate());
		dto.setClosedDate(audit.getClosedDate());

		return dto;
	}
}