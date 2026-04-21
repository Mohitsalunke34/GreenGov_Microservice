package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.InfrastructureCreateRequestDTO;
import com.example.demo.dto.InfrastructureResponseDTO;
import com.example.demo.dto.InfrastructureStatusDTO;

public interface InfrastructureService {
    InfrastructureResponseDTO addInfrastructure(InfrastructureCreateRequestDTO dto);
    InfrastructureResponseDTO updateInfrastructure(long infraId, InfrastructureCreateRequestDTO dto);
    InfrastructureResponseDTO updateStatus(InfrastructureStatusDTO dto);
    InfrastructureResponseDTO getInfrastructure(long infraId);
    List<InfrastructureResponseDTO> getAllInfrastructure();
    void deleteInfrastructure(long infraId);
}