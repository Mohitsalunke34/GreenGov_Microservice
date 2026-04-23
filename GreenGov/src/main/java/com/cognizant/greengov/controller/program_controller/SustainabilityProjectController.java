package com.cognizant.greengov.controller.program_controller;
//
//import java.util.List;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.cognizant.greengov.model.sustainability_renewable_proj.SustainabilityProject;
//import com.cognizant.greengov.service.sustainability_service.SustainabilityProjectService;
//
//@RestController
//@RequestMapping("/api/projects")
//public class SustainabilityProjectController {
//
//	private final SustainabilityProjectService projectService;
//
//	public SustainabilityProjectController(SustainabilityProjectService projectService) {
//		this.projectService = projectService;
//	}
//
//	/**
//	 * Admin / Officer creates project
//	 */
//	@PostMapping
//	public ResponseEntity<SustainabilityProject> create(@RequestBody SustainabilityProject project) {
//
//		return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(project));
//	}
//
//	/**
//	 * List projects by status (dashboard / reporting)
//	 */
//	@GetMapping
//	public ResponseEntity<List<SustainabilityProject>> getByStatus(@RequestParam String status) {
//
//		return ResponseEntity.ok(projectService.getProjectsByStatus(status));
//	}
//}



import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.greengov.dto.sustainability_dto.SustainabilityResponse;
import com.cognizant.greengov.exception.ProjectNotFound;
import com.cognizant.greengov.model.sustainability_renewable_proj.SustainabilityProject;
import com.cognizant.greengov.service.sustainability_service.SustainabilityProjectService;

import lombok.extern.slf4j.Slf4j;
 
@RestController
@RequestMapping("/api/projects")
@Slf4j
public class SustainabilityProjectController {
 
	private final SustainabilityProjectService projectService;
 
	public SustainabilityProjectController(SustainabilityProjectService projectService) {
		this.projectService = projectService;
	}
 
	
//	 Admin / Officer creates project
	@PostMapping("/save")
	public ResponseEntity<SustainabilityProject> create(@RequestBody SustainabilityProject project) {
 
		return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(project));
	}

//	  List projects by status (dashboard / reporting)
	@GetMapping("/getByStatus")
	public ResponseEntity<List<SustainabilityProject>> getByStatus(@RequestParam String status) {
 
		return ResponseEntity.ok(projectService.getProjectsByStatus(status));
	}
	// List all the projects
	@GetMapping("/fetchAll")
    public ResponseEntity<List<SustainabilityResponse>> getAll() {
        log.info("Received request to fetch all sustainability projects");
        List<SustainabilityResponse> projects = projectService.getAllProjects();
        log.info("Retrieved {} projects", projects.size());
        return ResponseEntity.ok(projects);
    }
 
	// List all projects by Id
    @GetMapping("/fetch/{projectId}")
    public ResponseEntity<SustainabilityResponse> getById(@PathVariable Long projectId) throws ProjectNotFound {
        log.info("Received request to fetch project with ID: {}", projectId);
        SustainabilityResponse response = projectService.getProjectById(projectId);
        log.debug("Found project details for ID {}: {}", projectId, response);
        return ResponseEntity.ok(response);
    }
    // Update status
    @PatchMapping("updateStatus/{projectId}/{status}")
    public ResponseEntity<SustainabilityResponse> updateStatus(@PathVariable Long projectId, @RequestParam String status) throws ProjectNotFound {
        return ResponseEntity.ok(projectService.updateProjectStatus(projectId, status));
    }
 
    // Delete project
    @DeleteMapping("/delete/{projectId}")
    public ResponseEntity<String> delete(@PathVariable Long projectId) throws ProjectNotFound {
        log.info("Received request to delete project with ID: {}", projectId);
        String message = projectService.deleteProject(projectId);
        log.info("Deletion result for ID {}: {}", projectId, message);
        return ResponseEntity.ok(message);
    }
}