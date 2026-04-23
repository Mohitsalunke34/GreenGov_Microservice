package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.SustainabilityProjectRequestDto;
import com.example.demo.dto.SustainabilityProjectResponseDto;
import com.example.demo.exception.ProjectNotFound;

public interface SustainabilityProjectService {

	SustainabilityProjectResponseDto createProject(SustainabilityProjectRequestDto request);

	List<SustainabilityProjectResponseDto> getProjectsByStatus(String status);

	SustainabilityProjectResponseDto updateProjectStatus(Long projectId, String status) throws ProjectNotFound;

	SustainabilityProjectResponseDto getProjectById(Long projectId) throws ProjectNotFound;

	List<SustainabilityProjectResponseDto> getAllProjects();

	String deleteProject(Long projectId) throws ProjectNotFound;

	boolean projectExists(Long projectId);

}
