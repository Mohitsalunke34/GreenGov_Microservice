package com.example.demo.dto;

import lombok.Data;

@Data
public class ParticipantBasicDTO {
	private Long id;
	private String legalName;
	private boolean verified;
}