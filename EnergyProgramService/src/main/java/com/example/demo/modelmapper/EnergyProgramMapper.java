package com.example.demo.modelmapper;

import com.example.demo.dto.EnergyProgramResponseDto;
import com.example.demo.model.EnergyProgram;

public final class EnergyProgramMapper {

	private EnergyProgramMapper() {
	}

	public static EnergyProgramResponseDto toDto(EnergyProgram entity) {
		if (entity == null) {
			return null;
		}

		return new EnergyProgramResponseDto(entity.getProgramId(), entity.getTitle(), entity.getDescription(),
				entity.getStartDate(), entity.getEndDate(), entity.getBudget(), entity.getRemainingProgramBudget(), // ✅
																													// ADD
																													// THIS
				entity.getStatus());
	}
}
