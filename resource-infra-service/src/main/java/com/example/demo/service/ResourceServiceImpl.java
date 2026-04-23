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
import com.example.demo.dto.NotificationRequestDTO; // Added Import
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
	private final NotificationClient notificationClient; 

	@Override
	public ResourceResponseDTO addResource(ResourceCreateRequestDTO dto) {
		logger.info("Attempting to add resource for Project ID: {}", dto.getProjectId());
		validateDto(dto);

		ProjectResponseDTO project = fetchProject(dto.getProjectId());

		if (!"Approved".equalsIgnoreCase(project.getStatus())) {
			throw new ValidationException("Resources can only be allocated to 'Approved' projects.");
		}

		Resources resource = Resources.builder()
				.projectId(dto.getProjectId())
				.type(dto.getType())
				.quantity(dto.getQuantity())
				.status("Available")
				.build();

		Resources saved = resourceRepository.save(resource);
		
		// Trigger In-App Notification (isSendEmail = false)
		sendInternalNotification("New resource (" + dto.getType() + ") allocated to Project ID: " + dto.getProjectId(), 
				"RESOURCE_ALLOCATION", saved.getResourceId());

		return mapToResponseDTO(saved);
	}

	@Override
	public ResourceResponseDTO updateResource(long resourceId, ResourceCreateRequestDTO dto) {
		validateDto(dto);
		Resources existing = resourceRepository.findById(resourceId).orElseThrow(() -> 
			new ResourceNotFoundException("Resource not found with ID: " + resourceId));

		fetchProject(dto.getProjectId());

		existing.setProjectId(dto.getProjectId());
		existing.setType(dto.getType());
		existing.setQuantity(dto.getQuantity());

		ResourceResponseDTO response = mapToResponseDTO(resourceRepository.save(existing));
		
		// Trigger In-App Notification
		sendInternalNotification("Resource ID " + resourceId + " details updated.", "RESOURCE_UPDATE", resourceId);

		return response;
	}

	@Override
	@Transactional
	public void deleteResource(long resourceId) {
		Resources resource = resourceRepository.findById(resourceId)
				.orElseThrow(() -> new ResourceNotFoundException("Cannot delete: Resource ID " + resourceId + " does not exist."));
		
		if ("Allocated".equalsIgnoreCase(resource.getStatus())) {
			throw new ValidationException("Cannot delete resource: It is currently 'Allocated'.");
		}

		resourceRepository.deleteById(resourceId);
		
		// Trigger In-App Notification
		sendInternalNotification("Resource ID " + resourceId + " has been deleted.", "RESOURCE_DELETE", resourceId);
	}

	@Override
	public ResourceResponseDTO updateStatus(long resourceId, String status) {
		Resources resource = resourceRepository.findById(resourceId).orElseThrow(() -> 
			new ResourceNotFoundException("Resource not found."));

		if (status == null || !ALLOWED_STATUSES.contains(status)) {
			throw new ValidationException("Invalid status. Allowed: " + ALLOWED_STATUSES);
		}

		resource.setStatus(status);
		ResourceResponseDTO response = mapToResponseDTO(resourceRepository.save(resource));
		
		// Trigger In-App Notification
		sendInternalNotification("Resource ID " + resourceId + " status changed to " + status, "STATUS_CHANGE", resourceId);

		return response;
	}

	
	private void sendInternalNotification(String message, String category, Long entityId) {
	    try {
	        NotificationRequestDTO notifyReq = NotificationRequestDTO.builder()
	                .userId(1L)
	                .message(message)
	                .category(category)
	                .entityId(entityId)
	                .sendEmail(false)
	                .email("dummy@greengov.com")
	                .build();
	        
	        notificationClient.createNotification(notifyReq);
	        logger.info("Notification successfully sent to Notification-Service");
	    } catch (Exception e) {
	        // Log the actual cause so you can see if it's a 404, 500, or Connection Refused
	        logger.error("DETAILED NOTIFICATION ERROR: ", e); 
	    }
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
	@Transactional(readOnly = true)
	public List<ResourceResponseDTO> getResourcesByProjectId(long projectId) {
		fetchProject(projectId);
		List<Resources> resources = resourceRepository.findByProjectId(projectId);
		return resources.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
	}

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
		response.setProjectId(entity.getProjectId());
		response.setType(entity.getType());
		response.setQuantity(entity.getQuantity());
		response.setStatus(entity.getStatus());
		return response;
	}
}