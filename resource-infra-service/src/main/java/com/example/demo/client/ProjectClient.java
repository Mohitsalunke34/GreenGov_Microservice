package com.example.demo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.dto.ProjectResponseDTO;


@FeignClient(name = "SUSTAINABILITYPROJECTSERVICE") 
public interface ProjectClient {

	/**
	 * Calls the GET endpoint of the Sustainability Project service to fetch details.
	 * * @param id The project ID to look up.
	 * @return ResponseEntity containing the ProjectResponseDTO.
	 */
	@GetMapping("/api/projects/{projectId}")
	ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable("projectId") Long projectId);
}