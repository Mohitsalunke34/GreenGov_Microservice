package com.cognizant.greengov.service.compliance_audit_service;

import java.util.List;

import com.cognizant.greengov.dto.complianceauditdto.ComplianceRecordCreateRequestDTO;
import com.cognizant.greengov.dto.complianceauditdto.ComplianceRecordResponseDTO;
import com.cognizant.greengov.model.Enums.ComplianceSubjectType;

public interface ComplianceService {

	ComplianceRecordResponseDTO recordCompliance(ComplianceRecordCreateRequestDTO dto, Long complianceOfficerUserId);

	List<ComplianceRecordResponseDTO> getByParticipant(Long participantId);

	List<ComplianceRecordResponseDTO> getBySubjectTypeandSubjectId(ComplianceSubjectType subjectType,Long subjectId);
}