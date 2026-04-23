package com.cognizant.greengov.controller.infra_resource_controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.cognizant.greengov.dto.infra_resource_dto.InfrastructureCreateRequestDTO;
import com.cognizant.greengov.dto.infra_resource_dto.InfrastructureResponseDTO;
import com.cognizant.greengov.dto.infra_resource_dto.InfrastructureStatusDTO;
import com.cognizant.greengov.service.infra_resource_service.InfrastructureService;

import jakarta.validation.Valid;

/**
 * REST Controller for managing Infrastructure assets (Solar Plants, Wind Farms,
 * etc.). Provides endpoints for Program Managers to track and update project
 * physical infrastructure.
 */
@RestController
@RequestMapping("/api/infrastructure")
public class InfrastructureController {

	private static final Logger logger = LoggerFactory.getLogger(InfrastructureController.class);

	@Autowired
	private InfrastructureService service;

	/**
	 * Registers a new infrastructure asset for an approved project.
	 * 
	 * @param dto Contains infrastructure type, location, capacity, and project ID.
	 * @return 201 Created and the details of the registered infrastructure.
	 */
	@PostMapping("/add")
	public ResponseEntity<InfrastructureResponseDTO> addInfra(@Valid @RequestBody InfrastructureCreateRequestDTO dto) {
		logger.info("REST request to add infrastructure: {} for Project ID: {}", dto.getType(), dto.getProjectId());
		InfrastructureResponseDTO response = service.addInfrastructure(dto);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	/**
	 * Performs a full update on an existing infrastructure asset.
	 * 
	 * @param infraId The unique ID of the infrastructure to update.
	 * @param dto     The updated infrastructure data.
	 * @return 200 OK with the updated infrastructure details.
	 */
	@PutMapping("/update/{id}")
	public ResponseEntity<InfrastructureResponseDTO> updateInfra(@PathVariable("id") long infraId,
			@Valid @RequestBody InfrastructureCreateRequestDTO dto) {
		logger.info("REST request to update infrastructure ID: {}", infraId);
		return ResponseEntity.ok(service.updateInfrastructure(infraId, dto));
	}

	/**
	 * Partially updates the operational status of an infrastructure asset.
	 * 
	 * @param dto Contains the infrastructure ID and the new status string.
	 * @return 200 OK with the updated record.
	 */
	@PatchMapping("/update-status")
	public ResponseEntity<InfrastructureResponseDTO> updateStatus(@Valid @RequestBody InfrastructureStatusDTO dto) {
		logger.info("REST request to update status for Infrastructure ID: {}", dto.getInfraId());
		return ResponseEntity.ok(service.updateStatus(dto));
	}

	/**
	 * Retrieves specific infrastructure details by ID.
	 * 
	 * @param id The unique ID of the infrastructure.
	 * @return 200 OK with the asset details.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<InfrastructureResponseDTO> getInfra(@PathVariable long id) {
		return ResponseEntity.ok(service.getInfrastructure(id));
	}

	/**
	 * Retrieves a list of all infrastructure assets in the system.
	 * 
	 * @return 200 OK with the global infrastructure inventory.
	 */
	@GetMapping("/all")
	public ResponseEntity<List<InfrastructureResponseDTO>> getAll() {
		return ResponseEntity.ok(service.getAllInfrastructure());
	}

	/**
	 * Permanently removes an infrastructure asset from the records.
	 * 
	 * @param infraId The ID of the asset to delete.
	 * @return 204 No Content upon successful deletion.
	 */
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteInfra(@PathVariable("id") long infraId) {
		logger.warn("REST request to DELETE infrastructure ID: {}", infraId);
		service.deleteInfrastructure(infraId);
		return ResponseEntity.noContent().build();
	}
}