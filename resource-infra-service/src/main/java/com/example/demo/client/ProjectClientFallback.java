package com.example.demo.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.example.demo.dto.ProjectResponseDTO;

@Component
public class ProjectClientFallback implements ProjectClient {

    private static final Logger logger = LoggerFactory.getLogger(ProjectClientFallback.class);

    @Override
    public ResponseEntity<ProjectResponseDTO> getProjectById(Long projectId) {
        logger.error("Project Service is DOWN. Falling back for Project ID: {}", projectId);

        ProjectResponseDTO fallbackProject = new ProjectResponseDTO();
        fallbackProject.setProjectId(projectId);
        fallbackProject.setTitle("Service Unavailable");
        fallbackProject.setStatus("SERVICE_FAILURE"); 

        return new ResponseEntity<>(fallbackProject, HttpStatus.SERVICE_UNAVAILABLE);
    }
}