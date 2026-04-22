package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.compliance_audit.ComplianceRecordCreateRequestDTO;
import com.example.demo.dto.compliance_audit.ComplianceResponseDTO;
import com.example.demo.model.Enums.ComplianceResult;
import com.example.demo.model.Enums.ComplianceSubjectType;
import com.example.demo.repo.ComplianceRecordRepository;
import com.example.demo.service.ComplianceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/compliance")
@RequiredArgsConstructor
public class ComplianceController {

	private final ComplianceService service;

	private final ComplianceRecordRepository complianceRepo;

	// ✅ Create compliance record
	@PostMapping
	public ResponseEntity<ComplianceResponseDTO> createRecord(@RequestParam Long officerUserId,
			@RequestBody @Valid ComplianceRecordCreateRequestDTO dto) {

		return ResponseEntity.status(HttpStatus.CREATED).body(service.recordCompliance(dto, officerUserId));
	}

	// ✅ Get compliance by participant
	@GetMapping("/participant/{participantId}")
	public ResponseEntity<List<ComplianceResponseDTO>> getByParticipant(@PathVariable Long participantId) {

		return ResponseEntity.ok(service.getByParticipant(participantId));
	}

	// ✅ Get compliance by subject
	@GetMapping("/subject")
	public ResponseEntity<List<ComplianceResponseDTO>> getBySubject(@RequestParam ComplianceSubjectType subjectType,
			@RequestParam Long subjectId) {

		return ResponseEntity.ok(service.getBySubject(subjectType, subjectId));
	}

	@GetMapping("/report-metrics")
	public Map<String, Object> getComplianceReportMetrics() {

		long totalAudits = complianceRepo.count();

		long passed = complianceRepo.countByResult(ComplianceResult.PASS);

		long failed = complianceRepo.countByResult(ComplianceResult.FAIL);

		Map<String, Object> response = new HashMap<>();
		response.put("totalAudits", (int) totalAudits);
		response.put("compliant", (int) passed);
		response.put("nonCompliant", (int) failed);

		return response;
	}

}