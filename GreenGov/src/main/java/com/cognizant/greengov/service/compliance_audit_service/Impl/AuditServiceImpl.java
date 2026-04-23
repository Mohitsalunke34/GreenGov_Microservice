package com.cognizant.greengov.service.compliance_audit_service.Impl;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cognizant.greengov.dto.complianceauditdto.AuditCreateRequestDTO;
import com.cognizant.greengov.dto.complianceauditdto.AuditResponseDTO;
import com.cognizant.greengov.model.Enums.AuditStatus;
import com.cognizant.greengov.model.Enums.ComplianceAuditStatus;
import com.cognizant.greengov.model.Enums.ReportScope;
import com.cognizant.greengov.model.compliance_audit.Audit;
import com.cognizant.greengov.model.compliance_audit.ComplianceRecord;
import com.cognizant.greengov.model.register_login.UserAccount;
import com.cognizant.greengov.modelmapper.AuditMapper;
import com.cognizant.greengov.repository.audit_compliance_repo.AuditRepository;
import com.cognizant.greengov.repository.audit_compliance_repo.ComplianceRecordRepository;
import com.cognizant.greengov.repository.register_login_repo.UserAccountRepository;
import com.cognizant.greengov.service.compliance_audit_service.AuditService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditServiceImpl implements AuditService {

	private final AuditRepository auditRepo;
	private final UserAccountRepository userRepo;
	private final ComplianceRecordRepository complianceRepo;

	@Override
	public AuditResponseDTO startAudit(AuditCreateRequestDTO dto, Long auditorUserId) {

		UserAccount auditor = userRepo.findById(auditorUserId)
				.orElseThrow(() -> new IllegalArgumentException("Audit manager not found"));

		ComplianceRecord compliance = complianceRepo.findById(dto.getComplianceId())
				.orElseThrow(() -> new IllegalArgumentException("Compliance record not found"));

		Audit audit = new Audit();
		audit.setOfficer(auditor);
		audit.setComplianceRecord(compliance);
		audit.setOpenedDate(Instant.now());
		audit.setStatus(AuditStatus.IN_PROGRESS);
		
		audit.setScope(ReportScope.COMPLIANCE);
		
		audit.setCreatedBy(auditor.getUsername());
		audit.setUpdatedBy(auditor.getUsername());

		return AuditMapper.toDTO(auditRepo.save(audit));
	}

	@Override
	public AuditResponseDTO closeAudit(Long auditId, AuditStatus finalStatus) {

		Audit audit = auditRepo.findById(auditId).orElseThrow(() -> new IllegalArgumentException("Audit not found"));

		if (audit.getStatus() != AuditStatus.IN_PROGRESS) {
			throw new IllegalStateException("Only IN_PROGRESS audits can be closed");
		}

		if (finalStatus != AuditStatus.COMPLETED && finalStatus != AuditStatus.CANCELLED) {
			throw new IllegalArgumentException("Invalid final audit status");
		}

		audit.setStatus(finalStatus);
		audit.setClosedDate(Instant.now());

		ComplianceRecord compliance = audit.getComplianceRecord();
		compliance.setAuditStatus(
				finalStatus == AuditStatus.COMPLETED ? ComplianceAuditStatus.VERIFIED : ComplianceAuditStatus.FLAGGED);

		audit.setUpdatedBy(audit.getOfficer().getUsername());

		return AuditMapper.toDTO(auditRepo.save(audit));
	}

	@Override
	public List<AuditResponseDTO> getAuditsByOfficer(Long officerId) {

		UserAccount officer = userRepo.findById(officerId)
				.orElseThrow(() -> new IllegalArgumentException("Officer not found"));

		return auditRepo.findByOfficer(officer).stream().map(AuditMapper::toDTO).toList();
	}

	@Override
	public List<AuditResponseDTO> getAuditsByCompliance(Long complianceId) {

		return auditRepo.findByComplianceRecordId(complianceId).stream().map(AuditMapper::toDTO).toList();
	}

	@Override
	public List<AuditResponseDTO> getAuditsByStatus(AuditStatus status) {

		return auditRepo.findByStatus(status).stream().map(AuditMapper::toDTO).toList();
	}
}