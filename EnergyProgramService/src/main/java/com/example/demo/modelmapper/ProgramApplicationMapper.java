package com.example.demo.modelmapper;

import com.example.demo.dto.ProgramApplicationResponseDto;
import com.example.demo.model.ProgramApplication;

public final class ProgramApplicationMapper {

	private ProgramApplicationMapper() {
		// Prevent instantiation
	}

	public static ProgramApplicationResponseDto toDto(ProgramApplication entity) {

		if (entity == null) {
			return null;
		}

		return new ProgramApplicationResponseDto(entity.getApplicationId(), entity.getApplicantId(),
				entity.getProgram().getProgramId(), entity.getSubmittedDate(),
				(entity.getProgram() != null) ? entity.getProgram().getTitle() : "Unknown Program", entity.getStatus());
	}
}