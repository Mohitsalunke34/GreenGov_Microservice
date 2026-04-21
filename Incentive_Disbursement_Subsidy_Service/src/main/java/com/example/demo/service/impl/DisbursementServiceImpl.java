package com.example.demo.service.impl;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.client.ProgramClient;
import com.example.demo.dto.BudgetSummaryDTO;
import com.example.demo.dto.DisbursementProcessResponse;
import com.example.demo.dto.DisbursementResponseDTO;
import com.example.demo.dto.ProgramDTO;
import com.example.demo.exception.InvalidDisbursementException;
import com.example.demo.exception.InvalidIncentiveException;
import com.example.demo.model.Disbursement;
import com.example.demo.model.Incentive;
import com.example.demo.modelMapper.DisbursementMapper;
import com.example.demo.repo.DisbursementRepository;
import com.example.demo.repo.IncentiveRepository;
import com.example.demo.service.DisbursementService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class DisbursementServiceImpl implements DisbursementService {

    private final DisbursementRepository disbursementRepo;
    private final IncentiveRepository incentiveRepo;
    private final ProgramClient programClient;

    // ============================
    // DISBURSE INCENTIVE
    // ============================
    @Override
    @Transactional
    public DisbursementProcessResponse disburse(
            Long incentiveId,
            Double amount,
            Long officerUserId) {

        log.info("Processing disbursement | IncentiveId={} | Amount={}", incentiveId, amount);

        Incentive incentive = incentiveRepo.findById(incentiveId)
                .orElseThrow(() ->
                        new InvalidIncentiveException("Incentive not found"));

        if (!"APPROVED".equals(incentive.getStatus())
                && !"PARTIALLY_DISBURSED".equals(incentive.getStatus())) {
            throw new InvalidDisbursementException(
                    "Incentive is not eligible for disbursement");
        }

        if (amount > incentive.getRemainingAmount()) {
            throw new InvalidDisbursementException(
                    "Requested amount exceeds remaining incentive balance");
        }

        // 1️⃣ Update remaining incentive
        double remaining = incentive.getRemainingAmount() - amount;
        incentive.setRemainingAmount(remaining);

        if (remaining == 0) {
            incentive.setStatus("COMPLETED");
        } else {
            incentive.setStatus("PARTIALLY_DISBURSED");
        }

        // 2️⃣ Create Disbursement
        Disbursement disbursement = new Disbursement();
        disbursement.setIncentive(incentive);
        disbursement.setOfficerUserId(officerUserId);
        disbursement.setAmount(amount);
        disbursement.setPaymentDate(LocalDate.now());
        disbursement.setStatus("SUCCESS");

        incentiveRepo.save(incentive);
        Disbursement saved = disbursementRepo.save(disbursement);

        // 3️⃣ History
        List<DisbursementResponseDTO> history =
                disbursementRepo.findByIncentive(incentive)
                        .stream()
                        .map(DisbursementMapper::toDTO)
                        .toList();

        // 4️⃣ Fetch program AFTER deduction (read-only)
        ProgramDTO program =
                programClient.getProgramById(incentive.getProgramId());

        // 5️⃣ Budget summary ✅ (FULLY POPULATED)
        BudgetSummaryDTO summary = new BudgetSummaryDTO();
        summary.setProgramId(incentive.getProgramId());
        summary.setBaseBudget(program.getBudget());
        summary.setRemainingProgramBudget(program.getRemainingProgramBudget());
        summary.setTotalDisbursedSoFar(BigDecimal.valueOf(amount)); // THIS PAYMENT
        summary.setRemainingIncentive(BigDecimal.valueOf(remaining));

        return new DisbursementProcessResponse(
                DisbursementMapper.toDTO(saved),
                summary,
                history
        );
    }

    // ============================
    // FETCH ALL DISBURSEMENTS
    // ============================
    @Override
    public List<DisbursementResponseDTO> getAllDisbursement(Long incentiveId) {

        Incentive incentive = incentiveRepo.findById(incentiveId)
                .orElseThrow(() ->
                        new InvalidIncentiveException("Incentive not found"));

        List<Disbursement> list = disbursementRepo.findByIncentive(incentive);

        if (list.isEmpty()) {
            throw new InvalidDisbursementException("No disbursement history found");
        }

        return list.stream()
                .map(DisbursementMapper::toDTO)
                .toList();
    }

    // ============================
    // FETCH BY DISBURSEMENT ID
    // ============================
    @Override
    public DisbursementResponseDTO getByDisbursementId(
            Long incentiveId,
            Long disbursementId) {

        Disbursement disbursement = disbursementRepo.findById(disbursementId)
                .orElseThrow(() ->
                        new InvalidDisbursementException("Disbursement not found"));

        if (!disbursement.getIncentive().getIncentiveId().equals(incentiveId)) {
            throw new InvalidDisbursementException(
                    "Disbursement does not belong to given incentive");
        }

        return DisbursementMapper.toDTO(disbursement);
    }

	
}
