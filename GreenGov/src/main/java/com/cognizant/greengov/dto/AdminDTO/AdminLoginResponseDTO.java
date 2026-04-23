package com.cognizant.greengov.dto.AdminDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminLoginResponseDTO {
	private Long adminId;
	private String username;
	private String token; // JWT
}