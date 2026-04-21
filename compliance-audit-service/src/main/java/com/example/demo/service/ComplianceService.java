package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.compliance_audit.ComplianceRecordCreateRequestDTO;
import com.example.demo.dto.compliance_audit.ComplianceResponseDTO;
import com.example.demo.model.Enums.ComplianceSubjectType;

public interface ComplianceService {

	ComplianceResponseDTO recordCompliance(ComplianceRecordCreateRequestDTO dto, Long officerUserId);

	List<ComplianceResponseDTO> getByParticipant(Long participantId);

	List<ComplianceResponseDTO> getBySubject(ComplianceSubjectType subjectType, Long subjectId);
}
