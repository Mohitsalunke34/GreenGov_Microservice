//package com.cognizant.greengov.service.sustainability_service;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.cognizant.greengov.model.sustainability_renewable_proj.SustainabilityProject;
//import com.cognizant.greengov.repository.sustainability_repo.SustainabilityProjectRepository;
//
//@Service
//@Transactional
//public class SustainabilityProjectServiceImpl implements SustainabilityProjectService {
//
//	private final SustainabilityProjectRepository projectRepo;
//
//	public SustainabilityProjectServiceImpl(SustainabilityProjectRepository projectRepo) {
//		this.projectRepo = projectRepo;
//	}
//
//	@Override
//	public SustainabilityProject createProject(SustainabilityProject project) {
//		project.setStatus("PLANNED");
//		project.setStartDate(LocalDate.now());
//		return projectRepo.save(project);
//	}
//
//	@Override
//	public List<SustainabilityProject> getProjectsByStatus(String status) {
//		return projectRepo.findByStatus(status);
//	}
//}

package com.cognizant.greengov.service.sustainability_service;
 
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cognizant.greengov.dto.sustainability_dto.SustainabilityResponse;
import com.cognizant.greengov.exception.ProjectNotFound;
import com.cognizant.greengov.model.sustainability_renewable_proj.SustainabilityProject;
import com.cognizant.greengov.repository.sustainability_repo.SustainabilityProjectRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
 
@Service

@Transactional

@Slf4j

@AllArgsConstructor

public class SustainabilityProjectServiceImpl implements SustainabilityProjectService {
 
	private final SustainabilityProjectRepository projectRepo;
 
	

	 /* Creates a new project in the system.

	    Sets the initial status to 'PLANNED' and the start date to current system date.*/ 

	@Override

	public SustainabilityProject createProject(SustainabilityProject project) {

		project.setStatus("PLANNED");

		project.setStartDate(LocalDate.now());

		return projectRepo.save(project);

	}
 
	/* Retrieves a list of projects filtered by their current status (e.g., PLANNED, APPROVED).*/

	@Override

	public List<SustainabilityProject> getProjectsByStatus(String status) {

		return projectRepo.findByStatus(status);

	}
 
	/*Updates the status of an existing project.

	 * Logic: If the status is 'APPROVED', the project is cleared for 

	 * Resource and Infrastructure allocation phases.*/

	@Override

	public SustainabilityResponse updateProjectStatus(Long projectId, String status) throws ProjectNotFound {

		log.info("Updating status for project ID: {} to {}", projectId, status);

		SustainabilityProject existing = projectRepo.findById(projectId)

				.orElseThrow(() -> new ProjectNotFound("Project not found with ID: " + projectId));
 
		// Condition: Only pass for resource and infrastructure if status is APPROVED

		if ("APPROVED".equalsIgnoreCase(status)) {

			log.info("Project {} APPROVED: Initiating Resource and Infrastructure allocation.", projectId);

		} else {

			log.info("Project {} is status: {}. Resource/Infrastructure allocation skipped.", projectId, status);

		}
 
		existing.setStatus(status);

		return mapToResponse(projectRepo.save(existing));

	}
 
	/*Fetches all sustainability projects from the database and converts them to DTOs.*/

	@Override

	public List<SustainabilityResponse> getAllProjects() {

		log.debug("Fetching all project records from the repository");

		return projectRepo.findAll().stream().map(this::mapToResponse).toList();

	}
 
	/*Finds a specific project by its unique ID.*/

	@Override

	public SustainabilityResponse getProjectById(Long projectId) throws ProjectNotFound {

		log.debug("Searching for project ID: {}", projectId);

		SustainabilityProject project = projectRepo.findById(projectId).orElseThrow(() -> {

			log.warn("Project search failed: ID {} not found", projectId);

			return new ProjectNotFound("Project with ID " + projectId + " not found.");

		});

		return mapToResponse(project);

	}
 
	/*Deletes a project record based on ID.

	 * Checks for existence before attempting deletion to avoid empty result data access exceptions.*/

	@Override

	public String deleteProject(Long projectId) throws ProjectNotFound {

		log.warn("Attempting to delete project ID: {}", projectId);

		if (!projectRepo.existsById(projectId)) {

			log.error("Deletion failed: ID {} does not exist", projectId);

			throw new ProjectNotFound("Cannot delete. Project with ID " + projectId + " not found.");

		}
 
		projectRepo.deleteById(projectId);

		log.info("Successfully deleted project ID: {}", projectId);

		return "Project with ID " + projectId + " has been deleted successfully.";

	}

	/*Private helper method to convert Entity to Response DTO.

	 * Uses the Builder pattern from Lombok for clean object instantiation.*/

	private SustainabilityResponse mapToResponse(SustainabilityProject entity) {

		return SustainabilityResponse.builder().projectId(entity.getProjectId()).title(entity.getTitle())

				.description(entity.getDescription()).startDate(entity.getStartDate()).endDate(entity.getEndDate())

				.budget(entity.getBudget()).status(entity.getStatus()).build();

	}

}
 