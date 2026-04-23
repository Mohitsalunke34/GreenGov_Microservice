package com.cognizant.greengov.controller.admincontroller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.greengov.dto.profile_dto.OfficerProfileDTO;
import com.cognizant.greengov.modelmapper.OfficerProfileMapper;
import com.cognizant.greengov.service.admin_service.OfficerApprovalService;

@RestController
@RequestMapping("/api/admin/officers")
public class OfficerApprovalController {

	private final OfficerApprovalService service;

	public OfficerApprovalController(OfficerApprovalService service) {
		this.service = service;
	}

	@GetMapping("/pending")
	public List<OfficerProfileDTO> getPending() {
		return service.getPendingOfficers().stream().map(OfficerProfileMapper::toDTO).toList();
	}

	@PostMapping("/{id}/approve")
	public ResponseEntity<Void> approve(@PathVariable Long id) {
		service.approveOfficer(id);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{id}/reject")
	public ResponseEntity<Void> reject(@PathVariable Long id) {
		service.rejectOfficer(id);
		return ResponseEntity.ok().build();
	}
}