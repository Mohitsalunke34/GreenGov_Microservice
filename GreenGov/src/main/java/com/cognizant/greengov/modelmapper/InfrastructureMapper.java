package com.cognizant.greengov.modelmapper;

import com.cognizant.greengov.dto.infra_resource_dto.InfrastructureResponseDTO;
import com.cognizant.greengov.model.resource_infrastructure.Infrastructure;

public class InfrastructureMapper {

	private InfrastructureMapper() {
	}

	public static InfrastructureResponseDTO toDTO(Infrastructure entity) {
		InfrastructureResponseDTO dto = new InfrastructureResponseDTO();
		dto.setInfraId(entity.getInfraId());
		dto.setProjectId(entity.getProject().getProjectId());
		dto.setType(entity.getType());
		dto.setLocation(entity.getLocation());
		dto.setCapacity(entity.getCapacity());
		dto.setStatus(entity.getStatus());
		return dto;
	}
}