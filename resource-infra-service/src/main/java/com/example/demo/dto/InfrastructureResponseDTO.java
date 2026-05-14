package com.example.demo.dto;

import lombok.Data;

@Data
public class InfrastructureResponseDTO {

	private Long infraId;

	// Mapping reference
	private Long projectId;
	private String projectTitle;
	// Infrastructure details
	private String type;
	private String location;
	private Integer capacity;

	// Lifecycle
	private String status;
}