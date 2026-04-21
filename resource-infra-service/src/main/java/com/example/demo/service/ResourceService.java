package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.ResourceCreateRequestDTO;
import com.example.demo.dto.ResourceResponseDTO;

public interface ResourceService {
	ResourceResponseDTO addResource(ResourceCreateRequestDTO dto);

	ResourceResponseDTO updateResource(long resourceId, ResourceCreateRequestDTO dto);

	ResourceResponseDTO getResource(long resourceId);

	List<ResourceResponseDTO> getAllResources();

	void deleteResource(long resourceId);

	ResourceResponseDTO updateStatus(long resourceId, String status);

	List<ResourceResponseDTO> getResourcesByProjectId(long projectId);
}
