package com.cognizant.greengov.service.infra_resource_service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cognizant.greengov.dto.infra_resource_dto.InfrastructureCreateRequestDTO;
import com.cognizant.greengov.dto.infra_resource_dto.InfrastructureResponseDTO;
import com.cognizant.greengov.dto.infra_resource_dto.InfrastructureStatusDTO;
import com.cognizant.greengov.exception.ResourceNotFoundException;
import com.cognizant.greengov.model.resource_infrastructure.Infrastructure;
import com.cognizant.greengov.model.sustainability_renewable_proj.SustainabilityProject;
import com.cognizant.greengov.repository.infra_resource_repo.InfrastructureRepository;
import com.cognizant.greengov.repository.sustainability_repo.SustainabilityProjectRepository;

import jakarta.validation.ValidationException;

/**
 * Service implementation for managing Government Infrastructure assets. Handles
 * the creation, status tracking, and decommissioning of physical assets like
 * Solar Plants, Wind Farms, and Recycling Units.
 */
@Service
public class InfrastructureServiceImpl implements InfrastructureService {

	private static final Logger logger = LoggerFactory.getLogger(InfrastructureServiceImpl.class);

	private static final List<String> ALLOWED_TYPES = Arrays.asList("SolarPlant", "WindFarm", "RecyclingUnit");
	private static final List<String> VALID_STATUSES = Arrays.asList("Active", "Inactive", "Maintenance",
			"Under Construction");
	private static final int MAX_CAPACITY_LIMIT = 5000;

	@Autowired
	private InfrastructureRepository repository;

	@Autowired
	private SustainabilityProjectRepository projectRepository;

	/**
	 * Internal helper to validate infrastructure payloads against business rules.
	 * * @param dto the request data to validate.
	 * 
	 * @throws ValidationException if type is invalid or capacity exceeds limits.
	 */
	private void validateInfrastructure(InfrastructureCreateRequestDTO dto) {
		if (dto == null) {
			logger.error("Validation failed: Infrastructure payload is null.");
			throw new ValidationException("Payload cannot be null.");
		}
		if (dto.getType() == null || !ALLOWED_TYPES.contains(dto.getType())) {
			logger.error("Validation failed: Invalid Type '{}'. Allowed: {}", dto.getType(), ALLOWED_TYPES);
			throw new ValidationException("Invalid Type. Allowed: " + ALLOWED_TYPES);
		}
		if (dto.getCapacity() <= 0 || dto.getCapacity() > MAX_CAPACITY_LIMIT) {
			logger.error("Validation failed: Capacity {} out of range [1-{}]", dto.getCapacity(), MAX_CAPACITY_LIMIT);
			throw new ValidationException("Capacity must be between 1 and " + MAX_CAPACITY_LIMIT);
		}
	}

	/**
	 * Registers a new infrastructure asset and links it to a sustainability
	 * project. Default status is set to 'Under Construction'.
	 *
	 * @param dto data for the new infrastructure.
	 * @return the saved infrastructure details.
	 * @throws ResourceNotFoundException if the project ID does not exist.
	 */
	@Override
	@Transactional
	public InfrastructureResponseDTO addInfrastructure(InfrastructureCreateRequestDTO dto) {
		logger.info("Attempting to add new {} infrastructure at location: {}", dto.getType(), dto.getLocation());

		validateInfrastructure(dto);
		SustainabilityProject project = projectRepository.findById(dto.getProjectId()).orElseThrow(() -> {
			logger.error("Failed to add infrastructure: Project ID {} not found.", dto.getProjectId());
			return new ResourceNotFoundException("Project ID " + dto.getProjectId() + " not found.");
		});

		if (!"Approved".equalsIgnoreCase(project.getStatus())) {
			logger.warn("Infrastructure creation rejected: Project ID {} status is '{}'. Must be 'Approved'.",
					dto.getProjectId(), project.getStatus());
			throw new ValidationException("Infrastructure can only be added to projects with 'Approved' status.");
		}

		Infrastructure infra = Infrastructure.builder().project(project).type(dto.getType()).location(dto.getLocation())
				.capacity(dto.getCapacity()).status("Under Construction").build();

		Infrastructure saved = repository.save(infra);
		logger.info("Infrastructure created successfully with ID: {} for Project: {}", saved.getInfraId(),
				dto.getProjectId());

		return mapToResponseDTO(saved);
	}

	/**
	 * Updates an existing infrastructure record with new capacity, location, or
	 * type details.
	 *
	 * @param infraId the ID of the infrastructure to update.
	 * @param dto     the new data to apply.
	 * @return the updated infrastructure details.
	 */
	@Override
	@Transactional
	public InfrastructureResponseDTO updateInfrastructure(long infraId, InfrastructureCreateRequestDTO dto) {
		logger.info("Request to update Infrastructure ID: {}", infraId);
		validateInfrastructure(dto);

		Infrastructure infra = repository.findById(infraId).orElseThrow(() -> {
			logger.error("Update failed: Infrastructure ID {} not found.", infraId);
			return new ResourceNotFoundException("Infrastructure ID " + infraId + " not found.");
		});

		SustainabilityProject project = projectRepository.findById(dto.getProjectId()).orElseThrow(() -> {
			logger.error("Update failed: Target Project ID {} not found.", dto.getProjectId());
			return new ResourceNotFoundException("Project ID " + dto.getProjectId() + " not found.");
		});

		infra.setProject(project);
		infra.setType(dto.getType());
		infra.setLocation(dto.getLocation());
		infra.setCapacity(dto.getCapacity());

		Infrastructure updated = repository.save(infra);
		logger.info("Infrastructure ID: {} updated successfully.", infraId);
		return mapToResponseDTO(updated);
	}

	/**
	 * Updates the operational status of a specific infrastructure asset.
	 *
	 * @param dto DTO containing the asset ID and the new status string.
	 * @return updated record details.
	 * @throws ValidationException if the status string is not in the approved list.
	 */
	@Override
	@Transactional
	public InfrastructureResponseDTO updateStatus(InfrastructureStatusDTO dto) {
		logger.info("Request to update status for Infrastructure ID: {} to {}", dto.getInfraId(), dto.getStatus());
		Infrastructure infra = repository.findById(dto.getInfraId()).orElseThrow(() -> {
			logger.error("Status update failed: Infrastructure ID {} not found.", dto.getInfraId());
			return new ResourceNotFoundException("Infrastructure ID " + dto.getInfraId() + " not found.");
		});

		if (dto.getStatus() == null || !VALID_STATUSES.contains(dto.getStatus())) {
			logger.warn("Status update rejected: '{}' is not a valid status.", dto.getStatus());
			throw new ValidationException("Invalid status. Allowed: " + VALID_STATUSES);
		}

		String oldStatus = infra.getStatus();
		infra.setStatus(dto.getStatus());
		Infrastructure saved = repository.save(infra);

		logger.info("Infrastructure ID: {} status changed from {} to {}", dto.getInfraId(), oldStatus, dto.getStatus());
		return mapToResponseDTO(saved);
	}

	/**
	 * Fetches details of a single infrastructure asset by ID.
	 *
	 * @param infraId target ID.
	 * @return found record details.
	 */
	@Override
	@Transactional(readOnly = true)
	public InfrastructureResponseDTO getInfrastructure(long infraId) {
		logger.debug("Fetching infrastructure details for ID: {}", infraId);
		return repository.findById(infraId).map(this::mapToResponseDTO).orElseThrow(() -> {
			logger.warn("Fetch failed: Infrastructure ID {} not found.", infraId);
			return new ResourceNotFoundException("Infrastructure ID " + infraId + " not found.");
		});
	}

	/**
	 * Retrieves the entire global inventory of Government infrastructure.
	 *
	 * @return a list of all infrastructure records.
	 */
	@Override
	@Transactional(readOnly = true)
	public List<InfrastructureResponseDTO> getAllInfrastructure() {
		logger.info("Retrieving all infrastructure records.");
		List<Infrastructure> allInfra = repository.findAll();
		logger.debug("Found {} records in infrastructure inventory.", allInfra.size());
		return allInfra.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
	}

	/**
	 * Removes an infrastructure record from the system. Only records that are NOT
	 * in 'Active' status can be deleted.
	 *
	 * @param infraId ID to delete.
	 * @throws ValidationException if the asset is currently 'Active'.
	 */
	@Override
	@Transactional
	public void deleteInfrastructure(long infraId) {
		logger.warn("Request to DELETE Infrastructure ID: {}", infraId);
		Infrastructure infra = repository.findById(infraId).orElseThrow(() -> {
			logger.error("Delete failed: Infrastructure ID {} not found.", infraId);
			return new ResourceNotFoundException("Infrastructure ID " + infraId + " not found.");
		});

		if ("Active".equalsIgnoreCase(infra.getStatus())) {
			logger.error("Delete rejected: Infrastructure ID {} is currently 'Active'.", infraId);
			throw new ValidationException("Cannot delete an Active resource.");
		}

		repository.delete(infra);
		logger.info("Infrastructure ID {} successfully deleted.", infraId);
	}

	/**
	 * Maps the Infrastructure entity to a DTO for API response.
	 *
	 * @param entity the database model.
	 * @return the mapped DTO.
	 */
	private InfrastructureResponseDTO mapToResponseDTO(Infrastructure entity) {
		logger.debug("Mapping Infrastructure entity ID: {} to DTO.", entity.getInfraId());
		InfrastructureResponseDTO response = new InfrastructureResponseDTO();
		response.setInfraId(entity.getInfraId());
		response.setProjectId(entity.getProject().getProjectId());
		response.setType(entity.getType());
		response.setLocation(entity.getLocation());
		response.setCapacity(entity.getCapacity());
		response.setStatus(entity.getStatus());
		return response;
	}
}