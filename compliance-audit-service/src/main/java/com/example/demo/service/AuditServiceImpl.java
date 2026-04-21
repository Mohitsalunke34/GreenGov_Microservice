package com.example.demo.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.clients.UserClient;
import com.example.demo.dto.UserBasicDTO;
import com.example.demo.dto.compliance_audit.AuditCreateRequestDTO;
import com.example.demo.dto.compliance_audit.AuditResponseDTO;
import com.example.demo.mapper.AuditMapper;
import com.example.demo.model.Audit;
import com.example.demo.model.Enums.AuditStatus;
import com.example.demo.model.Enums.ReportScope;
import com.example.demo.repo.AuditRepository;
import com.example.demo.repo.ComplianceRecordRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditServiceImpl implements AuditService {

	private final AuditRepository auditRepo;
	private final ComplianceRecordRepository complianceRepo;
	private final UserClient userClient;

	@Override
	public AuditResponseDTO startAudit(AuditCreateRequestDTO dto, Long auditorUserId) {

		UserBasicDTO auditor = userClient.getUserById(auditorUserId);

		complianceRepo.findById(dto.getComplianceId())
				.orElseThrow(() -> new IllegalArgumentException("Compliance record not found"));

		Audit audit = new Audit();
		audit.setComplianceId(dto.getComplianceId());
		audit.setOfficerUserId(auditorUserId);
		audit.setOpenedDate(Instant.now());
		audit.setStatus(AuditStatus.IN_PROGRESS);
		audit.setScope(ReportScope.COMPLIANCE);
		audit.setCreatedBy(auditor.getUsername());
		audit.setUpdatedBy(auditor.getUsername());

		return AuditMapper.toDTO(auditRepo.save(audit));
	}

	@Override
	public List<AuditResponseDTO> getByOfficer(Long officerId) {
		return auditRepo.findByOfficerUserId(officerId).stream().map(AuditMapper::toDTO).toList();
	}

	@Override
	public List<AuditResponseDTO> getByCompliance(Long complianceId) {
		return auditRepo.findByComplianceId(complianceId).stream().map(AuditMapper::toDTO).toList();
	}

	@Override
	public List<AuditResponseDTO> getByStatus(AuditStatus status) {
		return auditRepo.findByStatus(status).stream().map(AuditMapper::toDTO).toList();
	}

	@Override
	public AuditResponseDTO closeAudit(Long auditId, AuditStatus finalStatus, Long auditorUserId) {

		Audit audit = auditRepo.findById(auditId).orElseThrow(() -> new IllegalArgumentException("Audit not found"));

		if (audit.getStatus() != AuditStatus.IN_PROGRESS) {
			throw new IllegalStateException("Only IN_PROGRESS audits can be closed");
		}

		if (finalStatus != AuditStatus.COMPLETED && finalStatus != AuditStatus.CANCELLED) {

			throw new IllegalArgumentException("Invalid final audit status");
		}

		UserBasicDTO auditor = userClient.getUserById(auditorUserId);

		audit.setStatus(finalStatus);
		audit.setClosedDate(Instant.now());
		audit.setUpdatedBy(auditor.getUsername());

		return AuditMapper.toDTO(auditRepo.save(audit));
	}
}
