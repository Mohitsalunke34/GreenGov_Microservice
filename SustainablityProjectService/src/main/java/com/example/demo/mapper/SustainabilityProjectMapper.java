package com.example.demo.mapper;

import com.example.demo.dto.SustainabilityProjectResponseDto;
import com.example.demo.model.SustainabilityProject;

public final class SustainabilityProjectMapper {

	// Private constructor to prevent instantiation
	private SustainabilityProjectMapper() {
	}

	public static SustainabilityProjectResponseDto toDto(SustainabilityProject entity) {

		if (entity == null) {
			return null;
		}

		return SustainabilityProjectResponseDto.builder().projectId(entity.getProjectId()).title(entity.getTitle())
				.description(entity.getDescription()).startDate(entity.getStartDate()).endDate(entity.getEndDate())
				.budget(entity.getBudget()).status(entity.getStatus()).participantId(entity.getParticipantId()).build();
	}
}