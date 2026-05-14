package com.example.demo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.dto.ProjectResponseDTO;

@FeignClient(name = "SUSTAINABILITYPROJECTSERVICE", fallback = ProjectClientFallback.class)
public interface ProjectClient {

	@GetMapping("/api/projects/{projectId}")
	ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable("projectId") Long projectId);
}