package com.example.demo.controller;

import java.util.HashMap;
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

import com.example.demo.client.ParticipantClient;
import com.example.demo.dto.IncentiveCreateRequestDTO;
import com.example.demo.dto.IncentiveResponseDTO;
import com.example.demo.dto.ParticipantBasicDTO;
import com.example.demo.dto.client_dto.SubjectLookupDTO;
import com.example.demo.repo.IncentiveRepository;
import com.example.demo.service.IncentiveService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/incentives")
@RequiredArgsConstructor
public class IncentiveController {

	private final IncentiveService incentiveService;
	private final IncentiveRepository incentiveRepo;
	private final ParticipantClient participantClient;

	/* ================= CREATE ================= */

	@PostMapping("/create")
	public ResponseEntity<IncentiveResponseDTO> createIncentive(@RequestHeader("X-Officer-User-Id") Long officerUserId,
			@RequestBody @Valid IncentiveCreateRequestDTO dto) {

		log.info("Create Incentive | ParticipantId={} | OfficerId={}", dto.getParticipantId(), officerUserId);

		IncentiveResponseDTO response = incentiveService.createIncentive(dto, officerUserId);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/* ================= READ ================= */

	@GetMapping("/beneficiary/{beneficiaryId}")
	public ResponseEntity<List<IncentiveResponseDTO>> getByBeneficiary(@PathVariable Long beneficiaryId) {

		return ResponseEntity.ok(incentiveService.getByBeneficiary(beneficiaryId));
	}

	@GetMapping("/fetchById/{incentiveId}")
	public ResponseEntity<IncentiveResponseDTO> getByIncentiveId(@PathVariable Long incentiveId) {

		return ResponseEntity.ok(incentiveService.getByIncentiveId(incentiveId));
	}

	@GetMapping("/fetchAllIncentives")
	public ResponseEntity<List<IncentiveResponseDTO>> getAllIncentives() {

		return ResponseEntity.ok(incentiveService.getAllIncentives());
	}

	/* ================= DELETE ================= */

	@DeleteMapping("/deleteById/{incentiveId}")
	public ResponseEntity<IncentiveResponseDTO> deleteIncentive(@PathVariable Long incentiveId) {

		IncentiveResponseDTO deleted = incentiveService.deleteIncentive(incentiveId);

		return ResponseEntity.ok(deleted);
	}

	/* ================= INTERNAL ================= */

	@GetMapping("/{id}/exists")
	public ResponseEntity<Boolean> incentiveExists(@PathVariable Long id) {
		return ResponseEntity.ok(incentiveService.incentiveExists(id));
	}

	@GetMapping("/report-metrics")
	public Map<String, Object> getIncentiveReportMetrics() {

		Long totalIncentives = incentiveRepo.count();
		Long approvedIncentives = incentiveRepo
				.countByStatusIn(List.of("APPROVED", "PARTIALLY_DISBURSED", "COMPLETED"));

		Double totalAmount = incentiveRepo.sumTotalAmount();
		Double disbursedAmount = incentiveRepo.sumDisbursedAmount();

		Map<String, Object> response = new HashMap<>();
		response.put("totalIncentives", totalIncentives.intValue());
		response.put("approvedIncentives", approvedIncentives.intValue());
		response.put("totalAmount", totalAmount);
		response.put("disbursedAmount", disbursedAmount);

		return response;
	}

	// Used by Compliance Service
	@GetMapping("/subjects")
	public ResponseEntity<List<SubjectLookupDTO>> getIncentiveSubjects() {

		return ResponseEntity.ok(incentiveRepo.findAll().stream()
				.map(i -> new SubjectLookupDTO(i.getIncentiveId(), "Incentive #" + i.getIncentiveId())).toList());
	}
	
	/**
	 * PARTICIPANT LOOKUP
	 * Used by Incentive UI to select participant by legal name
	 */
	@GetMapping("/participants/lookup")
	public ResponseEntity<List<ParticipantBasicDTO>> getParticipantsLookup() {
	    return ResponseEntity.ok(participantClient.getParticipants());
	}
}