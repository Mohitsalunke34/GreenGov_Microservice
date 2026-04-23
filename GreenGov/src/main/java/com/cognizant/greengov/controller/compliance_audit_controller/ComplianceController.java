package com.cognizant.greengov.controller.compliance_audit_controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.greengov.dto.complianceauditdto.ComplianceRecordCreateRequestDTO;
import com.cognizant.greengov.dto.complianceauditdto.ComplianceRecordResponseDTO;
import com.cognizant.greengov.model.Enums.ComplianceSubjectType;
import com.cognizant.greengov.service.compliance_audit_service.ComplianceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/compliance")
public class ComplianceController {

	private final ComplianceService service;

	public ComplianceController(ComplianceService service) {
		this.service = service;
	}

	// create compliance
	@PostMapping
	public ResponseEntity<ComplianceRecordResponseDTO> createRecord(@RequestParam("officerUserId") Long officerUserId,
			@RequestBody @Valid ComplianceRecordCreateRequestDTO dto) {

		return ResponseEntity.status(HttpStatus.CREATED).body(service.recordCompliance(dto, officerUserId));

	}

	@GetMapping("/participant/{participantId}")
	public ResponseEntity<List<ComplianceRecordResponseDTO>> getComplianceByParticipantId(
			@PathVariable Long participantId) {
		return ResponseEntity.ok(service.getByParticipant(participantId));
	}

	@GetMapping("/subject")
	public ResponseEntity<List<ComplianceRecordResponseDTO>> getComplianceBySubjectTypeAndSubjectId(
			@RequestParam ComplianceSubjectType subjectType, @RequestParam Long subjectId) {
		return ResponseEntity.ok(service.getBySubjectTypeandSubjectId(subjectType, subjectId));

	}
}
