package com.cognizant.greengov.controller.subsidy_controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cognizant.greengov.dto.disburse_incentive_dto.DisbursementProcessResponse;
import com.cognizant.greengov.dto.disburse_incentive_dto.DisbursementResponseDTO;
import com.cognizant.greengov.service.disburse_incentive_service.DisbursementService;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller for managing the final stage of the subsidy lifecycle: Disbursements.
 * Handles the actual release of funds and auditing of payment records.
 */
@Slf4j
@RestController
@RequestMapping("/api/disbursements")
public class DisbursementController {

    private final DisbursementService service;

    public DisbursementController(DisbursementService service) {
        this.service = service;
    }

    /**
     * POST: Processes a new disbursement (payout).
     * * @param officerUserId The ID of the officer executing the disbursement.
     * @param incentiveId The specific incentive record being paid out.
     * @param amount The total amount to be disbursed.
     * @return A response object containing the status and details of the transaction.
     */
    @PostMapping("/disburse")
    public ResponseEntity<DisbursementProcessResponse> processDisbursement(
            @RequestParam Long officerUserId,
            @RequestParam Long incentiveId, 
            @RequestParam Double amount) {

        log.info("Disbursement request received. Incentive ID: {}, Amount: {}, Authorized by Officer: {}", 
                 incentiveId, amount, officerUserId);

        DisbursementProcessResponse response = service.disburse(incentiveId, amount, officerUserId);

        log.info("Disbursement processed successfully for Incentive ID: {}", 
                 incentiveId);

        return ResponseEntity.ok(response);
    }

    /**
     * GET: Fetches the audit history/details for disbursements.
     * * @param disbursementId The ID of the specific disbursement record.
     * @return A list of disbursement response DTOs.
     */
    @GetMapping("/fetchAll/{disbursementId}")
    public ResponseEntity<List<DisbursementResponseDTO>> getAllDisbursements(@PathVariable Long disbursementId) {
        
        log.debug("Fetching disbursement records for ID: {}", disbursementId);
        
        List<DisbursementResponseDTO> disbursements = service.getAllDisbursement(disbursementId);
        
        log.info("Retrieved {} disbursement records for ID: {}", disbursements.size(), disbursementId);
        
        return ResponseEntity.ok(disbursements);
    }
}