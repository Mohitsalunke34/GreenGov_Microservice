package com.example.demo.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.clients.EnergyProgramClient;
import com.example.demo.clients.IncentiveClient;
import com.example.demo.clients.ParticipantClient;
import com.example.demo.clients.SustainabilityProjectClient;
import com.example.demo.clients.UserClient;
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

@Service
@RequiredArgsConstructor
@Transactional
public class ComplianceServiceImpl implements ComplianceService {

	private final ComplianceRecordRepository complianceRepo;
	private final ParticipantClient participantClient;
	private final UserClient userClient;
	private final SustainabilityProjectClient sustainabilityClient;
	private final EnergyProgramClient programClient;
	private final IncentiveClient incentiveClient;

	/**
	 * Record a compliance result for a PROJECT / PROGRAM / INCENTIVE.
	 */
	@Override
	public ComplianceResponseDTO recordCompliance(ComplianceRecordCreateRequestDTO dto, Long complianceOfficerUserId) {

		// 1️⃣ Validate compliance officer (Auth Service)
		UserBasicDTO officer = userClient.getUserById(complianceOfficerUserId);

		// 2️⃣ Validate participant (Profile Service)
		ParticipantBasicDTO participant = participantClient.getParticipant(dto.getParticipantId());

		if (!participant.isVerified()) {
			throw new IllegalStateException("Participant is not verified");
		}

		// 3️⃣ Parse and validate subject type
		ComplianceSubjectType subjectType;
		try {
			subjectType = ComplianceSubjectType.valueOf(dto.getSubjectType());
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("Invalid subjectType. Allowed values: PROJECT, PROGRAM, INCENTIVE");
		}

		// 4️⃣ Validate subject existence (Owner microservice)
		switch (subjectType) {
		case PROJECT -> assertExists(sustainabilityClient.projectExists(dto.getSubjectId()), "Project not found");

		case PROGRAM -> assertExists(programClient.programExists(dto.getSubjectId()), "Program not found");

		case INCENTIVE -> assertExists(incentiveClient.incentiveExists(dto.getSubjectId()), "Incentive not found");
		}

		// 5️⃣ Persist compliance record
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

		// 6️⃣ Map entity → minimal DTO
		return ComplianceMapper.toDTO(saved);
	}

	/**
	 * Fetch all compliance records for a participant.
	 */
	@Override
	public List<ComplianceResponseDTO> getByParticipant(Long participantId) {
		return complianceRepo.findByParticipantId(participantId).stream().map(ComplianceMapper::toDTO).toList();
	}

	/**
	 * Fetch all compliance records for a subject (PROJECT / PROGRAM / INCENTIVE).
	 */
	@Override
	public List<ComplianceResponseDTO> getBySubject(ComplianceSubjectType subjectType, Long subjectId) {

		return complianceRepo.findBySubjectTypeAndSubjectId(subjectType, subjectId).stream()
				.map(ComplianceMapper::toDTO).toList();
	}

	// ✅ Small helper for existence validation
	private void assertExists(Boolean exists, String message) {
		if (!Boolean.TRUE.equals(exists)) {
			throw new IllegalArgumentException(message);
		}
	}
}