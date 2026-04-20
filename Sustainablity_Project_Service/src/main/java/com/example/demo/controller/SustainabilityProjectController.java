package com.example.demo.controller;

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

import com.example.demo.dto.SustainabilityProjectRequestDto;
import com.example.demo.dto.SustainabilityProjectResponseDto;
import com.example.demo.exception.ProjectNotFound;
import com.example.demo.service.SustainabilityProjectService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/projects")
@Slf4j
public class SustainabilityProjectController {

	private final SustainabilityProjectService projectService;

	public SustainabilityProjectController(SustainabilityProjectService projectService) {
		this.projectService = projectService;
	}

	/* ================= CREATE ================= */

	@PostMapping("/create")
	public ResponseEntity<SustainabilityProjectResponseDto> createProject(
			@Valid @RequestBody SustainabilityProjectRequestDto request) {

		log.info("REST request to create Sustainability Project: {}", request.getTitle());

		SustainabilityProjectResponseDto response = projectService.createProject(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/* ================= READ ================= */

	@GetMapping("/fetchAll")
	public ResponseEntity<List<SustainabilityProjectResponseDto>> getAllProjects() {

		log.info("REST request to fetch all sustainability projects");
		return ResponseEntity.ok(projectService.getAllProjects());
	}

	@GetMapping(params = "status")
	public ResponseEntity<List<SustainabilityProjectResponseDto>> getProjectsByStatus(@RequestParam String status) {

		log.info("REST request to fetch projects with status {}", status);
		return ResponseEntity.ok(projectService.getProjectsByStatus(status));
	}

	@GetMapping("/fetchById/{projectId}")
	public ResponseEntity<SustainabilityProjectResponseDto> getProjectById(@PathVariable Long projectId)
			throws ProjectNotFound {

		log.info("REST request to fetch project ID {}", projectId);
		return ResponseEntity.ok(projectService.getProjectById(projectId));
	}

	/* ================= UPDATE ================= */

	@PatchMapping("/updateByStatus/{projectId}/status")
	public ResponseEntity<SustainabilityProjectResponseDto> updateStatus(@PathVariable Long projectId,
			@RequestParam String status) throws ProjectNotFound {

		log.info("REST request to update status of project {} to {}", projectId, status);
		return ResponseEntity.ok(projectService.updateProjectStatus(projectId, status));
	}

	/* ================= DELETE ================= */

	@DeleteMapping("/deleteById/{projectId}")
	public ResponseEntity<String> deleteProject(@PathVariable Long projectId) throws ProjectNotFound {

		log.warn("REST request to delete project ID {}", projectId);
		return ResponseEntity.ok(projectService.deleteProject(projectId));
	}
}