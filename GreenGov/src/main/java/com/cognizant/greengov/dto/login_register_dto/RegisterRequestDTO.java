package com.cognizant.greengov.dto.login_register_dto;

import com.cognizant.greengov.model.Enums.EntityType;
import com.cognizant.greengov.model.Enums.OfficerType;
import com.cognizant.greengov.model.Enums.PrimaryRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequestDTO {

	// ---- Common ----
	@NotBlank
	private String username;

	@NotBlank
	private String password;

	@NotBlank
	private String email;

	@NotNull
	private PrimaryRole primaryRole;

	// ---- Citizen / Business ----
	private EntityType entityType;
	private String legalName;
	private String address;
	private String contactInfoJson;

	// ---- Officer ----
	private OfficerType officerType;
	private String department;
	private String designation;
	private String officeCode;
}