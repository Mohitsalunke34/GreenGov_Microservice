package com.example.demo.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.DisbursementCreateRequestDTO;
import com.example.demo.dto.DisbursementProcessResponse;
import com.example.demo.dto.DisbursementResponseDTO;
import com.example.demo.service.DisbursementService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * DISBURSEMENT CONTROLLER (MICROSERVICE)
 *
 * - No entity exposure
 * - No business logic
 * - Officer identity externalized
 */
@Slf4j
@RestController
@RequestMapping("/api/disbursements")
@RequiredArgsConstructor
public class DisbursementController {

    private final DisbursementService disbursementService;

    /**
     * POST: Execute a disbursement (partial or full).
     *
     * Officer ID is expected from API Gateway / Auth Service.
     */
    @PostMapping
    public ResponseEntity<DisbursementProcessResponse> disburseIncentive(
            @RequestHeader("X-Officer-User-Id") Long officerUserId,
            @RequestBody @Valid DisbursementCreateRequestDTO request
    ) {

        log.info(
                "Disbursement request | IncentiveId={} | Amount={} | OfficerId={}",
                request.getIncentiveId(),
                request.getAmount(),
                officerUserId
        );

        DisbursementProcessResponse response =
                disbursementService.disburse(
                        request.getIncentiveId(),
                        request.getAmount(),
                        officerUserId
                );

        return ResponseEntity.ok(response);
    }

    /**
     * GET: Fetch ALL disbursements for a given incentive.
     */
    @GetMapping("/by-incentive/{incentiveId}")
    public ResponseEntity<List<DisbursementResponseDTO>> getDisbursementHistory(
            @PathVariable Long incentiveId
    ) {

        log.debug("Fetching disbursement history | IncentiveId={}", incentiveId);

        List<DisbursementResponseDTO> list =
                disbursementService.getAllDisbursement(incentiveId);

        return ResponseEntity.ok(list);
    }

    /**
     * GET: Fetch a specific disbursement under an incentive.
     */
    @GetMapping("/fetchByIncentiveId&disbursementId/{incentiveId}/{disbursementId}")
    public ResponseEntity<DisbursementResponseDTO> getDisbursementById(
            @PathVariable Long incentiveId,
            @PathVariable Long disbursementId
    ) {

        log.debug(
                "Fetching disbursement | IncentiveId={} | DisbursementId={}",
                incentiveId,
                disbursementId
        );

        return ResponseEntity.ok(
                disbursementService.getByDisbursementId(incentiveId, disbursementId)
        );
    }
}