package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.client_dto.SubjectLookupDTO;
import com.example.demo.dto.compliance_audit.ComplianceLookupDTO;
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

	// Create compliance record
	@PostMapping
	public ResponseEntity<?> createRecord(@RequestParam Long officerUserId,
			@RequestBody @Valid ComplianceRecordCreateRequestDTO dto) {

		ComplianceResponseDTO newCompliance = service.recordCompliance(dto, officerUserId);
		return ResponseEntity.status(HttpStatus.CREATED).body(newCompliance);

	}

	// Get compliance by subject
	@GetMapping("/subject")
	public ResponseEntity<List<ComplianceResponseDTO>> getBySubject(@RequestParam ComplianceSubjectType subjectType,
			@RequestParam Long subjectId) {

		return ResponseEntity.ok(service.getBySubject(subjectType, subjectId));
	}

	// to get all the programs through client calls
	@GetMapping("/subjects/programs")
	public ResponseEntity<List<SubjectLookupDTO>> getProgramsForCompliance() {
		return ResponseEntity.ok(service.getProgramSubjects());
	}

	// to get all the projects through client calls
	@GetMapping("/subjects/projects")
	public ResponseEntity<List<SubjectLookupDTO>> getProjectsForCompliance() {
		return ResponseEntity.ok(service.getProjectSubjects());
	}

	// to get all the incentives through client calls
	@GetMapping("/subjects/incentives")
	public ResponseEntity<List<SubjectLookupDTO>> getIncentivesForCompliance() {
		return ResponseEntity.ok(service.getIncentiveSubjects());
	}
	
	
	// to get all the compliance record as needed
	@GetMapping("/lookup")
	public ResponseEntity<List<ComplianceLookupDTO>> getComplianceLookup() {

		return ResponseEntity.ok(complianceRepo.findAll().stream()
				.map(c -> new ComplianceLookupDTO(c.getId(),
						"Compliance #" + c.getId() + " | " + c.getSubjectType() + " (" + c.getSubjectId() + ")"))
				.toList());
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