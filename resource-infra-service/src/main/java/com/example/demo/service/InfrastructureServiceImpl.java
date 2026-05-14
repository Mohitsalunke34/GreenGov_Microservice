package com.example.demo.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.client.NotificationClient; // Added Import
import com.example.demo.client.ProjectClient;
import com.example.demo.dto.InfrastructureCreateRequestDTO;
import com.example.demo.dto.InfrastructureResponseDTO;
import com.example.demo.dto.InfrastructureStatusDTO;
import com.example.demo.dto.NotificationRequestDTO; // Added Import
import com.example.demo.dto.ProjectResponseDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Infrastructure;
import com.example.demo.repository.InfrastructureRepository;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class InfrastructureServiceImpl implements InfrastructureService {

	private static final Logger logger = LoggerFactory.getLogger(InfrastructureServiceImpl.class);
	private static final List<String> ALLOWED_STATUSES = Arrays.asList("Planned", "Under Construction", "Operational");

	private final InfrastructureRepository infraRepository;
	private final ProjectClient projectClient;
	private final NotificationClient notificationClient;
	private static final List<String> ALLOWED_INFRA_TYPES = Arrays.asList("SolarPlant", "WindFarm", "RecyclingUnit");

	@Override
	public InfrastructureResponseDTO addInfrastructure(InfrastructureCreateRequestDTO dto) {
		logger.info("Adding new infrastructure for Project ID: {}", dto.getProjectId());

		// 1. Validate Type
		if (!ALLOWED_INFRA_TYPES.contains(dto.getType())) {
			logger.error("Invalid infrastructure type: {}", dto.getType());
			throw new ValidationException("Invalid Type. Allowed: " + ALLOWED_INFRA_TYPES);
		}

		// 2. Fetch project and check Approval Status
		ProjectResponseDTO project = fetchProject(dto.getProjectId());
		if (!"Approved".equalsIgnoreCase(project.getStatus())) {
			throw new ValidationException("Infrastructure can only be added to 'Approved' projects.");
		}

		// 3. Build and Save
		Infrastructure infra = Infrastructure.builder().projectId(dto.getProjectId()).projectTitle(project.getTitle())
				.type(dto.getType()).location(dto.getLocation()).capacity(dto.getCapacity()).status("Planned").build();

		Infrastructure saved = infraRepository.save(infra);

		sendInternalNotification("New " + dto.getType() + " infrastructure planned for Project: " + project.getTitle(),
				"INFRASTRUCTURE", saved.getInfraId());

		return mapToResponseDTO(saved);
	}

	@Override
	public InfrastructureResponseDTO updateInfrastructure(long infraId, InfrastructureCreateRequestDTO dto) {
		Infrastructure existing = infraRepository.findById(infraId)
				.orElseThrow(() -> new ResourceNotFoundException("Infrastructure not found with ID: " + infraId));

		ProjectResponseDTO project = fetchProject(dto.getProjectId());
		if (!ALLOWED_INFRA_TYPES.contains(dto.getType())) {
			logger.error("Invalid infrastructure type: {}", dto.getType());
			throw new ValidationException("Invalid Type. Allowed: " + ALLOWED_INFRA_TYPES);
		}

		existing.setProjectId(dto.getProjectId());
		existing.setProjectTitle(project.getTitle());

		existing.setType(dto.getType());
		existing.setLocation(dto.getLocation());
		existing.setCapacity(dto.getCapacity());

		Infrastructure saved = infraRepository.save(existing);
		sendInternalNotification("Infrastructure ID " + infraId + " details updated for Project: " + project.getTitle(),
				"INFRASTRUCTURE", infraId);

		return mapToResponseDTO(saved);
	}

	@Override
	public InfrastructureResponseDTO updateStatus(InfrastructureStatusDTO dto) {
		Infrastructure infra = infraRepository.findById(dto.getInfraId()).orElseThrow(
				() -> new ResourceNotFoundException("Infrastructure not found with ID: " + dto.getInfraId()));

		if (dto.getStatus() == null || !ALLOWED_STATUSES.contains(dto.getStatus())) {
			logger.warn("Invalid status update attempt: {}", dto.getStatus());
			throw new ValidationException("Invalid status. Allowed values: " + ALLOWED_STATUSES);
		}

		infra.setStatus(dto.getStatus());
		Infrastructure saved = infraRepository.save(infra);

		// Trigger Notification
		sendInternalNotification("Infrastructure ID " + dto.getInfraId() + " status changed to " + dto.getStatus(),
				"STATUS_CHANGE", dto.getInfraId());

		return mapToResponseDTO(saved);
	}

	@Override
	@Transactional
	public void deleteInfrastructure(long infraId) {
		Infrastructure infra = infraRepository.findById(infraId)
				.orElseThrow(() -> new ResourceNotFoundException("Cannot delete: ID " + infraId + " not found."));

		if ("Operational".equalsIgnoreCase(infra.getStatus())) {
			logger.warn("Delete blocked: Infrastructure ID {} is Operational.", infraId);
			throw new ValidationException("Access Denied: Operational infrastructure cannot be deleted.");
		}

		infraRepository.deleteById(infraId);

		sendInternalNotification("Infrastructure ID " + infraId + " has been removed from the system.",
				"INFRASTRUCTURE", infraId);

		logger.info("Infrastructure ID {} successfully purged.", infraId);
	}

	private void sendInternalNotification(String message, String category, Long entityId) {
		NotificationRequestDTO notifyReq = NotificationRequestDTO.builder().userId(1L).message(message)
				.category(category).entityId(entityId).sendEmail(false).email("system@greengov.com").build();
		notificationClient.createNotification(notifyReq);
		logger.info("Notification request processed (either sent or handled by fallback).");
	}

	/* ================= READ METHODS ================= */

	@Override
	@Transactional(readOnly = true)
	public InfrastructureResponseDTO getInfrastructure(long infraId) {
		return infraRepository.findById(infraId).map(this::mapToResponseDTO)
				.orElseThrow(() -> new ResourceNotFoundException("Infrastructure not found"));
	}

	@Override
	@Transactional(readOnly = true)
	public List<InfrastructureResponseDTO> getAllInfrastructure() {
		return infraRepository.findAll().stream().map(this::mapToResponseDTO).collect(Collectors.toList());
	}

	private ProjectResponseDTO fetchProject(long projectId) {
		ResponseEntity<ProjectResponseDTO> response = projectClient.getProjectById(projectId);
		ProjectResponseDTO project = response.getBody();

		if (project == null || "SERVICE_FAILURE".equals(project.getStatus())) {
			throw new ResourceNotFoundException("Project Service is currently unavailable. Please try again later.");
		}

		return project;
	}

	private InfrastructureResponseDTO mapToResponseDTO(Infrastructure entity) {
		InfrastructureResponseDTO response = new InfrastructureResponseDTO();
		response.setInfraId(entity.getInfraId());
		response.setProjectId(entity.getProjectId());
		response.setProjectTitle(entity.getProjectTitle());
		response.setType(entity.getType());
		response.setLocation(entity.getLocation());
		response.setCapacity(entity.getCapacity());
		response.setStatus(entity.getStatus());
		return response;
	}
}