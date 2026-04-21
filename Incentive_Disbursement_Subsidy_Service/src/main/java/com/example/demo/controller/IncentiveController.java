package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.IncentiveCreateRequestDTO;
import com.example.demo.dto.IncentiveResponseDTO;
import com.example.demo.service.IncentiveService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Incentive Controller (MICROSERVICE) Handles incentive creation, retrieval and
 * deletion.
 *
 * NOTE: - No entity access - No budget logic - No program/application logic
 */
@Slf4j
@RestController
@RequestMapping("/api/incentives")
@RequiredArgsConstructor
public class IncentiveController {

	private final IncentiveService incentiveService;

	/**
	 * CREATE INCENTIVE Officer ID comes from API Gateway / Auth service
	 */
	@PostMapping("/create")
	public ResponseEntity<IncentiveResponseDTO> createIncentive(@RequestHeader("X-Officer-User-Id") Long officerUserId,
			@RequestBody @Valid IncentiveCreateRequestDTO dto) {

		log.info("Microservice request → Create Incentive | ApplicationId={} | OfficerId={}", dto.getApplicationId(),
				officerUserId);

		IncentiveResponseDTO response = incentiveService.createIncentive(dto, officerUserId);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/**
	 * FETCH INCENTIVE BY APPLICATION ID One application → one incentive
	 */
	@GetMapping("/application/{applicationId}")
	public ResponseEntity<IncentiveResponseDTO> getByApplication(@PathVariable Long applicationId) {

		return ResponseEntity.ok(incentiveService.getByApplication(applicationId));
	}

	/**
	 * FETCH ALL INCENTIVES FOR A BENEFICIARY
	 */
	@GetMapping("/beneficiary/{beneficiaryId}")
	public ResponseEntity<List<IncentiveResponseDTO>> getByBeneficiary(@PathVariable Long beneficiaryId) {

		return ResponseEntity.ok(incentiveService.getByBeneficiary(beneficiaryId));
	}

	/**
	 * FETCH INCENTIVE BY ID
	 */
	@GetMapping("/fetchById/{incentiveId}")
	public ResponseEntity<IncentiveResponseDTO> getByIncentiveId(@PathVariable Long incentiveId) {

		return ResponseEntity.ok(incentiveService.getByIncentiveId(incentiveId));
	}

	/**
	 * FETCH ALL INCENTIVES (ADMIN / AUDIT)
	 */
	@GetMapping("/fetchAllIncentives")
	public ResponseEntity<List<IncentiveResponseDTO>> getAllIncentives() {

		return ResponseEntity.ok(incentiveService.getAllIncentives());
	}

	/**
	 * DELETE INCENTIVE IMPORTANT: - Does NOT refund budget (business decision)
	 */
	@DeleteMapping("/deleteById/{incentiveId}")
	public ResponseEntity<IncentiveResponseDTO> deleteIncentive(@PathVariable Long incentiveId) {

		log.warn("Microservice request → Delete Incentive | IncentiveId={}", incentiveId);

		IncentiveResponseDTO deleted = incentiveService.deleteIncentive(incentiveId);

		return ResponseEntity.ok(deleted);
	}

	/**
	 * Reporting & Analytics endpoint Used by Reports microservice
	 */
	@GetMapping("/fetch/report-metrics")
	public Map<String, Object> getIncentiveReportMetrics() {
		return incentiveService.getIncentiveReportMetrics();
	}

	/**
	 * EXISTS CHECK Used by Compliance microservice via Feign
	 */
	@GetMapping("/{id}/exists")
	public ResponseEntity<Boolean> incentiveExists(@PathVariable Long id) {

		return ResponseEntity.ok(incentiveService.incentiveExists(id));
	}

}