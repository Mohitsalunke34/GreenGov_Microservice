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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ResourceCreateRequestDTO;
import com.example.demo.dto.ResourceResponseDTO;
import com.example.demo.service.ResourceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

	private static final Logger logger = LoggerFactory.getLogger(ResourceController.class);
	private final ResourceService service;

	/**
	 * US23: Allocate resources to a specific sustainability project. 
	 * POST /api/resources/allocate
	 */
	@PostMapping("/allocate")
	public ResponseEntity<ResourceResponseDTO> allocate(@RequestBody @Valid ResourceCreateRequestDTO dto) {
		logger.info("Allocating {} for Project ID: {}", dto.getType(), dto.getProjectId());
		ResourceResponseDTO response = service.addResource(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/**
	 * US26: Update existing resource details. 
	 * PUT /api/resources/{id}
	 */
	@PutMapping("/{id}") 
	public ResponseEntity<ResourceResponseDTO> update(
			@PathVariable("id") long resourceId,
			@RequestBody @Valid ResourceCreateRequestDTO dto) {
		logger.info("REST request to update Resource ID: {}", resourceId);
		return ResponseEntity.ok(service.updateResource(resourceId, dto));
	}

	/**
	 * US24: View details of a single resource. 
	 * GET /api/resources/{id}
	 */
	@GetMapping("/{id}")
	public ResponseEntity<ResourceResponseDTO> getOne(@PathVariable("id") long resourceId) {
		logger.debug("Fetching details for Resource ID: {}", resourceId);
		return ResponseEntity.ok(service.getResource(resourceId));
	}

	/**
	 * US25: View all registered resources (Global Inventory). 
	 * GET /api/resources
	 */
	@GetMapping("/get-all")
	public ResponseEntity<List<ResourceResponseDTO>> getAll() {
		logger.info("Fetching global resource inventory.");
		return ResponseEntity.ok(service.getAllResources());
	}

	/**
	 * US28: Get all resources for a specific project.
	 * GET /api/resources/project/{projectId}
	 */
	@GetMapping("/project/{projectId}")
	public ResponseEntity<List<ResourceResponseDTO>> getByProject(@PathVariable("projectId") long projectId) {
		logger.info("Fetching resources for Project ID: {}", projectId);
		return ResponseEntity.ok(service.getResourcesByProjectId(projectId));
	}

	/**
	 * US27: Remove a resource record. 
	 * DELETE /api/resources/{id}
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") long resourceId) {
		logger.warn("REST request to DELETE Resource ID: {}", resourceId);
		service.deleteResource(resourceId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * US21: Update resource status (e.g., Available -> Allocated). 
	 * PATCH /api/resources/{id}/status
	 */
	@PatchMapping("/{id}/status")
	public ResponseEntity<ResourceResponseDTO> updateStatus(
			@PathVariable("id") long resourceId,
			@RequestParam("status") String status) { // Added explicit param name
		logger.info("Updating status of Resource {} to {}", resourceId, status);
		return ResponseEntity.ok(service.updateStatus(resourceId, status));
	}
}