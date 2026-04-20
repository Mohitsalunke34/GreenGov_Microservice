package com.example.demo.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.client.ProjectClient;
import com.example.demo.dto.ProjectResponseDTO;
import com.example.demo.dto.ResourceCreateRequestDTO;
import com.example.demo.dto.ResourceResponseDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Resources;
import com.example.demo.repository.ResourceRepository;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ResourceServiceImpl implements ResourceService {

	private static final Logger logger = LoggerFactory.getLogger(ResourceServiceImpl.class);
	private static final List<String> ALLOWED_TYPES = Arrays.asList("Funds", "Equipment");
	private static final List<String> ALLOWED_STATUSES = Arrays.asList("Available", "Allocated", "Depleted");

	private final ResourceRepository resourceRepository;
	
	private final ProjectClient projectClient;

	@Override
	public ResourceResponseDTO addResource(ResourceCreateRequestDTO dto) {
		logger.info("Attempting to add resource for Project ID: {}", dto.getProjectId());

		validateDto(dto);

		// 2. Fetch Project details via Microservice call (OpenFeign)
		ProjectResponseDTO project = fetchProject(dto.getProjectId());

		// 3. Check Business Rule: Must be 'Approved'
		if (!"Approved".equalsIgnoreCase(project.getStatus())) {
			logger.warn("Allocation rejected: Project {} is {}", dto.getProjectId(), project.getStatus());
			throw new ValidationException("Resources can only be allocated to 'Approved' projects.");
		}

		// 4. Build Resource using the ID only
		Resources resource = Resources.builder()
				.projectId(dto.getProjectId()) // Store ID, not the object
				.type(dto.getType())
				.quantity(dto.getQuantity())
				.status("Available")
				.build();

		Resources saved = resourceRepository.save(resource);
		return mapToResponseDTO(saved);
	}

	@Override
	public ResourceResponseDTO updateResource(long resourceId, ResourceCreateRequestDTO dto) {
		validateDto(dto);

		Resources existing = resourceRepository.findById(resourceId).orElseThrow(() -> 
			new ResourceNotFoundException("Resource not found with ID: " + resourceId));

		// Verify target project exists in the Project Service
		fetchProject(dto.getProjectId());

		existing.setProjectId(dto.getProjectId());
		existing.setType(dto.getType());
		existing.setQuantity(dto.getQuantity());

		return mapToResponseDTO(resourceRepository.save(existing));
	}

	@Override
	@Transactional(readOnly = true)
	public ResourceResponseDTO getResource(long resourceId) {
		Resources resource = resourceRepository.findById(resourceId).orElseThrow(() -> 
			new ResourceNotFoundException("Resource ID " + resourceId + " not found."));
		return mapToResponseDTO(resource);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ResourceResponseDTO> getAllResources() {
		return resourceRepository.findAll().stream()
				.map(this::mapToResponseDTO)
				.collect(Collectors.toList());
	}

	@Override
	public void deleteResource(long resourceId) {
		if (!resourceRepository.existsById(resourceId)) {
			throw new ResourceNotFoundException("Cannot delete: Resource ID " + resourceId + " does not exist.");
		}
		resourceRepository.deleteById(resourceId);
	}

	@Override
	public ResourceResponseDTO updateStatus(long resourceId, String status) {
		Resources resource = resourceRepository.findById(resourceId).orElseThrow(() -> 
			new ResourceNotFoundException("Resource not found."));

		if (status == null || !ALLOWED_STATUSES.contains(status)) {
			throw new ValidationException("Invalid status. Allowed: " + ALLOWED_STATUSES);
		}

		resource.setStatus(status);
		return mapToResponseDTO(resourceRepository.save(resource));
	}

	@Override
	@Transactional(readOnly = true)
	public List<ResourceResponseDTO> getResourcesByProjectId(long projectId) {
		// Verify project exists before returning resources
		fetchProject(projectId);
		
		List<Resources> resources = resourceRepository.findByProjectId(projectId);

		return resources.stream()
				.map(this::mapToResponseDTO)
				.collect(Collectors.toList());
	}

	/**
	 * Helper method to handle Feign Client calls and Error Handling
	 */
	private ProjectResponseDTO fetchProject(long projectId) {
		try {
			ResponseEntity<ProjectResponseDTO> response = projectClient.getProjectById(projectId);
			if (response.getBody() == null) {
				throw new ResourceNotFoundException("Project not found with ID: " + projectId);
			}
			return response.getBody();
		} catch (Exception e) {
			logger.error("Project Service communication failed: {}", e.getMessage());
			throw new ResourceNotFoundException("Could not verify Project ID " + projectId + " (Service might be down)");
		}
	}

	private void validateDto(ResourceCreateRequestDTO dto) {
		if (!ALLOWED_TYPES.contains(dto.getType())) {
			throw new ValidationException("Invalid Type. Must be 'Funds' or 'Equipment'.");
		}
		if (dto.getQuantity() <= 0) {
			throw new ValidationException("Quantity must be greater than zero.");
		}
	}

	private ResourceResponseDTO mapToResponseDTO(Resources entity) {
		ResourceResponseDTO response = new ResourceResponseDTO();
		response.setResourceId(entity.getResourceId());
		response.setProjectId(entity.getProjectId()); // Using the Long ID
		response.setType(entity.getType());
		response.setQuantity(entity.getQuantity());
		response.setStatus(entity.getStatus());
		return response;
	}
}