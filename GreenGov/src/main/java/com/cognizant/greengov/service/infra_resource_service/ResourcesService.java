package com.cognizant.greengov.service.infra_resource_service;

import java.util.List;
import com.cognizant.greengov.dto.infra_resource_dto.ResourcesCreateRequestDTO;
import com.cognizant.greengov.dto.infra_resource_dto.ResourcesResponseDTO;

public interface ResourcesService {
    ResourcesResponseDTO addResource(ResourcesCreateRequestDTO dto);
    ResourcesResponseDTO updateResource(long resourceId, ResourcesCreateRequestDTO dto);
    ResourcesResponseDTO getResource(long resourceId);
    List<ResourcesResponseDTO> getAllResources();
    void deleteResource(long resourceId);
    ResourcesResponseDTO updateStatus(long resourceId, String status);
}