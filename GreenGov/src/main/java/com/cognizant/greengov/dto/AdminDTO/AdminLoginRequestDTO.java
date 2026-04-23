package com.cognizant.greengov.dto.AdminDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminLoginRequestDTO {
	@NotBlank
	private String username;
	@NotBlank
	private String password;
}