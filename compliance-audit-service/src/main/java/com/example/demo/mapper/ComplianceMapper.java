package com.example.demo.mapper;

import com.example.demo.dto.compliance_audit.ComplianceResponseDTO;
import com.example.demo.model.ComplianceRecord;

public final class ComplianceMapper {

	public static ComplianceResponseDTO toDTO(ComplianceRecord record) {

		ComplianceResponseDTO dto = new ComplianceResponseDTO();
		dto.setId(record.getId());
		dto.setSubjectType(record.getSubjectType().name());
		dto.setSubjectId(record.getSubjectId());
		dto.setResult(record.getResult().name());
		dto.setAuditStatus(record.getAuditStatus().name());
		dto.setNotes(record.getNotes());
		dto.setEvidenceURL(record.getEvidenceURL());
		dto.setRecordedDate(record.getRecordedDate());

		return dto;
	}
}