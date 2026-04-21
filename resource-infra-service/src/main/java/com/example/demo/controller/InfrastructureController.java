package com.example.demo.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.InfrastructureCreateRequestDTO;
import com.example.demo.dto.InfrastructureResponseDTO;
import com.example.demo.dto.InfrastructureStatusDTO;
import com.example.demo.service.InfrastructureService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/infrastructure")
@RequiredArgsConstructor // Automatically creates constructor for the 'final' service field
public class InfrastructureController {

	private static final Logger logger = LoggerFactory.getLogger(InfrastructureController.class);
	private final InfrastructureService service;

	/**
	 * US: Allocate new infrastructure to a project. POST /api/infrastructure
	 */
	@PostMapping("/create")
	public ResponseEntity<InfrastructureResponseDTO> addInfra(@Valid @RequestBody InfrastructureCreateRequestDTO dto) {
		logger.info("REST request to add infrastructure: {} for Project ID: {}", dto.getType(), dto.getProjectId());
		InfrastructureResponseDTO response = service.addInfrastructure(dto);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	/**
	 * US: Update existing infrastructure details. PUT /api/infrastructure/{id}
	 */
	@PutMapping("/{id}")
	public ResponseEntity<InfrastructureResponseDTO> updateInfra(@PathVariable("id") long infraId,
			@Valid @RequestBody InfrastructureCreateRequestDTO dto) {
		logger.info("REST request to update infrastructure ID: {}", infraId);
		return ResponseEntity.ok(service.updateInfrastructure(infraId, dto));
	}

	/**
	 * US: Update operational status via DTO. PATCH /api/infrastructure/status
	 */
	@PatchMapping("/status")
	public ResponseEntity<InfrastructureResponseDTO> updateStatus(@Valid @RequestBody InfrastructureStatusDTO dto) {
		logger.info("REST request to update status for Infrastructure ID: {}", dto.getInfraId());
		return ResponseEntity.ok(service.updateStatus(dto));
	}

	/**
	 * US: Retrieve details for a single asset. GET /api/infrastructure/{id}
	 */
	@GetMapping("/{id}")
	public ResponseEntity<InfrastructureResponseDTO> getInfra(@PathVariable("id") long id) {
		logger.debug("Fetching Infrastructure ID: {}", id);
		return ResponseEntity.ok(service.getInfrastructure(id));
	}

	/**
	 * US: View global infrastructure inventory. GET /api/infrastructure
	 */
	@GetMapping("/get-all")
	public ResponseEntity<List<InfrastructureResponseDTO>> getAll() {
		logger.info("Fetching all infrastructure records.");
		return ResponseEntity.ok(service.getAllInfrastructure());
	}

	/**
	 * US: Remove infrastructure record. DELETE /api/infrastructure/{id}
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteInfra(@PathVariable("id") long infraId) {
		logger.warn("REST request to DELETE infrastructure ID: {}", infraId);
		service.deleteInfrastructure(infraId);
		return ResponseEntity.noContent().build();
	}
}