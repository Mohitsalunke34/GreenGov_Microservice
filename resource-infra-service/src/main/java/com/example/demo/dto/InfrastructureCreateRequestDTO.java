package com.example.demo.dto;

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