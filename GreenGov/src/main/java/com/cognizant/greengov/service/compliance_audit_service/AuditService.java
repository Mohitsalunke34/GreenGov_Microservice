package com.cognizant.greengov.service.compliance_audit_service;

import java.util.List;

import com.cognizant.greengov.dto.complianceauditdto.AuditCreateRequestDTO;
import com.cognizant.greengov.dto.complianceauditdto.AuditResponseDTO;
import com.cognizant.greengov.model.Enums.AuditStatus;

public interface AuditService {

	AuditResponseDTO startAudit(AuditCreateRequestDTO dto, Long auditorUserId);

	AuditResponseDTO closeAudit(Long auditId, AuditStatus finalStatus);

	List<AuditResponseDTO> getAuditsByOfficer(Long officerId);

	List<AuditResponseDTO> getAuditsByCompliance(Long complianceId);

	List<AuditResponseDTO> getAuditsByStatus(AuditStatus status);
}