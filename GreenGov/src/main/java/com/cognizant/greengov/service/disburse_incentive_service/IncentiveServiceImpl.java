package com.cognizant.greengov.service.disburse_incentive_service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cognizant.greengov.dto.disburse_incentive_dto.IncentiveCreateRequestDTO;
import com.cognizant.greengov.dto.disburse_incentive_dto.IncentiveResponseDTO;
import com.cognizant.greengov.exception.ResourceNotFoundException;
import com.cognizant.greengov.model.incentive_subsidy.Incentive;
import com.cognizant.greengov.model.register_login.ParticipantProfile;
import com.cognizant.greengov.model.register_login.UserAccount;
import com.cognizant.greengov.model.sustainability_renewable_proj.EnergyProgram;
import com.cognizant.greengov.model.sustainability_renewable_proj.ProgramApplication;
import com.cognizant.greengov.modelmapper.IncentiveMapper;
import com.cognizant.greengov.repository.disbursement_incentive_repo.IncentiveRepository;
import com.cognizant.greengov.repository.register_login_repo.ParticipantProfileRepository;
import com.cognizant.greengov.repository.register_login_repo.UserAccountRepository;
import com.cognizant.greengov.repository.sustainability_repo.EnergyProgramRepository;
import com.cognizant.greengov.repository.sustainability_repo.ProgramApplicationRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service implementation for managing Incentives (Subsidy Payouts).
 * Handles budget validation, deduction, and lifecycle management of incentive records.
 */
@Slf4j
@Service
@AllArgsConstructor
public class IncentiveServiceImpl implements IncentiveService {

	private final IncentiveRepository incentiveRepo;
	private final ProgramApplicationRepository appRepo;
	private final UserAccountRepository userRepo;
	private final ParticipantProfileRepository participantRepo;
	private final EnergyProgramRepository energyProgramRepo;

	/**
	 * Creates a new incentive, validates program budget, and deducts the amount.
	 * * @param dto Data for incentive creation.
	 * @param officerUserId The ID of the officer approving the incentive.
	 * @return The created IncentiveResponseDTO.
	 * @throws RuntimeException if budget is insufficient.
	 */
	@Override
	@Transactional
	public IncentiveResponseDTO createIncentive(IncentiveCreateRequestDTO dto, Long officerUserId) {
	    log.info("Starting incentive creation for Application ID: {}", dto.getApplicationId());

	    // 1. Fetch the application
	    ProgramApplication app = appRepo.findById(dto.getApplicationId())
	            .orElseThrow(() -> {
	                log.error("Application ID {} not found", dto.getApplicationId());
	                return new IllegalArgumentException("Application not found");
	            });

	    // --- NEW STATUS VALIDATION START ---
	    // Only allow incentive creation if the application status is 'APPROVED' or 'VALIDATED'
	    // Adjust the string to match exactly what you store in your DB (e.g., "APPROVED")
	    if (!"APPROVED".equalsIgnoreCase(app.getStatus())) {
	        log.warn("Access Denied: Application {} is in {} status. Incentive can only be created for APPROVED applications.", 
	                 dto.getApplicationId(), app.getStatus());
	        throw new IllegalStateException("Cannot create incentive. Application status must be APPROVED (Current: " + app.getStatus() + ")");
	    }
	    // --- NEW STATUS VALIDATION END ---

	    // 2. Check for existing duplicate incentives
	    if (incentiveRepo.findByApplication(app).isPresent()) {
	        log.warn("Attempted to create duplicate incentive for Application ID: {}", dto.getApplicationId());
	        throw new IllegalStateException("Incentive already exists for this application");
	    }

	    EnergyProgram program = app.getProgram();
	    BigDecimal requestedAmountBD = BigDecimal.valueOf(dto.getAmount());

	    // --- BUDGET LOGIC ---
	    if (program.getRemainingProgramBudget() == null) {
	        program.setRemainingProgramBudget(program.getBudget());
	    }

	    if (requestedAmountBD.compareTo(program.getRemainingProgramBudget()) > 0) {
	        log.error("Insufficient budget for Program: {}. Required: {}, Available: {}", 
	                program.getTitle(), requestedAmountBD, program.getRemainingProgramBudget());
	        throw new RuntimeException("Insufficient Program Budget!");
	    }

	    // Deduct and Save Budget
	    program.setRemainingProgramBudget(program.getRemainingProgramBudget().subtract(requestedAmountBD));
	    energyProgramRepo.save(program);
	    log.info("Budget deducted. New remaining budget: {}", program.getRemainingProgramBudget());

	    UserAccount officer = userRepo.findById(officerUserId)
	            .orElseThrow(() -> new IllegalArgumentException("Officer not found"));

	    // 3. Create the Incentive record
	    Incentive incentive = new Incentive();
	    incentive.setApplication(app);
	    incentive.setProgram(program);
	    incentive.setBeneficiary(app.getApplicant());
	    incentive.setAmount(dto.getAmount());
	    incentive.setRemainingAmount(dto.getAmount());
	    incentive.setSanctionedDate(LocalDate.now());
	    incentive.setStatus("APPROVED"); // This is the status of the Incentive itself
	    incentive.setApprovedBy(officer);
	    incentive.setCreatedBy(officer.getUsername());
	    incentive.setUpdatedBy(officer.getUsername());

	    Incentive saved = incentiveRepo.save(incentive);
	    log.info("Successfully created Incentive ID: {} for Application ID: {}", saved.getIncentiveId(), app.getApplicationId());

	    return IncentiveMapper.toDTO(saved);
	}

	@Override
	public IncentiveResponseDTO getByApplication(Long applicationId) {
		log.debug("Fetching incentive for application: {}", applicationId);
		ProgramApplication app = appRepo.findById(applicationId)
				.orElseThrow(() -> new IllegalArgumentException("Application not found"));

		return incentiveRepo.findByApplication(app)
				.map(IncentiveMapper::toDTO)
				.orElseThrow(() -> new IllegalArgumentException("No incentive found"));
	}

	@Override
	public List<IncentiveResponseDTO> getByBeneficiary(Long participantId) {
		log.debug("Fetching incentives for beneficiary ID: {}", participantId);
		ParticipantProfile participant = participantRepo.findById(participantId)
				.orElseThrow(() -> new IllegalArgumentException("Participant not found"));

		return incentiveRepo.findByBeneficiary(participant).stream()
				.map(IncentiveMapper::toDTO)
				.toList();
	}

	@Override
	public IncentiveResponseDTO getByIncentiveId(Long incentiveId) {
		log.debug("Fetching incentive record: {}", incentiveId);
		return incentiveRepo.findByIncentiveId(incentiveId)
				.map(IncentiveMapper::toDTO)
				.orElseThrow(() -> new ResourceNotFoundException("Incentive not found"));
	}

	/**
	 * Deletes an incentive record from the system.
	 */
	@Override
	@Transactional
	public IncentiveResponseDTO deleteIncentive(Long incentiveId) {
		log.warn("Deleting Incentive record with ID: {}", incentiveId);
		Incentive incentive = incentiveRepo.findById(incentiveId)
				.orElseThrow(() -> new IllegalArgumentException("Incentive not found"));

		IncentiveResponseDTO response = IncentiveMapper.toDTO(incentive);
		incentiveRepo.delete(incentive);
		
		log.info("Successfully deleted Incentive ID: {}", incentiveId);
		return response;
	}

	/**
	 * Returns a list of all sanctioned incentives.
	 */
	@Override
	public List<IncentiveResponseDTO> getAllIncentives() {
		log.info("Fetching all incentive records from database.");
		return incentiveRepo.findAll().stream()
				.map(IncentiveMapper::toDTO)
				.toList();
	}
}