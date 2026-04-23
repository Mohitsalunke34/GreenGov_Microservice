package com.cognizant.greengov.service.admin_service;

import com.cognizant.greengov.dto.AdminDTO.AdminLoginRequestDTO;
import com.cognizant.greengov.dto.AdminDTO.AdminLoginResponseDTO;

public interface AdminAuthService {
	AdminLoginResponseDTO login(AdminLoginRequestDTO dto);
}