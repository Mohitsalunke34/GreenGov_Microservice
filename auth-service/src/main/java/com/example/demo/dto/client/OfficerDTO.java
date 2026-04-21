package com.example.demo.dto.client;

import lombok.Data;

@Data
public class OfficerDTO {

	private Long userId;
	private String username;
	private String officerType;
	private String department;
	private String designation;
}