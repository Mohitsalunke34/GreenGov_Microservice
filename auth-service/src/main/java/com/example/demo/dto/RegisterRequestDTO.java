package com.example.demo.dto;

import com.example.demo.model.Enums.OfficerType;
import com.example.demo.model.Enums.PrimaryRole;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {

	private String username;
	private String password;
	private String email;

	private PrimaryRole primaryRole;

	// Required ONLY if primaryRole == OFFICER
	private OfficerType officerType;
	private String department;
	private String designation;
	private String officeCode;
}