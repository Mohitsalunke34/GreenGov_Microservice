package com.cognizant.greengov.modelmapper;

import com.cognizant.greengov.dto.infra_resource_dto.ResourcesResponseDTO;
import com.cognizant.greengov.model.resource_infrastructure.Resources;

public class ResourcesMapper {

	private ResourcesMapper() {
	}

	public static ResourcesResponseDTO toDTO(Resources entity) {
		ResourcesResponseDTO dto = new ResourcesResponseDTO();
		dto.setResourceId(entity.getResourceId());
		dto.setProjectId(entity.getProject().getProjectId());
		dto.setType(entity.getType());
		dto.setQuantity(entity.getQuantity());
		dto.setStatus(entity.getStatus());
		return dto;
	}
}