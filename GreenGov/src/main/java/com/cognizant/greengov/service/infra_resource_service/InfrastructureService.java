package com.cognizant.greengov.service.infra_resource_service;

import java.util.List;

import com.cognizant.greengov.dto.infra_resource_dto.InfrastructureCreateRequestDTO;
import com.cognizant.greengov.dto.infra_resource_dto.InfrastructureResponseDTO;
import com.cognizant.greengov.dto.infra_resource_dto.InfrastructureStatusDTO;

public interface InfrastructureService {
    InfrastructureResponseDTO addInfrastructure(InfrastructureCreateRequestDTO dto);
    InfrastructureResponseDTO updateInfrastructure(long infraId, InfrastructureCreateRequestDTO dto);
    InfrastructureResponseDTO updateStatus(InfrastructureStatusDTO dto);
    InfrastructureResponseDTO getInfrastructure(long infraId);
    List<InfrastructureResponseDTO> getAllInfrastructure();
    void deleteInfrastructure(long infraId);
}