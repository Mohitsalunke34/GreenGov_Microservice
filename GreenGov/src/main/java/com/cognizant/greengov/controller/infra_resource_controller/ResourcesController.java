package com.cognizant.greengov.controller.infra_resource_controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.greengov.dto.infra_resource_dto.ResourcesCreateRequestDTO;
import com.cognizant.greengov.dto.infra_resource_dto.ResourcesResponseDTO;
import com.cognizant.greengov.service.infra_resource_service.ResourcesService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for managing resource allocations (Funds and Equipment).
 * Provides endpoints for Environmental Officers and Program Managers to track
 * assets.
 */
@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourcesController {

	private static final Logger logger = LoggerFactory.getLogger(ResourcesController.class);
	private final ResourcesService service;

	/**
	 * Allocates new resources to a sustainability project.
	 * 
	 * @param dto Contains resource type, quantity, and target project ID.
	 * @return 201 Created and the allocated resource details.
	 */
	@PostMapping("/allocate")
	public ResponseEntity<ResourcesResponseDTO> allocate(@RequestBody @Valid ResourcesCreateRequestDTO dto) {
		logger.info("Environmental Officer requesting allocation of {} for Project ID: {}", dto.getType(),
				dto.getProjectId());
		ResourcesResponseDTO response = service.addResource(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/**
	 * Updates an existing resource's attributes.
	 * 
	 * @param resourceId The ID of the resource to modify.
	 * @param dto        The new resource data.
	 * @return 200 OK with the updated resource details.
	 */
	@PutMapping("/update/{id}")
	public ResponseEntity<ResourcesResponseDTO> update(@PathVariable("id") long resourceId,
			@RequestBody @Valid ResourcesCreateRequestDTO dto) {
		logger.info("REST request to update Resource ID: {}", resourceId);
		return ResponseEntity.ok(service.updateResource(resourceId, dto));
	}

	/**
	 * Retrieves a single resource record by its unique ID.
	 * 
	 * @param resourceId The ID of the resource.
	 * @return 200 OK with the resource details.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<ResourcesResponseDTO> getOne(@PathVariable("id") long resourceId) {
		logger.debug("Fetching details for Resource ID: {}", resourceId);
		return ResponseEntity.ok(service.getResource(resourceId));
	}

	/**
	 * Retrieves the global inventory of all registered resources.
	 * 
	 * @return 200 OK with a list of all resources.
	 */
	@GetMapping
	public ResponseEntity<List<ResourcesResponseDTO>> getAll() {
		logger.info("Program Manager requesting global resource inventory.");
		return ResponseEntity.ok(service.getAllResources());
	}

	/**
	 * Permanently deletes a resource from the system.
	 * 
	 * @param resourceId The ID of the resource to delete.
	 * @return 204 No Content upon successful deletion.
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") long resourceId) {
		logger.warn("REST request to DELETE Resource ID: {}", resourceId);
		service.deleteResource(resourceId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Partially updates the status of a specific resource.
	 * 
	 * @param resourceId The ID of the resource.
	 * @param status     The new status string (e.g., 'Allocated', 'Depleted').
	 * @return 200 OK with the updated resource record.
	 */
	@PatchMapping("/{id}/status")
	public ResponseEntity<ResourcesResponseDTO> updateStatus(@PathVariable("id") long resourceId,
			@RequestParam String status) {
		logger.info("Updating status of Resource {} to {}", resourceId, status);
		return ResponseEntity.ok(service.updateStatus(resourceId, status));
	}
}