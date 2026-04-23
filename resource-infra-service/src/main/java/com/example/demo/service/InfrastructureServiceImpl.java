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

	@Override
	public InfrastructureResponseDTO addInfrastructure(InfrastructureCreateRequestDTO dto) {
		logger.info("Adding new infrastructure for Project ID: {}", dto.getProjectId());

		ProjectResponseDTO project = fetchProject(dto.getProjectId());

		Infrastructure infra = Infrastructure.builder().projectId(dto.getProjectId()).type(dto.getType())
				.location(dto.getLocation()).capacity(dto.getCapacity()).status("Planned").build();

		Infrastructure saved = infraRepository.save(infra);

		// Trigger Notification
		sendInternalNotification(
				"New " + dto.getType() + " infrastructure planned for Project ID: " + dto.getProjectId(),
				"INFRASTRUCTURE", saved.getInfraId());

		return mapToResponseDTO(saved);
	}

	@Override
	public InfrastructureResponseDTO updateInfrastructure(long infraId, InfrastructureCreateRequestDTO dto) {
		Infrastructure existing = infraRepository.findById(infraId)
				.orElseThrow(() -> new ResourceNotFoundException("Infrastructure not found with ID: " + infraId));

		fetchProject(dto.getProjectId());

		existing.setProjectId(dto.getProjectId());
		existing.setType(dto.getType());
		existing.setLocation(dto.getLocation());
		existing.setCapacity(dto.getCapacity());

		Infrastructure saved = infraRepository.save(existing);

		
		sendInternalNotification("Infrastructure ID " + infraId + " details updated.", "INFRASTRUCTURE", infraId);

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
		try {
			NotificationRequestDTO notifyReq = NotificationRequestDTO.builder().userId(1L) // Testing ID
					.message(message).category(category).entityId(entityId).sendEmail(false) // Keeping email off per
																								// requirements
					.email("admin@greengov.com").build();

			notificationClient.createNotification(notifyReq);
			logger.info("Infrastructure notification triggered: {}", category);
		} catch (Exception e) {
			// Fail-safe: Don't break the main business logic if notifications fail
			logger.error("Infrastructure notification failed: {}", e.getMessage());
		}
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
		try {
			ResponseEntity<ProjectResponseDTO> response = projectClient.getProjectById(projectId);
			if (response.getBody() == null) {
				throw new ResourceNotFoundException("Project not found with ID: " + projectId);
			}
			return response.getBody();
		} catch (Exception e) {
			logger.error("Error communicating with Project Service: {}", e.getMessage());
			throw new RuntimeException("Project verification failed for ID: " + projectId);
		}
	}

	private InfrastructureResponseDTO mapToResponseDTO(Infrastructure entity) {
		InfrastructureResponseDTO response = new InfrastructureResponseDTO();
		response.setInfraId(entity.getInfraId());
		response.setProjectId(entity.getProjectId());
		response.setType(entity.getType());
		response.setLocation(entity.getLocation());
		response.setCapacity(entity.getCapacity());
		response.setStatus(entity.getStatus());
		return response;
	}
}