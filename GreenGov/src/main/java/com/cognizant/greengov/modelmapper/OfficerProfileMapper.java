package com.cognizant.greengov.modelmapper;

import com.cognizant.greengov.dto.profile_dto.OfficerProfileDTO;
import com.cognizant.greengov.model.register_login.OfficerProfile;

public class OfficerProfileMapper {

	private OfficerProfileMapper() {
	}

	public static OfficerProfileDTO toDTO(OfficerProfile entity) {
		OfficerProfileDTO dto = new OfficerProfileDTO();
		dto.setId(entity.getId());
		dto.setUserId(entity.getUser().getId());
		dto.setOfficerType(entity.getOfficerType());
		dto.setDepartment(entity.getDepartment());
		dto.setDesignation(entity.getDesignation());
		dto.setOfficeCode(entity.getOfficeCode());
		dto.setStatus(entity.getStatus());
		dto.setSubmittedAt(entity.getSubmittedAt());
		dto.setApprovedAt(entity.getApprovedAt());
		return dto;
	}
}