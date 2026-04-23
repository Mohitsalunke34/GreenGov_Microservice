package com.example.demo.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.clients.EnergyProgramClient;
import com.example.demo.clients.IncentiveClient;
import com.example.demo.clients.NotificationClient;
import com.example.demo.clients.ParticipantClient;
import com.example.demo.clients.SustainabilityProjectClient;
import com.example.demo.clients.UserClient;
import com.example.demo.dto.NotificationRequestDTO;
import com.example.demo.dto.ParticipantBasicDTO;
import com.example.demo.dto.UserBasicDTO;
import com.example.demo.dto.compliance_audit.ComplianceRecordCreateRequestDTO;
import com.example.demo.dto.compliance_audit.ComplianceResponseDTO;
import com.example.demo.mapper.ComplianceMapper;
import com.example.demo.model.ComplianceRecord;
import com.example.demo.model.Enums.ComplianceResult;
import com.example.demo.model.Enums.ComplianceSubjectType;
import com.example.demo.repo.ComplianceRecordRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ComplianceServiceImpl implements ComplianceService {

	private final ComplianceRecordRepository complianceRepo;
	private final ParticipantClient participantClient;
	private final UserClient userClient;
	private final SustainabilityProjectClient sustainabilityClient;
	private final EnergyProgramClient programClient;
	private final IncentiveClient incentiveClient;
	private final NotificationClient notificationClient;

	@Override
	public ComplianceResponseDTO recordCompliance(ComplianceRecordCreateRequestDTO dto, Long complianceOfficerUserId) {

		// ✅ Validate officer
		UserBasicDTO officer = userClient.getUserById(complianceOfficerUserId);

		// ✅ Validate participant
		ParticipantBasicDTO participant = participantClient.getParticipant(dto.getParticipantId());

		if (!participant.isVerified()) {
			throw new IllegalStateException("Participant is not verified");
		}

		// ✅ Parse subject type
		ComplianceSubjectType subjectType = ComplianceSubjectType.valueOf(dto.getSubjectType());

		// ✅ Validate subject existence
		switch (subjectType) {
		case PROJECT -> assertExists(sustainabilityClient.projectExists(dto.getSubjectId()), "Project not found");

		case PROGRAM -> assertExists(programClient.programExists(dto.getSubjectId()), "Program not found");

		case INCENTIVE -> assertExists(incentiveClient.incentiveExists(dto.getSubjectId()), "Incentive not found");
		}

		// ✅ Persist compliance record
		ComplianceRecord record = new ComplianceRecord();
		record.setSubjectType(subjectType);
		record.setSubjectId(dto.getSubjectId());
		record.setParticipantId(dto.getParticipantId());
		record.setComplianceManagerUserId(complianceOfficerUserId);
		record.setResult(ComplianceResult.valueOf(dto.getResult()));
		record.setNotes(dto.getNotes());
		record.setEvidenceURL(dto.getEvidenceURL());
		record.setRecordedDate(Instant.now());
		record.setCreatedBy(officer.getUsername());
		record.setUpdatedBy(officer.getUsername());

		ComplianceRecord saved = complianceRepo.save(record);

		// ✅ Send notification (FAIL‑SAFE)
		sendNotification("Compliance recorded for " + subjectType + " ID " + dto.getSubjectId() + " with result "
				+ dto.getResult(), "COMPLIANCE", saved.getId());

		return ComplianceMapper.toDTO(saved);
	}

	@Override
	public List<ComplianceResponseDTO> getByParticipant(Long participantId) {
		return complianceRepo.findByParticipantId(participantId).stream().map(ComplianceMapper::toDTO).toList();
	}

	@Override
	public List<ComplianceResponseDTO> getBySubject(ComplianceSubjectType subjectType, Long subjectId) {

		return complianceRepo.findBySubjectTypeAndSubjectId(subjectType, subjectId).stream()
				.map(ComplianceMapper::toDTO).toList();
	}

	/* ================= HELPERS ================= */

	private void assertExists(Boolean exists, String message) {
		if (!Boolean.TRUE.equals(exists)) {
			throw new IllegalArgumentException(message);
		}
	}

	private void sendNotification(String message, String category, Long entityId) {
		try {
			NotificationRequestDTO request = NotificationRequestDTO.builder().userId(1L) // system/admin
					.message(message).category(category).entityId(entityId).sendEmail(false).email("admin@greengov.com")
					.build();

			notificationClient.createNotification(request);
			log.info("Compliance notification sent");

		} catch (Exception ex) {
			log.error("Compliance notification failed: {}", ex.getMessage());
		}
	}
}