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

	    // This call returns the project details including the title
	    ProjectResponseDTO project = fetchProject(dto.getProjectId());

	    if (!"Approved".equalsIgnoreCase(project.getStatus())) {
	        throw new ValidationException("Resources can only be allocated to 'Approved' projects.");
	    }

	    // Capture the title from the Feign Client response
	    Resources resource = Resources.builder()
	            .projectId(dto.getProjectId())
	            .projectTitle(project.getTitle()) // Map the title here
	            .type(dto.getType())
	            .quantity(dto.getQuantity())
	            .status("Available")
	            .build();

	    Resources saved = resourceRepository.save(resource);

	    // Trigger Notification
	    sendInternalNotification("New resource (" + dto.getType() + ") allocated to Project: " + project.getTitle(),
	            "RESOURCE_ALLOCATION", saved.getResourceId());

	    return mapToResponseDTO(saved);
	}
	@Override
	public ResourceResponseDTO updateResource(long resourceId, ResourceCreateRequestDTO dto) {
	    validateDto(dto);
	    Resources existing = resourceRepository.findById(resourceId)
	            .orElseThrow(() -> new ResourceNotFoundException("Resource not found with ID: " + resourceId));
	    ProjectResponseDTO project = fetchProject(dto.getProjectId());
	    existing.setProjectId(dto.getProjectId());
	    
	    existing.setProjectTitle(project.getTitle()); 
	    
	    existing.setType(dto.getType());
	    existing.setQuantity(dto.getQuantity());

	    ResourceResponseDTO response = mapToResponseDTO(resourceRepository.save(existing));

	    
	    sendInternalNotification("Resource ID " + resourceId + " details updated for Project: " + project.getTitle(), 
	            "RESOURCE_UPDATE", resourceId);

	    return response;
	}

	@Override
	@Transactional
	public void deleteResource(long resourceId) {
		Resources resource = resourceRepository.findById(resourceId).orElseThrow(
				() -> new ResourceNotFoundException("Cannot delete: Resource ID " + resourceId + " does not exist."));

		if ("Allocated".equalsIgnoreCase(resource.getStatus())) {
			throw new ValidationException("Cannot delete resource: It is currently 'Allocated'.");
		}

		resourceRepository.deleteById(resourceId);

		// Trigger In-App Notification
		sendInternalNotification("Resource ID " + resourceId + " has been deleted.", "RESOURCE_DELETE", resourceId);
	}

	@Override
	public ResourceResponseDTO updateStatus(long resourceId, String status) {
		Resources resource = resourceRepository.findById(resourceId)
				.orElseThrow(() -> new ResourceNotFoundException("Resource not found."));

		if (status == null || !ALLOWED_STATUSES.contains(status)) {
			throw new ValidationException("Invalid status. Allowed: " + ALLOWED_STATUSES);
		}

		resource.setStatus(status);
		ResourceResponseDTO response = mapToResponseDTO(resourceRepository.save(resource));

		// Trigger In-App Notification
		sendInternalNotification("Resource ID " + resourceId + " status changed to " + status, "STATUS_CHANGE",
				resourceId);

		return response;
	}

	private void sendInternalNotification(String message, String category, Long entityId) {
		NotificationRequestDTO notifyReq = NotificationRequestDTO.builder().userId(4L).message(message)
				.category(category).entityId(entityId).sendEmail(true).email("hobip98770@minitts.net").build();

		notificationClient.createNotification(notifyReq);

		logger.info("Line after notification call reached successfully.");
	}

	@Override
	@Transactional(readOnly = true)
	public ResourceResponseDTO getResource(long resourceId) {
		Resources resource = resourceRepository.findById(resourceId)
				.orElseThrow(() -> new ResourceNotFoundException("Resource ID " + resourceId + " not found."));
		return mapToResponseDTO(resource);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ResourceResponseDTO> getAllResources() {
		return resourceRepository.findAll().stream().map(this::mapToResponseDTO).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ResourceResponseDTO> getResourcesByProjectId(long projectId) {
		fetchProject(projectId);
		List<Resources> resources = resourceRepository.findByProjectId(projectId);
		return resources.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
	}

	private ProjectResponseDTO fetchProject(long projectId) {
	    ResponseEntity<ProjectResponseDTO> response = projectClient.getProjectById(projectId);
	    ProjectResponseDTO project = response.getBody();

	    if (project == null || "SERVICE_FAILURE".equals(project.getStatus())) {
	        throw new ResourceNotFoundException("Project Service is currently unavailable. Please try again later.");
	    }
	    
	    return project;
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
	    
	    // Now returning the stored title
	    response.setProjectTitle(entity.getProjectTitle()); 
	    
	    response.setType(entity.getType());
	    response.setQuantity(entity.getQuantity());
	    response.setStatus(entity.getStatus());
	    return response;
	}
}