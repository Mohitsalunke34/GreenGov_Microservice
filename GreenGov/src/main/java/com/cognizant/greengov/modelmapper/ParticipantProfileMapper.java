package com.cognizant.greengov.modelmapper;

import com.cognizant.greengov.dto.profile_dto.ParticipantProfileDTO;
import com.cognizant.greengov.model.register_login.ParticipantProfile;

public class ParticipantProfileMapper {

	private ParticipantProfileMapper() {
	}

	public static ParticipantProfileDTO toDTO(ParticipantProfile entity) {
		ParticipantProfileDTO dto = new ParticipantProfileDTO();
		dto.setId(entity.getId());
		dto.setEntityType(entity.getEntityType());
		dto.setLegalName(entity.getLegalName());
		dto.setAddress(entity.getAddress());
		dto.setContactInfoJson(entity.getContactInfoJson());
		return dto;
	}
}