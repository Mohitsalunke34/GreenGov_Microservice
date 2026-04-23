package com.cognizant.greengov.modelmapper;

import com.cognizant.greengov.dto.complianceauditdto.ComplianceRecordResponseDTO;
import com.cognizant.greengov.model.compliance_audit.ComplianceRecord;

public class ComplianceRecordMapper {

	private ComplianceRecordMapper() {
	}

	public static ComplianceRecordResponseDTO toDTO(ComplianceRecord entity) {
		ComplianceRecordResponseDTO dto = new ComplianceRecordResponseDTO();
		dto.setComplianceId(entity.getId());
		dto.setSubjectType(entity.getSubjectType().name());

		if (entity.getProject() != null) {
			dto.setProjectId(entity.getProject().getProjectId());
		}

		if (entity.getProgram() != null) {
			dto.setProgramId(entity.getProgram().getProgramId());
		}

		if (entity.getIncentive() != null) {
			dto.setIncentiveId(entity.getIncentive().getIncentiveId());
		}

		dto.setSubjectId(entity.getSubjectId());
		dto.setParticipantId(entity.getParticipant().getId());
		dto.setResult(entity.getResult().name());
		dto.setRecordedDate(entity.getRecordedDate());
		dto.setNotes(entity.getNotes());
		dto.setComplianceManagerUserId(entity.getComplianceManager().getId());
		return dto;
	}
}