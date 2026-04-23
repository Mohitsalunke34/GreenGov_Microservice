package com.cognizant.greengov.controller.subsidy_controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.greengov.dto.disburse_incentive_dto.IncentiveCreateRequestDTO;
import com.cognizant.greengov.dto.disburse_incentive_dto.IncentiveResponseDTO;
import com.cognizant.greengov.service.disburse_incentive_service.IncentiveService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller to handle all operations related to Government Payouts (Incentives).
 * Manages creation, retrieval, and deletion of subsidy records.
 */
@Slf4j
@RestController
@RequestMapping("/api/incentives")
public class IncentiveController {

    private final IncentiveService incentiveService;

    public IncentiveController(IncentiveService incentiveService) {
        this.incentiveService = incentiveService;
    }

    /**
     * POST: Creates a new incentive record after a subsidy application is approved.
     * * @param officerUserId The ID of the officer authorizing the payout.
     * @param dto The details of the incentive (Amount, Application ID, etc).
     * @return The created incentive details with HTTP 201 status.
     */
    @PostMapping
    public ResponseEntity<IncentiveResponseDTO> createIncentive(
            @RequestParam Long officerUserId, 
            @RequestBody @Valid IncentiveCreateRequestDTO dto) {

        log.info("Received request to create incentive for application ID: {} by officer ID: {}", 
                 dto.getApplicationId(), officerUserId);
        
        IncentiveResponseDTO response = incentiveService.createIncentive(dto, officerUserId);
        
        log.info("Successfully created incentive with ID: {}", response.getIncentiveId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET: Retrieves incentive details linked to a specific subsidy application.
     */
    @GetMapping("/by-application/{applicationId}")
    public ResponseEntity<IncentiveResponseDTO> getByApplication(@PathVariable Long applicationId) {
        log.debug("Fetching incentive details for application ID: {}", applicationId);
        return ResponseEntity.ok(incentiveService.getByApplication(applicationId));
    }

    /**
     * GET: Retrieves all incentives awarded to a specific beneficiary (Citizen/Business).
     */
    @GetMapping("/by-beneficiary/{participantId}")
    public ResponseEntity<List<IncentiveResponseDTO>> getByBeneficiary(@PathVariable Long participantId) {
        log.debug("Fetching all incentives for beneficiary ID: {}", participantId);
        return ResponseEntity.ok(incentiveService.getByBeneficiary(participantId));
    }

    /**
     * GET: Retrieves the master list of all incentives in the GreenGov system.
     */
    @GetMapping("/fetchAll")
    public ResponseEntity<List<IncentiveResponseDTO>> getByIncentive() {
        log.info("Request received to fetch all incentive records.");
        List<IncentiveResponseDTO> list = incentiveService.getAllIncentives();
        log.info("Total incentive records retrieved: {}", list.size());
        return ResponseEntity.ok(list);
    }

    /**
     * GET: Retrieves a single incentive record by its primary ID.
     */
    @GetMapping("/fetchById/{incentiveId}")
    public ResponseEntity<IncentiveResponseDTO> getByIncentiveId(@PathVariable Long incentiveId) {
        log.debug("Attempting to fetch incentive record for ID: {}", incentiveId);
        return ResponseEntity.ok(incentiveService.getByIncentiveId(incentiveId));
    }

    /**
     * DELETE: Removes an incentive record from the system.
     * * @param incentiveId The ID of the record to delete.
     * @return The details of the deleted incentive as confirmation.
     */
    @DeleteMapping("/deleteById/{incentiveId}")
    public ResponseEntity<IncentiveResponseDTO> deleteByIncentiveId(@PathVariable Long incentiveId) {
        log.warn("Received request to DELETE incentive ID: {}", incentiveId);
        IncentiveResponseDTO deletedRecord = incentiveService.deleteIncentive(incentiveId);
        log.info("Successfully deleted incentive ID: {}", incentiveId);
        return ResponseEntity.ok(deletedRecord);
    }
}