package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ProgramApplicationRequestDto;
import com.example.demo.dto.ProgramApplicationResponseDto;
import com.example.demo.dto.client_dto.ApprovedApplicationLookupDTO;
import com.example.demo.service.ProgramApplicationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/applications")
@Slf4j
@RequiredArgsConstructor
public class ProgramApplicationController {

	private final ProgramApplicationService service;

	/* ================= APPLY ================= */

	@PostMapping("/apply")
	public ResponseEntity<ProgramApplicationResponseDto> apply(
			@Valid @RequestBody ProgramApplicationRequestDto request) {

		log.info("REST request to apply for Program {} by Applicant {}", request.getProgramId(),
				request.getApplicantId());

		ProgramApplicationResponseDto response = service.apply(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/* ================= READ ================= */

	@GetMapping("/fetchAll")
	public ResponseEntity<List<ProgramApplicationResponseDto>> getAllApplications() {

		log.debug("REST request to fetch all program applications");
		return ResponseEntity.ok(service.getAllApplications());
	}

	@GetMapping("/fetchById/{id}")
	public ResponseEntity<ProgramApplicationResponseDto> getApplicationById(@PathVariable("id") Long applicationId) {

		log.debug("REST request to fetch application ID {}", applicationId);
		return ResponseEntity.ok(service.getApplicationById(applicationId));
	}

	/* ================= REVIEW ================= */

	@PatchMapping("/updateApplicationStatus/{applicationId}/approve")
	public ResponseEntity<ProgramApplicationResponseDto> approve(@PathVariable Long applicationId) {

		log.info("REST request to APPROVE application ID {}", applicationId);
		return ResponseEntity.ok(service.approveApplication(applicationId));
	}

	@PatchMapping("/updateApplicationStatus/{applicationId}/reject")
	public ResponseEntity<ProgramApplicationResponseDto> reject(@PathVariable Long applicationId) {

		log.info("REST request to REJECT application ID {}", applicationId);
		return ResponseEntity.ok(service.rejectApplication(applicationId));
	}

	@GetMapping("/approved/by-participant/{participantId}")
	public ResponseEntity<List<ApprovedApplicationLookupDTO>> getApprovedApplicationsByParticipant(
			@PathVariable Long participantId) {

		return ResponseEntity.ok(service.getApprovedApplicationsByParticipant(participantId));
	}

}