package com.example.demo.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.client.OfficerClient;
import com.example.demo.client.ProgramClient;
import com.example.demo.dto.IncentiveCreateRequestDTO;
import com.example.demo.dto.IncentiveResponseDTO;
import com.example.demo.dto.OfficerDTO;
import com.example.demo.dto.ProgramDTO;
import com.example.demo.dto.client_dto.ApprovedApplicationLookupDTO;
import com.example.demo.model.Incentive;
import com.example.demo.modelMapper.IncentiveMapper;
import com.example.demo.repo.DisbursementRepository;
import com.example.demo.repo.IncentiveRepository;
import com.example.demo.service.IncentiveService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class IncentiveServiceImpl implements IncentiveService {

	private final IncentiveRepository incentiveRepo;
	private final ProgramClient programClient;
	private final OfficerClient officerClient;
	private final DisbursementRepository disbursementRepo;

	@Override
	@Transactional
	public IncentiveResponseDTO createIncentive(IncentiveCreateRequestDTO dto, Long officerUserId) {

		/* ======================= 0️⃣ Validate Officer ======================= */
		List<OfficerDTO> officers = officerClient.getActiveDisbursementOfficers(officerUserId);

		OfficerDTO officer = officers.stream().filter(o -> o.getUserId().equals(officerUserId)).findFirst()
				.orElseThrow(() -> new RuntimeException("User is not an APPROVED DISBURSEMENT officer"));

		log.info("Creating incentive by officer {}", officer.getUsername());

		/*
		 * ======================= 1️⃣ Resolve APPROVED Application
		 * =======================
		 */
		List<ApprovedApplicationLookupDTO> applications = programClient
				.getApprovedApplicationsByParticipant(dto.getParticipantId());

		if (applications.isEmpty()) {
			throw new IllegalStateException("No approved application found for this participant");
		}

		// ✅ Pick first approved application (can be enhanced later)
		ApprovedApplicationLookupDTO application = applications.get(0);

		Long applicationId = application.getApplicationId();
		Long programId = application.getProgramId();

		/* ======================= 2️⃣ Prevent duplicates ======================= */
		incentiveRepo.findByApplicationId(applicationId).ifPresent(existing -> {
			throw new IllegalStateException("Incentive already exists for this application");
		});

		/* ======================= 3️⃣ Fetch Program ======================= */
		ProgramDTO program = programClient.getProgramById(programId);

		if (!"ACTIVE".equalsIgnoreCase(program.getStatus())) {
			throw new IllegalStateException("Program not active");
		}

		/* ======================= 4️⃣ Budget Check ======================= */
		BigDecimal requestedAmount = BigDecimal.valueOf(dto.getAmount());

		BigDecimal remainingBudget = program.getRemainingProgramBudget() != null ? program.getRemainingProgramBudget()
				: program.getBudget();

		if (requestedAmount.compareTo(remainingBudget) > 0) {
			throw new IllegalStateException("Insufficient program budget");
		}

		/* ======================= 5️⃣ Deduct Budget ======================= */
		programClient.deductProgramBudget(programId, requestedAmount);

		/* ======================= 6️⃣ Persist Incentive ======================= */
		Incentive incentive = Incentive.builder().applicationId(applicationId) // ✅ INTERNAL ONLY
				.programId(programId).beneficiaryId(dto.getParticipantId()).amount(dto.getAmount())
				.remainingAmount(dto.getAmount()).sanctionedDate(LocalDate.now()).status("APPROVED")
				.approvedBy(officerUserId).build();

		Incentive saved = incentiveRepo.save(incentive);

		log.info("Incentive created | IncentiveId={} | ParticipantId={}", saved.getIncentiveId(),
				dto.getParticipantId());

		return IncentiveMapper.toDTO(saved);
	}

	/* ======================= READ APIs ======================= */

	@Override
	public List<IncentiveResponseDTO> getByBeneficiary(Long beneficiaryId) {
		return incentiveRepo.findByBeneficiaryId(beneficiaryId).stream().map(IncentiveMapper::toDTO).toList();
	}

	@Override
	public IncentiveResponseDTO getByIncentiveId(Long incentiveId) {
		return incentiveRepo.findByIncentiveId(incentiveId).map(IncentiveMapper::toDTO)
				.orElseThrow(() -> new RuntimeException("Incentive not found"));
	}

	@Override
	@Transactional
	public IncentiveResponseDTO deleteIncentive(Long incentiveId) {
		Incentive incentive = incentiveRepo.findById(incentiveId)
				.orElseThrow(() -> new IllegalArgumentException("Incentive not found"));
		IncentiveResponseDTO response = IncentiveMapper.toDTO(incentive);
		incentiveRepo.delete(incentive);
		return response;
	}

	@Override
	public List<IncentiveResponseDTO> getAllIncentives() {
		return incentiveRepo.findAll().stream().map(IncentiveMapper::toDTO).toList();
	}

	@Override
	public Map<String, Object> getIncentiveReportMetrics() {

		long totalIncentives = incentiveRepo.count();
		long totalDisbursements = disbursementRepo.count();
		Double totalDisbursedAmount = disbursementRepo.getTotalDisbursedAmount();

		Map<String, Object> metrics = new HashMap<>();
		metrics.put("totalIncentives", totalIncentives);
		metrics.put("totalDisbursements", totalDisbursements);
		metrics.put("totalAmountDisbursed", totalDisbursedAmount != null ? totalDisbursedAmount : 0.0);

		return metrics;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean incentiveExists(Long incentiveId) {
		return incentiveRepo.existsById(incentiveId);
	}

	@Override
	public IncentiveResponseDTO getByApplication(Long applicationId) {
		// TODO Auto-generated method stub
		return null;
	}
}
