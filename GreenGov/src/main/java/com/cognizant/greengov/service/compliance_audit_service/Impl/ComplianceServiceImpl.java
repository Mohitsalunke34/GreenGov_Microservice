package com.cognizant.greengov.service.compliance_audit_service.Impl;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.cognizant.greengov.dto.complianceauditdto.ComplianceRecordCreateRequestDTO;
import com.cognizant.greengov.dto.complianceauditdto.ComplianceRecordResponseDTO;
import com.cognizant.greengov.model.Enums.ComplianceResult;
import com.cognizant.greengov.model.Enums.ComplianceSubjectType;
import com.cognizant.greengov.model.compliance_audit.ComplianceRecord;
import com.cognizant.greengov.model.incentive_subsidy.Incentive;
import com.cognizant.greengov.model.register_login.ParticipantProfile;
import com.cognizant.greengov.model.register_login.UserAccount;
import com.cognizant.greengov.model.sustainability_renewable_proj.EnergyProgram;
import com.cognizant.greengov.model.sustainability_renewable_proj.SustainabilityProject;
import com.cognizant.greengov.modelmapper.ComplianceRecordMapper;
import com.cognizant.greengov.repository.audit_compliance_repo.ComplianceRecordRepository;
import com.cognizant.greengov.repository.disbursement_incentive_repo.IncentiveRepository;
import com.cognizant.greengov.repository.register_login_repo.ParticipantProfileRepository;
import com.cognizant.greengov.repository.register_login_repo.UserAccountRepository;
import com.cognizant.greengov.repository.sustainability_repo.EnergyProgramRepository;
import com.cognizant.greengov.repository.sustainability_repo.SustainabilityProjectRepository;
import com.cognizant.greengov.service.compliance_audit_service.ComplianceService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ComplianceServiceImpl implements ComplianceService {

	private final ComplianceRecordRepository complianceRepo;
	private final ParticipantProfileRepository participantRepo;
	private final UserAccountRepository userRepo;
	private final SustainabilityProjectRepository projectrepo;
	private final EnergyProgramRepository programrepo;
	private final IncentiveRepository incentiverepo;

//	public ComplianceServiceImpl(ComplianceRecordRepository complianceRepo,
//			ParticipantProfileRepository participantRepo, UserAccountRepository userRepo) {
//		this.complianceRepo = complianceRepo;
//		this.participantRepo = participantRepo;
//		this.userRepo = userRepo;
//	}

	@Override
	public ComplianceRecordResponseDTO recordCompliance(ComplianceRecordCreateRequestDTO dto,
			Long complianceOfficerUserId) {
		// ---------- Fail-fast validation (clear messages → HTTP 400 via
		// @ControllerAdvice) ----------
		if (complianceOfficerUserId == null) {
			throw new IllegalArgumentException("officerUserId is required");
		}
		if (dto == null) {
			throw new IllegalArgumentException("Request body is required");
		}
		if (dto.getParticipantId() == null) {
			throw new IllegalArgumentException("participantId is required");
		}
		if (dto.getSubjectId() == null) {
			throw new IllegalArgumentException("subjectId is required");
		}
		if (!StringUtils.hasText(dto.getSubjectType())) {
			throw new IllegalArgumentException("subjectType is required");
		}
		if (!StringUtils.hasText(dto.getResult())) {
			throw new IllegalArgumentException("result is required");
		}

		// ---------- Lookups ----------
		ParticipantProfile participant = participantRepo.findById(dto.getParticipantId())
				.orElseThrow(() -> new IllegalArgumentException("Participant not found"));

		UserAccount officer = userRepo.findById(complianceOfficerUserId)
				.orElseThrow(() -> new IllegalArgumentException("Officer not found"));

		// ---------- Defensive enum conversion (case-insensitive) ----------
		ComplianceSubjectType subjectType;
		ComplianceResult result;
		try {
			subjectType = ComplianceSubjectType.valueOf(dto.getSubjectType().trim());
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("Invalid subjectType. Allowed: PROJECT, PROGRAM, INCENTIVE");
		}
		try {
			result = ComplianceResult.valueOf(dto.getResult().trim());
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("Invalid result. Allowed: PASS, FAIL, NEEDS_REVIEW");
		}

		// ---------- Build entity ----------
		Instant now = Instant.now();

		ComplianceRecord record = new ComplianceRecord();

		record.setSubjectType(subjectType);
		record.setSubjectId(dto.getSubjectId());
		record.setParticipant(participant);
		record.setResult(result);
		record.setNotes(dto.getNotes());
		record.setEvidenceURL(dto.getEvidenceURL() != null ? dto.getEvidenceURL() : "Evidence URL not provided");
		record.setRecordedDate(dto.getRecordedDate() != null ? dto.getRecordedDate() : now);
		record.setComplianceManager(officer);

		switch (subjectType) {
		case PROJECT -> {
			SustainabilityProject project = projectrepo.findById(dto.getSubjectId())
					.orElseThrow(() -> new IllegalArgumentException("Project Not Found as per SubjectId"));
			record.setProject(project);
		}
		case PROGRAM -> {
			EnergyProgram program = programrepo.findById(dto.getSubjectId())
					.orElseThrow(() -> new IllegalArgumentException("Program not found as per SubjectId"));
			record.setProgram(program);
		}
		case INCENTIVE -> {
			Incentive incentive = incentiverepo.findById(dto.getSubjectId())
					.orElseThrow(() -> new IllegalArgumentException("Incentive not found as per SubjectId"));
			record.setIncentive(incentive);
		}
		}
		// ---------- Audit fields ----------

		record.setCreatedAt(now);
		record.setUpdatedAt(now);

		record.setCreatedBy(officer.getUsername());
		record.setUpdatedBy(officer.getUsername());

		// ---------- Persist ----------
		ComplianceRecord saved = complianceRepo.save(record);

		// ---------- Map & return ----------
		return ComplianceRecordMapper.toDTO(saved);
	}

	// Compliance records of a single participant
	@Override
	public List<ComplianceRecordResponseDTO> getByParticipant(Long participantId) {
		ParticipantProfile profile = participantRepo.findById(participantId)
				.orElseThrow(() -> new IllegalArgumentException("The articipant is not there in compliance record"));
		return complianceRepo.findByParticipant(profile).stream().map((x) -> ComplianceRecordMapper.toDTO(x)).toList();
	}

	// I had to manually map here as entity is returning direct fileds.
	@Override
	public List<ComplianceRecordResponseDTO> getBySubjectTypeandSubjectId(ComplianceSubjectType subjectType,
			Long subjectId) {

		return complianceRepo.findBySubjectTypeAndSubjectId(subjectType, subjectId).stream()
				.map((x) -> ComplianceRecordMapper.toDTO(x)).toList();
	}

}