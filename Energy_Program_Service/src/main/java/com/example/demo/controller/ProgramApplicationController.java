package com.example.demo.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ProgramApplicationRequestDto;
import com.example.demo.dto.ProgramApplicationResponseDto;
import com.example.demo.service.ProgramApplicationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/applications")
@Slf4j
@RequiredArgsConstructor
public class ProgramApplicationController {

	private final ProgramApplicationService service;

	/* ================= APPLY ================= */

	@PostMapping
	public ResponseEntity<ProgramApplicationResponseDto> apply(
			@Valid @RequestBody ProgramApplicationRequestDto request) {

		log.info("REST request to apply for Program {} by Applicant {}", request.getProgramId(),
				request.getApplicantId());

		ProgramApplicationResponseDto response = service.apply(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/* ================= READ ================= */

	@GetMapping
	public ResponseEntity<List<ProgramApplicationResponseDto>> getAllApplications() {

		log.debug("REST request to fetch all program applications");
		return ResponseEntity.ok(service.getAllApplications());
	}

	@GetMapping("/fetchById/{id}")
	public ResponseEntity<ProgramApplicationResponseDto> getApplicationById(@PathVariable Long applicationId) {

		log.debug("REST request to fetch application ID {}", applicationId);
		return ResponseEntity.ok(service.getApplicationById(applicationId));
	}

	/* ================= REVIEW ================= */

	@PatchMapping("/{applicationId}/approve")
	public ResponseEntity<ProgramApplicationResponseDto> approve(@PathVariable Long applicationId) {

		log.info("REST request to APPROVE application ID {}", applicationId);
		return ResponseEntity.ok(service.approveApplication(applicationId));
	}

	@PatchMapping("/{applicationId}/reject")
	public ResponseEntity<ProgramApplicationResponseDto> reject(@PathVariable Long applicationId) {

		log.info("REST request to REJECT application ID {}", applicationId);
		return ResponseEntity.ok(service.rejectApplication(applicationId));
	}
}