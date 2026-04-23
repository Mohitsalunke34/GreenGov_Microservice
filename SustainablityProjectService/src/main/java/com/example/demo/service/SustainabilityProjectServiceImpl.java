package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.client.NotificationClient; // Added
import com.example.demo.client.ParticipantStatusClient;
import com.example.demo.dto.NotificationRequestDTO; // Added
import com.example.demo.dto.SustainabilityProjectRequestDto;
import com.example.demo.dto.SustainabilityProjectResponseDto;
import com.example.demo.exception.ProjectNotFound;
import com.example.demo.mapper.SustainabilityProjectMapper;
import com.example.demo.model.SustainabilityProject;
import com.example.demo.repository.SustainabilityProjectRepo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class SustainabilityProjectServiceImpl implements SustainabilityProjectService {

	private final SustainabilityProjectRepo projectRepo;
	private final ParticipantStatusClient participantStatusClient;
	private final NotificationClient notificationClient; // Added

	/* ================= CREATE ================= */

	@Override
	public SustainabilityProjectResponseDto createProject(SustainabilityProjectRequestDto request) {

	    Long participantId = request.getParticipantId();

	    if (participantId == null) {
	        throw new IllegalArgumentException("Participant ID must not be null");
	    }

	    log.info("Verifying participantId = {}", participantId);

	    boolean isVerified = participantStatusClient.isVerified(participantId);

	    if (!isVerified) {
	        throw new IllegalStateException("Only VERIFIED participants are allowed to create projects");
	    }

	    // ✅ CREATE PROJECT ENTITY
	    SustainabilityProject project = new SustainabilityProject();
	    project.setTitle(request.getTitle());
	    project.setDescription(request.getDescription());
	    project.setStartDate(request.getStartDate() != null ? request.getStartDate() : LocalDate.now());
	    project.setEndDate(request.getEndDate());
	    project.setBudget(request.getBudget());
	    project.setStatus("PLANNED");
	    
	    // ⬇️ ADD THIS LINE ⬇️
	    project.setParticipantId(participantId); 

	    SustainabilityProject saved = projectRepo.save(project);

	    // Trigger Notification
	    sendInternalNotification(
	        "New project created: " + saved.getTitle(), 
	        "PROJECT_CREATION", 
	        saved.getProjectId()
	    );

	    return SustainabilityProjectMapper.toDto(saved);
	}

	/* ================= READ ================= */

	@Override
	@Transactional(readOnly = true)
	public List<SustainabilityProjectResponseDto> getProjectsByStatus(String status) {
		log.debug("Fetching projects with status {}", status);
		return projectRepo.findByStatus(status).stream().map(SustainabilityProjectMapper::toDto).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public SustainabilityProjectResponseDto getProjectById(Long projectId) throws ProjectNotFound {
		SustainabilityProject project = projectRepo.findById(projectId)
				.orElseThrow(() -> new ProjectNotFound("Project not found with ID: " + projectId));
		return SustainabilityProjectMapper.toDto(project);
	}

	@Override
	@Transactional(readOnly = true)
	public List<SustainabilityProjectResponseDto> getAllProjects() {
		log.debug("Fetching all sustainability projects");
		return projectRepo.findAll().stream().map(SustainabilityProjectMapper::toDto).toList();
	}

	/* ================= UPDATE ================= */

	@Override
	public SustainabilityProjectResponseDto updateProjectStatus(Long projectId, String status) throws ProjectNotFound {

		log.info("Updating status of project {} to {}", projectId, status);

		SustainabilityProject project = projectRepo.findById(projectId)
				.orElseThrow(() -> new ProjectNotFound("Project not found with ID: " + projectId));

		project.setStatus(status);
		SustainabilityProject updated = projectRepo.save(project);

		if ("APPROVED".equalsIgnoreCase(status)) {
			log.info("Project {} APPROVED – eligible for resource/infrastructure allocation", projectId);
		}

		// Trigger Notification
		sendInternalNotification(
			"Project status updated to " + status, 
			"STATUS_CHANGE", 
			projectId
		);

		return SustainabilityProjectMapper.toDto(updated);
	}

	/* ================= DELETE ================= */

	@Override
	public String deleteProject(Long projectId) throws ProjectNotFound {
		if (!projectRepo.existsById(projectId)) {
			throw new ProjectNotFound("Cannot delete. Project not found with ID: " + projectId);
		}
		projectRepo.deleteById(projectId);
		log.warn("Project {} deleted", projectId);
		return "Project with ID " + projectId + " has been deleted successfully.";
	}

	@Override
	@Transactional(readOnly = true)
	public boolean projectExists(Long projectId) {
		return projectRepo.existsById(projectId);
	}

	/* ================= HELPER METHODS ================= */

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
			log.info("Notification successfully sent to Notification-Service");
		} catch (Exception e) {
			log.error("DETAILED NOTIFICATION ERROR: ", e);
		}
	}
}