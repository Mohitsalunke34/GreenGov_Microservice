package com.cognizant.greengov.dto.infra_resource_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceStatusDTO {
	private long resourceId;
	private String status;
}

