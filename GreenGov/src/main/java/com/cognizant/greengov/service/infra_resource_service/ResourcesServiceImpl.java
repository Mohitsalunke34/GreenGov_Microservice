package com.cognizant.greengov.service.infra_resource_service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cognizant.greengov.dto.infra_resource_dto.ResourcesCreateRequestDTO;
import com.cognizant.greengov.dto.infra_resource_dto.ResourcesResponseDTO;
import com.cognizant.greengov.exception.ResourceNotFoundException;
import com.cognizant.greengov.model.resource_infrastructure.Resources;
import com.cognizant.greengov.model.sustainability_renewable_proj.SustainabilityProject;
import com.cognizant.greengov.repository.infra_resource_repo.ResourcesRepository;
import com.cognizant.greengov.repository.sustainability_repo.SustainabilityProjectRepository;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of the {@link ResourcesService} to manage environmental
 * resources. Handles allocation, updates, tracking, and lifecycle management of
 * project assets.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ResourcesServiceImpl implements ResourcesService {

	private static final Logger logger = LoggerFactory.getLogger(ResourcesServiceImpl.class);
	private static final List<String> ALLOWED_TYPES = Arrays.asList("Funds", "Equipment");

	private final ResourcesRepository resourceRepository;
	private final SustainabilityProjectRepository projectRepository;

	/**
	 * Allocates a new resource to a specific sustainability project. Validates the
	 * resource type and quantity before establishing a link with the project.
	 *
	 * @param dto the data transfer object containing project ID, resource type, and
	 *            quantity.
	 * @return {@link ResourcesResponseDTO} containing the saved resource details
	 *         and generated ID.
	 * @throws ValidationException       if the type is invalid or quantity is
	 *                                   non-positive.
	 * @throws ResourceNotFoundException if the specified project ID does not exist.
	 */
	@Override
	public ResourcesResponseDTO addResource(ResourcesCreateRequestDTO dto) {
		logger.info("Attempting to add new resource of type: {} for Project ID: {}", dto.getType(), dto.getProjectId());

		validateDto(dto);

		SustainabilityProject project = projectRepository.findById(dto.getProjectId()).orElseThrow(() -> {
			logger.error("Resource allocation failed: Project ID {} not found.", dto.getProjectId());
			return new ResourceNotFoundException("Project not found with ID: " + dto.getProjectId());
		});

		if (!"Approved".equalsIgnoreCase(project.getStatus())) {
			logger.warn("Resource allocation rejected: Project ID {} has status '{}', but must be 'Approved'.",
					dto.getProjectId(), project.getStatus());
			throw new ValidationException("Resources can only be allocated to projects with 'Approved' status.");
		}

		Resources resource = Resources.builder().project(project).type(dto.getType()).quantity(dto.getQuantity())
				.status("Available").build();

		Resources saved = resourceRepository.save(resource);
		logger.info("Successfully saved resource. Assigned Resource ID: {} to Project ID: {}", saved.getResourceId(),
				dto.getProjectId());

		return mapToResponseDTO(saved);
	}

	/**
	 * Updates an existing resource's information, including its type, quantity, or
	 * project assignment.
	 *
	 * @param resourceId the unique identifier of the resource to be updated.
	 * @param dto        the updated data (type, quantity, project ID).
	 * @return {@link ResourcesResponseDTO} containing the updated state of the
	 *         resource.
	 * @throws ResourceNotFoundException if the resource or the target project does
	 *                                   not exist.
	 * @throws ValidationException       if the update data fails validation
	 *                                   constraints.
	 */
	@Override
	public ResourcesResponseDTO updateResource(long resourceId, ResourcesCreateRequestDTO dto) {
		logger.info("Request to update Resource ID: {} with new data for Project ID: {}", resourceId,
				dto.getProjectId());

		validateDto(dto);

		Resources existing = resourceRepository.findById(resourceId).orElseThrow(() -> {
			logger.error("Update failed: Resource ID {} does not exist.", resourceId);
			return new ResourceNotFoundException("Resource not found with ID: " + resourceId);
		});

		SustainabilityProject project = projectRepository.findById(dto.getProjectId()).orElseThrow(() -> {
			logger.error("Update failed: Target Project ID {} not found.", dto.getProjectId());
			return new ResourceNotFoundException("Project not found with ID: " + dto.getProjectId());
		});

		existing.setProject(project);
		existing.setType(dto.getType());
		existing.setQuantity(dto.getQuantity());

		Resources updated = resourceRepository.save(existing);
		logger.info("Resource ID: {} updated successfully.", resourceId);
		return mapToResponseDTO(updated);
	}

	/**
	 * Retrieves the details of a specific resource by its ID.
	 *
	 * @param resourceId the unique identifier of the resource.
	 * @return {@link ResourcesResponseDTO} containing the resource details.
	 * @throws ResourceNotFoundException if no resource is found with the given ID.
	 */
	@Override
	@Transactional(readOnly = true)
	public ResourcesResponseDTO getResource(long resourceId) {
		logger.debug("Fetching resource details for ID: {}", resourceId);
		Resources resource = resourceRepository.findById(resourceId).orElseThrow(() -> {
			logger.warn("Fetch failed: Resource ID {} not found.", resourceId);
			return new ResourceNotFoundException("Resource ID " + resourceId + " not found.");
		});
		return mapToResponseDTO(resource);
	}

	/**
	 * Fetches all resources currently registered in the global inventory.
	 *
	 * @return a List of {@link ResourcesResponseDTO} objects representing all
	 *         records.
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ResourcesResponseDTO> getAllResources() {
		logger.info("Fetching global resource inventory.");
		List<Resources> resources = resourceRepository.findAll();
		logger.debug("Retrieved {} resource records from the database.", resources.size());
		return resources.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
	}

	/**
	 * Permanently removes a resource record from the database.
	 *
	 * @param resourceId the unique identifier of the resource to be deleted.
	 * @throws ResourceNotFoundException if the resource ID does not exist.
	 */
	@Override
	public void deleteResource(long resourceId) {
		logger.warn("Received request to permanently delete Resource ID: {}", resourceId);
		if (!resourceRepository.existsById(resourceId)) {
			logger.error("Deletion failed: Resource ID {} not found.", resourceId);
			throw new ResourceNotFoundException("Cannot delete: Resource ID " + resourceId + " does not exist.");
		}
		resourceRepository.deleteById(resourceId);
		logger.info("Resource ID {} successfully purged from the system.", resourceId);
	}

	/**
	 * Updates the operational status (e.g., 'Available', 'Allocated', 'Depleted')
	 * of a specific resource.
	 *
	 * @param resourceId the unique identifier of the resource.
	 * @param status     the new status string to apply.
	 * @return {@link ResourcesResponseDTO} with the updated status.
	 * @throws ResourceNotFoundException if the resource ID is invalid.
	 */
	@Override
	public ResourcesResponseDTO updateStatus(long resourceId, String status) {
		logger.info("Updating status for Resource ID: {} to {}", resourceId, status);

		Resources resource = resourceRepository.findById(resourceId).orElseThrow(() -> {
			logger.error("Status update failed: Resource ID {} not found.", resourceId);
			return new ResourceNotFoundException("Resource not found.");
		});

		String oldStatus = resource.getStatus();
		resource.setStatus(status);
		Resources saved = resourceRepository.save(resource);

		logger.info("Status changed for Resource {}: {} -> {}", resourceId, oldStatus, status);
		return mapToResponseDTO(saved);
	}

	/**
	 * Internal validation logic to enforce business rules for resource creation and
	 * updates. Checks for allowed types and valid quantity values.
	 *
	 * @param dto the request DTO to validate.
	 * @throws ValidationException if validation rules are violated.
	 */
	private void validateDto(ResourcesCreateRequestDTO dto) {
		if (!ALLOWED_TYPES.contains(dto.getType())) {
			logger.error("Validation Error: Type '{}' is not in allowed list {}", dto.getType(), ALLOWED_TYPES);
			throw new ValidationException("Invalid Type. Must be 'Funds' or 'Equipment'.");
		}
		if (dto.getQuantity() <= 0) {
			logger.error("Validation Error: Quantity {} is non-positive.", dto.getQuantity());
			throw new ValidationException("Quantity must be greater than zero.");
		}
	}

	/**
	 * Maps a {@link Resources} entity to a {@link ResourcesResponseDTO} for
	 * external API consumption. Specifically decouples the circular project
	 * relationship to prevent serialization errors.
	 *
	 * @param entity the database entity to map.
	 * @return the mapped response DTO.
	 */
	private ResourcesResponseDTO mapToResponseDTO(Resources entity) {
		logger.debug("Mapping Resource entity ID {} to Response DTO.", entity.getResourceId());
		ResourcesResponseDTO response = new ResourcesResponseDTO();
		response.setResourceId(entity.getResourceId());
		response.setProjectId(entity.getProject().getProjectId());
		response.setType(entity.getType());
		response.setQuantity(entity.getQuantity());
		response.setStatus(entity.getStatus());
		return response;
	}
}