package com.example.demo.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.clients.EnergyProgramClient;
import com.example.demo.clients.IncentiveClient;
import com.example.demo.clients.NotificationClient;
import com.example.demo.clients.SustainabilityProjectClient;
import com.example.demo.clients.UserClient;
import com.example.demo.dto.NotificationRequestDTO;
import com.example.demo.dto.UserBasicDTO;
import com.example.demo.dto.client_dto.SubjectLookupDTO;
import com.example.demo.dto.compliance_audit.ComplianceRecordCreateRequestDTO;
import com.example.demo.dto.compliance_audit.ComplianceResponseDTO;
import com.example.demo.exception.ServiceUnavailableException;
import com.example.demo.mapper.ComplianceMapper;
import com.example.demo.model.ComplianceRecord;
import com.example.demo.model.Enums.ComplianceResult;
import com.example.demo.model.Enums.ComplianceSubjectType;
import com.example.demo.repo.ComplianceRecordRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ComplianceServiceImpl implements ComplianceService {

	private final ComplianceRecordRepository complianceRepo;
	private final UserClient userClient;
	private final SustainabilityProjectClient sustainabilityClient;
	private final EnergyProgramClient programClient;
	private final IncentiveClient incentiveClient;
	private final NotificationClient notificationClient;

	// ===============================
	// MAIN BUSINESS OPERATION
	// ===============================
	@Override
	public ComplianceResponseDTO recordCompliance(ComplianceRecordCreateRequestDTO dto, Long complianceOfficerUserId) {

		log.info("Recording compliance | subjectType={} | subjectId={}", dto.getSubjectType(), dto.getSubjectId());

		UserBasicDTO officer = fetchOfficer(complianceOfficerUserId);

		ComplianceSubjectType subjectType = ComplianceSubjectType.valueOf(dto.getSubjectType());

		validateSubject(subjectType, dto.getSubjectId());

		ComplianceRecord record = new ComplianceRecord();
		record.setSubjectType(subjectType);
		record.setSubjectId(dto.getSubjectId());
		record.setComplianceManagerUserId(complianceOfficerUserId);
		record.setResult(ComplianceResult.valueOf(dto.getResult()));
		record.setNotes(dto.getNotes());
		record.setEvidenceURL(dto.getEvidenceURL());
		record.setRecordedDate(Instant.now());
		record.setCreatedBy(officer.getUsername());
		record.setUpdatedBy(officer.getUsername());

		ComplianceRecord saved = complianceRepo.save(record);

		notifyCompliance("Compliance recorded for " + subjectType + " ID " + dto.getSubjectId() + " with result "
				+ dto.getResult(), "COMPLIANCE", saved.getId());

		return ComplianceMapper.toDTO(saved);
	}

	// ===============================
	// READ OPERATIONS
	// ===============================

	@Override
	public List<ComplianceResponseDTO> getBySubject(ComplianceSubjectType subjectType, Long subjectId) {

		return complianceRepo.findBySubjectTypeAndSubjectId(subjectType, subjectId).stream()
				.map(ComplianceMapper::toDTO).toList();
	}

	// ===============================
	// REMOTE CALLS WITH RESILIENCE
	// ===============================

	// ---- AUTH SERVICE (CRITICAL) ----
	@CircuitBreaker(name = "authService", fallbackMethod = "authFallback")
	private UserBasicDTO fetchOfficer(Long officerUserId) {
		return userClient.getUserById(officerUserId);
	}

	private UserBasicDTO authFallback(Long officerUserId, Throwable ex) {
		log.error("Auth service unavailable | officerId={}", officerUserId, ex);
		throw new IllegalStateException("Authentication service unavailable");
	}

	// ---- SUBJECT VALIDATION (CRITICAL) ----
	@CircuitBreaker(name = "subjectValidationService", fallbackMethod = "subjectFallback")
	private void validateSubject(ComplianceSubjectType type, Long subjectId) {
		try {
			switch (type) {
			case PROJECT -> assertExists(sustainabilityClient.projectExists(subjectId), "Project not found");

			case PROGRAM -> assertExists(programClient.programExists(subjectId), "Program not found");

			case INCENTIVE -> assertExists(incentiveClient.incentiveExists(subjectId), "Incentive not found");
			}
		} catch (feign.FeignException ex) {
			throw new ServiceUnavailableException("Dependent service for " + type + " is currently unavailable");
		}
	}

	private void subjectFallback(ComplianceSubjectType type, Long subjectId, Throwable ex) {

		log.error("Subject validation service unavailable | type={} subjectId={}", type, subjectId, ex);

		throw new ServiceUnavailableException("Subject validation service is currently unavailable");
	}

	// Program Client call with circuit braker
	@Override
	@CircuitBreaker(name = "subjectLookupService", fallbackMethod = "programSubjectsFallback")
	public List<SubjectLookupDTO> getProgramSubjects() {
		return programClient.getProgramSubjects();
	}

	// fallback method for program
	private List<SubjectLookupDTO> programSubjectsFallback(Throwable ex) {
		log.error("Program subject lookup failed", ex);
		throw new ServiceUnavailableException("Unable to fetch program list at the moment");
	}

	// Project Client call with circuit braker
	@Override
	@CircuitBreaker(name = "subjectLookupService", fallbackMethod = "projectSubjectsFallback")
	public List<SubjectLookupDTO> getProjectSubjects() {
		return sustainabilityClient.getProjectSubjects();
	}

	private List<SubjectLookupDTO> projectSubjectsFallback(Throwable ex) {
		log.error("Project subject lookup failed", ex);
		throw new ServiceUnavailableException("Unable to fetch project list at the moment");
	}

	// Incentive Client call with circuit braker
	@Override
	@CircuitBreaker(name = "subjectLookupService", fallbackMethod = "incentiveSubjectsFallback")
	public List<SubjectLookupDTO> getIncentiveSubjects() {
		return incentiveClient.getIncentiveSubjects();
	}

	@SuppressWarnings("unused")
	private List<SubjectLookupDTO> incentiveSubjectsFallback(Throwable ex) {
		log.error("Incentive subject lookup failed", ex);
		throw new ServiceUnavailableException("Unable to fetch incentive list at the moment");
	}

	// ---- NOTIFICATION SERVICE (NON‑CRITICAL) ----
	@CircuitBreaker(name = "notificationService", fallbackMethod = "notificationFallback")
	@Retry(name = "notificationService")
	private void notifyCompliance(String message, String category, Long entityId) {

		NotificationRequestDTO request = NotificationRequestDTO.builder().userId(1L).message(message).category(category)
				.entityId(entityId).sendEmail(false).email("admin@greengov.com").build();

		notificationClient.createNotification(request);
		log.info("Compliance notification sent | entityId={}", entityId);
	}

	private void notificationFallback(String message, String category, Long entityId, Throwable ex) {

		log.warn("Notification skipped (service down) | entityId={}", entityId, ex);
	}

	// ===============================
	// HELPERS
	// ===============================
	private void assertExists(Boolean exists, String message) {
		if (!Boolean.TRUE.equals(exists)) {
			throw new IllegalArgumentException(message);
		}
	}
}
