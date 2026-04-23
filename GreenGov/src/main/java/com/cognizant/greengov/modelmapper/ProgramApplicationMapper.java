package com.cognizant.greengov.modelmapper;

import com.cognizant.greengov.dto.sustainability_dto.ProgramApplicationResponseDTO;
import com.cognizant.greengov.model.sustainability_renewable_proj.ProgramApplication;

public class ProgramApplicationMapper {

	private ProgramApplicationMapper() {
	}

	public static ProgramApplicationResponseDTO toDTO(ProgramApplication entity) {
		ProgramApplicationResponseDTO dto = new ProgramApplicationResponseDTO();
		dto.setApplicationId(entity.getApplicationId());
		dto.setProgramId(entity.getProgram().getProgramId());
		dto.setApplicantId(entity.getApplicant().getId());
		dto.setSubmittedDate(entity.getSubmittedDate());
		dto.setStatus(entity.getStatus());
		return dto;
	}
}