package com.cognizant.greengov.dto.profile_dto;

import com.cognizant.greengov.model.Enums.EntityType;

import lombok.Data;

@Data
public class ParticipantProfileDTO {

	private Long id;
	private EntityType entityType;
	private String legalName;
	private String address;
	private String contactInfoJson;
}