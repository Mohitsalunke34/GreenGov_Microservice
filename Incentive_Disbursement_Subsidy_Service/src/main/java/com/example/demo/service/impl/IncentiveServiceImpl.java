package com.example.demo.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.client.OfficerClient;
import com.example.demo.client.ProgramClient;
import com.example.demo.dto.ApplicationDTO;
import com.example.demo.dto.IncentiveCreateRequestDTO;
import com.example.demo.dto.IncentiveResponseDTO;
import com.example.demo.dto.OfficerDTO;
import com.example.demo.dto.ProgramDTO;
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

//    @Override
//    @Transactional
//    public IncentiveResponseDTO createIncentive(
//            IncentiveCreateRequestDTO dto,
//            Long officerUserId
//    ) {
//
//        log.info("Creating incentive for Application ID: {}", dto.getApplicationId());
//
//        // 1️⃣ Prevent duplicate incentive per application
//        if (incentiveRepo.findByApplicationId(dto.getApplicationId()).isPresent()) {
//            throw new IllegalStateException(
//                    "Incentive already exists for this application"
//            );
//        }
//
//        // 2️⃣ Fetch Application (Program Service)
//        ApplicationDTO application =
//                programClient.getApplicationById(dto.getApplicationId());
//
//        if (application == null) {
//            throw new IllegalArgumentException("Application not found");
//        }
//
//        // 3️⃣ Validate Application status
//        if (!"APPROVED".equalsIgnoreCase(application.getStatus())) {
//            throw new IllegalStateException(
//                    "Incentive can be created only for APPROVED applications"
//            );
//        }
//
//        // 4️⃣ Fetch Program
//        ProgramDTO program =
//                programClient.getProgramById(application.getProgramId());
//
//        // 5️⃣ Validate Program status
//        if (!"ACTIVE".equalsIgnoreCase(program.getStatus())) {
//            throw new IllegalStateException(
//                    "Program is not ACTIVE. Incentive cannot be issued"
//            );
//        }
//
//        BigDecimal requestedAmount = BigDecimal.valueOf(dto.getAmount());
//
//        // 6️⃣ Budget check (read‑only)
//        BigDecimal remainingBudget =
//                program.getRemainingProgramBudget() != null
//                        ? program.getRemainingProgramBudget()
//                        : program.getBudget();
//
//        if (requestedAmount.compareTo(remainingBudget) > 0) {
//            throw new IllegalStateException("Insufficient program budget");
//        }
//
//        // 7️⃣ Deduct budget (Program Service OWNS budget)
//        programClient.deductProgramBudget(
//                program.getProgramId(),
//                requestedAmount
//        );
//
//        // 8️⃣ Save Incentive locally ✅ (IMPORTANT CHANGE HERE)
//        Incentive incentive = Incentive.builder()
//                .applicationId(dto.getApplicationId())
//                .programId(program.getProgramId())
//                .beneficiaryId(application.getApplicantId())
//                .amount(dto.getAmount())                    // total sanctioned
//                .remainingAmount(dto.getAmount())           // ✅ INITIAL REMAINING AMOUNT
//                .sanctionedDate(LocalDate.now())
//                .status("APPROVED")                          // initial state
//                .approvedBy(officerUserId)
//                .build();
//
//        Incentive saved = incentiveRepo.save(incentive);
//
//        log.info("Incentive {} created successfully", saved.getIncentiveId());
//
//        return IncentiveMapper.toDTO(saved);
//    }
//    @Override
//    public IncentiveResponseDTO getByApplication(Long applicationId) {
//        return incentiveRepo.findByApplicationId(applicationId)
//                .map(IncentiveMapper::toDTO)
//                .orElseThrow(() -> new IllegalArgumentException("No incentive found for Application ID: " + applicationId));
//    }
	@Override
	@Transactional
	public IncentiveResponseDTO createIncentive(IncentiveCreateRequestDTO dto, Long officerUserId) {

		/*
		 * ======================= 0️⃣ Validate Officer =======================
		 */
		List<OfficerDTO> officers = officerClient.getActiveDisbursementOfficers(officerUserId);

		OfficerDTO officer = officers.stream().filter(o -> o.getUserId().equals(officerUserId)).findFirst()
				.orElseThrow(() -> new RuntimeException("User is not an APPROVED DISBURSEMENT officer"));

		log.info("Creating incentive by officer {}", officer.getUsername());

		/*
		 * ======================= 1️⃣ Prevent duplicates =======================
		 */
		incentiveRepo.findByApplicationId(dto.getApplicationId()).ifPresent(existing -> {
			throw new IllegalStateException("Incentive already exists for this application");
		});

		/*
		 * ======================= 2️⃣ Fetch Application =======================
		 */
		ApplicationDTO application = programClient.getApplicationById(dto.getApplicationId());

		if (!"APPROVED".equalsIgnoreCase(application.getStatus())) {
			throw new IllegalStateException("Application not approved");
		}

		/*
		 * ======================= 3️⃣ Fetch Program =======================
		 */
		ProgramDTO program = programClient.getProgramById(application.getProgramId());

		if (!"ACTIVE".equalsIgnoreCase(program.getStatus())) {
			throw new IllegalStateException("Program not active");
		}

		/*
		 * ======================= 4️⃣ Budget Check =======================
		 */
		BigDecimal requestedAmount = BigDecimal.valueOf(dto.getAmount());

		BigDecimal remainingBudget = program.getRemainingProgramBudget() != null ? program.getRemainingProgramBudget()
				: program.getBudget();

		if (requestedAmount.compareTo(remainingBudget) > 0) {
			throw new IllegalStateException("Insufficient program budget");
		}

		/*
		 * ======================= 5️⃣ Deduct Budget =======================
		 */
		programClient.deductProgramBudget(program.getProgramId(), requestedAmount);

		/*
		 * ======================= 6️⃣ Persist Incentive =======================
		 */
		Incentive incentive = Incentive.builder().applicationId(dto.getApplicationId())
				.programId(program.getProgramId()).beneficiaryId(application.getApplicantId()).amount(dto.getAmount())
				.remainingAmount(dto.getAmount()).sanctionedDate(LocalDate.now()).status("APPROVED")
				.approvedBy(officerUserId).build();

		Incentive saved = incentiveRepo.save(incentive);

		log.info("Incentive {} created successfully", saved.getIncentiveId());

		return IncentiveMapper.toDTO(saved);
	}

	@Override
	public List<IncentiveResponseDTO> getByBeneficiary(@PathVariable Long beneficiaryId) {
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
	public IncentiveResponseDTO getByApplication(Long applicationId) {

		return incentiveRepo.findByApplicationId(applicationId).map(IncentiveMapper::toDTO)
				.orElseThrow(() -> new RuntimeException("No incentive found for applicationId: " + applicationId));
	}

}