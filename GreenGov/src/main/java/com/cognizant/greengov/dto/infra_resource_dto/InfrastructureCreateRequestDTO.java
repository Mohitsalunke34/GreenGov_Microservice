package com.cognizant.greengov.dto.infra_resource_dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InfrastructureCreateRequestDTO {

	@NotNull
	private Long projectId;

	@NotBlank
	private String type;

	@NotBlank
	private String location;

	@NotNull
	private Integer capacity;
}