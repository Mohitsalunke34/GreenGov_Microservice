package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.AdminOfficerDTO;
import com.example.demo.dto.client.OfficerDTO;
import com.example.demo.repository.OfficerProfileRepo;
import com.example.demo.service.OfficerApprovalService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/officers")
@Slf4j
public class OfficerApprovalController {

	private final OfficerApprovalService service;
	private final OfficerProfileRepo officerRepo;

	public OfficerApprovalController(OfficerApprovalService service, OfficerProfileRepo officerRepo) {
		this.service = service;
		this.officerRepo = officerRepo;
	}

	// GET ALL OFFICERS

	@GetMapping
	public List<AdminOfficerDTO> getAllOfficers() {
		return service.getAllOfficers();
	}

	// GET OFFICER BY ID

	@GetMapping("/{id}")
	public AdminOfficerDTO getOfficerById(@PathVariable Long id) {
		return service.getOfficerById(id);
	}

	// GET PENDING OFFICERS

	@GetMapping("/status/pending")
	public List<AdminOfficerDTO> pending() {
		return service.pendingOfficers();
	}

	// APPROVE OFFICER

	@PostMapping("/{id}/approve")
	public ResponseEntity<?> approve(@PathVariable Long id) {
		service.approveOfficer(id);
		return ResponseEntity.ok(Map.of("message", "Officer approved successfully"));
	}

	// REJECT OFFICER (MISSING EARLIER)

	@PostMapping("/{id}/reject")
	public ResponseEntity<?> reject(@PathVariable Long id) {
		service.rejectOfficer(id);
		return ResponseEntity.ok(Map.of("message", "Officer rejected successfully"));
	}

	// Client get active disbursement officer

	@GetMapping("/disbursement/active")
	public List<OfficerDTO> getActiveDisbursementOfficers() {

		return service.getActiveDisbursementOfficers().stream().map(profile -> {
			OfficerDTO dto = new OfficerDTO();
			dto.setUserId(profile.getUser().getId());
			dto.setUsername(profile.getUser().getUsername());
			dto.setOfficerType(profile.getOfficerType().name());
			dto.setDepartment(profile.getDepartment());
			dto.setDesignation(profile.getDesignation());
			return dto;
		}).toList();
	}
}
