package com.cognizant.greengov.service.disburse_incentive_service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cognizant.greengov.dto.disburse_incentive_dto.BudgetSummaryDTO;
import com.cognizant.greengov.dto.disburse_incentive_dto.DisbursementProcessResponse;
import com.cognizant.greengov.dto.disburse_incentive_dto.DisbursementResponseDTO;
import com.cognizant.greengov.exception.InvalidDisbursementException;
import com.cognizant.greengov.exception.InvalidIncentiveException;
import com.cognizant.greengov.model.incentive_subsidy.Disbursement;
import com.cognizant.greengov.model.incentive_subsidy.Incentive;
import com.cognizant.greengov.model.register_login.UserAccount;
import com.cognizant.greengov.model.sustainability_renewable_proj.EnergyProgram;
import com.cognizant.greengov.modelmapper.DisbursementMapper;
import com.cognizant.greengov.repository.disbursement_incentive_repo.DisbursementRepository;
import com.cognizant.greengov.repository.disbursement_incentive_repo.IncentiveRepository;
import com.cognizant.greengov.repository.register_login_repo.UserAccountRepository;
import com.cognizant.greengov.repository.sustainability_repo.EnergyProgramRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service implementation for processing Payouts (Disbursements).
 * Manages partial and full payments, balance tracking, and transaction history.
 */
@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class DisbursementServiceImpl implements DisbursementService {

	private final DisbursementRepository disbursementRepo;
	private final IncentiveRepository incentiveRepo;
	private final UserAccountRepository userRepo;
	private final EnergyProgramRepository energyProgramRepo;

	/**
	 * Executes a disbursement against an approved incentive.
	 * Updates the remaining balance and determines if the incentive is partially or fully paid.
	 * * @param incentiveId The ID of the sanctioned incentive.
	 * @param amount The specific amount to release in this transaction.
	 * @param officerUserId The officer authorizing the release of funds.
	 * @return A comprehensive response including the new receipt, budget summary, and payment history.
	 */
	@Override
	@Transactional
	public DisbursementProcessResponse disburse(Long incentiveId, Double amount, Long officerUserId) {
		log.info("Processing disbursement for Incentive ID: {}. Amount requested: {}", incentiveId, amount);

		Incentive incentive = incentiveRepo.findById(incentiveId)
				.orElseThrow(() -> {
					log.error("Disbursement failed: Incentive ID {} not found.", incentiveId);
					return new InvalidIncentiveException("Incentive not found");
				});

		EnergyProgram program = incentive.getProgram();
		UserAccount officer = userRepo.findById(officerUserId)
				.orElseThrow(() -> new InvalidIncentiveException("Officer not found"));

		// --- BALANCE VALIDATION ---
		log.debug("Checking remaining balance for Incentive ID: {}. Current remaining: {}", 
				incentiveId, incentive.getRemainingAmount());

		if (amount > incentive.getRemainingAmount()) {
			log.warn("Over-payment blocked! Incentive {} has {}, but {} was requested.", 
					incentiveId, incentive.getRemainingAmount(), amount);
			throw new InvalidDisbursementException(
					"Requested amount exceeds remaining incentive balance: " + incentive.getRemainingAmount());
		}

		// Update Individual Remaining Balance
		double updatedRemaining = incentive.getRemainingAmount() - amount;
		incentive.setRemainingAmount(updatedRemaining);

		// Logical Status Transition
		if (updatedRemaining == 0) {
			incentive.setStatus("COMPLETED");
			log.info("Incentive ID: {} is now fully disbursed (Status: COMPLETED).", incentiveId);
		} else {
			incentive.setStatus("PARTIALLY_DISBURSED");
			log.info("Incentive ID: {} updated to PARTIALLY_DISBURSED. Remaining: {}", incentiveId, updatedRemaining);
		}

		// Record the Transaction History
		Disbursement d = new Disbursement();
		d.setIncentive(incentive);
		d.setOfficer(officer);
		d.setAmount(amount);
		d.setPaymentDate(LocalDate.now());
		d.setStatus("SUCCESS");

		incentiveRepo.save(incentive);
		Disbursement savedDisbursement = disbursementRepo.save(d);

		// Prepare Budget Summary for Response
		BudgetSummaryDTO summary = new BudgetSummaryDTO();
		summary.setProgramId(program.getProgramId());
		summary.setBaseBudget(program.getBudget());
		summary.setTotalDisbursedSoFar(BigDecimal.valueOf(amount));
		summary.setRemainingIncentive(BigDecimal.valueOf(updatedRemaining));
		summary.setRemainingProgramBudget(program.getRemainingProgramBudget());

		List<DisbursementResponseDTO> history = disbursementRepo.findByIncentive(incentive).stream()
				.map(DisbursementMapper::toDTO).toList();

		log.info("Disbursement transaction {} completed successfully.", savedDisbursement.getDisbursementId());
		return new DisbursementProcessResponse(DisbursementMapper.toDTO(savedDisbursement), summary, history);
	}

	/**
	 * Retrieves the full payment history for a specific incentive record.
	 */
	public List<DisbursementResponseDTO> getAllDisbursement(Long incentiveId) {
		log.debug("Fetching all disbursement history for Incentive ID: {}", incentiveId);
		
		Incentive incentive = incentiveRepo.findById(incentiveId)
				.orElseThrow(() -> new IllegalArgumentException("Incentive not found"));

		List<Disbursement> disburse = disbursementRepo.findByIncentive(incentive);

		if (disburse.isEmpty()) {
			log.warn("No payment records found for Incentive ID: {}", incentiveId);
			throw new InvalidDisbursementException("No disbursement history found for this incentive");
		}

		return disburse.stream().map(d -> {
			DisbursementResponseDTO dto = new DisbursementResponseDTO();
			dto.setDisbursementId(d.getDisbursementId());
			dto.setAmount(d.getAmount());
			dto.setStatus(d.getStatus());
			dto.setIncentiveId(d.getIncentive().getIncentiveId());
			dto.setOfficerUserId(d.getOfficer().getId());
			dto.setPaymentDate(d.getPaymentDate());
			return dto;
		}).toList();
	}

	/**
	 * Retrieves a specific disbursement record and validates that it belongs to the given incentive.
	 */
	public DisbursementResponseDTO getByDisbursementId(Long incentiveId, Long disbursementId) {
		log.debug("Fetching specific Disbursement record: {} for Incentive: {}", disbursementId, incentiveId);
		
		Incentive incentive = incentiveRepo.findByIncentiveId(incentiveId)
				.orElseThrow(() -> new InvalidIncentiveException("No incentive found with this id: " + incentiveId));

		Disbursement dto = disbursementRepo.findById(disbursementId).orElseThrow(
				() -> new InvalidDisbursementException("Disbursement not found for Id: " + disbursementId));

		// Security/Audit Check: Ensure the record belongs to the claimed incentive
		if (!dto.getIncentive().getIncentiveId().equals(incentiveId)) {
			log.error("Mismatch detected! Disbursement {} does not belong to Incentive {}.", disbursementId, incentiveId);
			throw new InvalidDisbursementException(
					"Disbursement ID " + disbursementId + " does not belong to Incentive ID " + incentiveId);
		}

		return DisbursementMapper.toDTO(dto);
	}
}