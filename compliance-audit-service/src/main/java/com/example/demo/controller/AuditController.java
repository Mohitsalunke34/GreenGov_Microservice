package com.example.demo.controller;

import java.time.LocalDateTime;
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

import com.example.demo.dto.ErrorResponseDTO;
import com.example.demo.dto.compliance_audit.AuditCreateRequestDTO;
import com.example.demo.dto.compliance_audit.AuditResponseDTO;
import com.example.demo.model.Enums.AuditStatus;
import com.example.demo.service.AuditService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/audits")
@RequiredArgsConstructor
public class AuditController {

	private final AuditService service;

	// Start audit
	@PostMapping
	public ResponseEntity<?> startAudit(@RequestParam Long auditorUserId,
			@RequestBody @Valid AuditCreateRequestDTO dto) {
		try {
			AuditResponseDTO response = service.startAudit(dto, auditorUserId);

			return ResponseEntity.status(HttpStatus.CREATED).body(response);

		} catch (IllegalArgumentException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ErrorResponseDTO(ex.getMessage(), LocalDateTime.now()));

		} catch (IllegalStateException ex) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(new ErrorResponseDTO(ex.getMessage(), LocalDateTime.now()));
		}

	}

	// Close audit
	@PostMapping("/{auditId}/close")
	public ResponseEntity<AuditResponseDTO> closeAudit(@PathVariable Long auditId, @RequestParam AuditStatus status,
			@RequestParam Long auditorUserId) {

		return ResponseEntity.ok(service.closeAudit(auditId, status, auditorUserId));
	}

	// Get audits by officer
	@GetMapping("/by-officer/{officerId}")
	public ResponseEntity<List<AuditResponseDTO>> getByOfficer(@PathVariable Long officerId) {

		return ResponseEntity.ok(service.getByOfficer(officerId));
	}

	// Get audits by compliance
	@GetMapping("/by-compliance/{complianceId}")
	public ResponseEntity<List<AuditResponseDTO>> getByCompliance(@PathVariable Long complianceId) {

		return ResponseEntity.ok(service.getByCompliance(complianceId));
	}

	// Get audits by status
	@GetMapping
	public ResponseEntity<List<AuditResponseDTO>> getByStatus(@RequestParam AuditStatus status) {

		return ResponseEntity.ok(service.getByStatus(status));
	}

	// Get All Audits
	@GetMapping("/all")
	public ResponseEntity<List<AuditResponseDTO>> getAllAudits() {
		return ResponseEntity.ok(service.getAllAudit());
	}
}