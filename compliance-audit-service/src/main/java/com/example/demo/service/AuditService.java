package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.compliance_audit.AuditCreateRequestDTO;
import com.example.demo.dto.compliance_audit.AuditResponseDTO;
import com.example.demo.model.Enums.AuditStatus;

public interface AuditService {

	AuditResponseDTO startAudit(AuditCreateRequestDTO dto, Long auditorUserId);

	List<AuditResponseDTO> getByOfficer(Long officerId);

	List<AuditResponseDTO> getByCompliance(Long complianceId);

	List<AuditResponseDTO> getByStatus(AuditStatus status);

	AuditResponseDTO closeAudit(Long auditId, AuditStatus finalStatus, Long auditorUserId);

	List<AuditResponseDTO> getAllAudit();
}