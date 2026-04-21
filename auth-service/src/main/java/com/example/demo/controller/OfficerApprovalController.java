package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.client.OfficerDTO;
import com.example.demo.model.OfficerProfile;
import com.example.demo.service.OfficerApprovalService;

@RestController
@RequestMapping("/api/admin/officers")
public class OfficerApprovalController {

	private final OfficerApprovalService service;

	public OfficerApprovalController(OfficerApprovalService service) {
		this.service = service;
	}

	@GetMapping("/pending")
	public List<OfficerProfile> pending() {
		return service.pendingOfficers();
	}

	@PostMapping("/{id}/approve")
	public void approve(@PathVariable Long id) {
		service.approveOfficer(id);
	}

	@GetMapping("/disbursement/active")
	public ResponseEntity<List<OfficerDTO>> getActiveDisbursementOfficers(
			@RequestHeader("X-Officer-User-Id") Long adminUserId) {

		List<OfficerProfile> profiles = service.getActiveDisbursementOfficers();

		List<OfficerDTO> result = profiles.stream().map(profile -> {
			OfficerDTO dto = new OfficerDTO();
			dto.setUserId(profile.getUser().getId());
			dto.setUsername(profile.getUser().getUsername());
			dto.setOfficerType(profile.getOfficerType().name());
			dto.setDepartment(profile.getDepartment());
			dto.setDesignation(profile.getDesignation());
			return dto;
		}).toList();

		return ResponseEntity.ok(result);
	}
}