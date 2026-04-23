package com.cognizant.greengov.service.register_login_service;

import com.cognizant.greengov.dto.login_register_dto.LoginRequestDTO;
import com.cognizant.greengov.dto.login_register_dto.LoginResponseDTO;
import com.cognizant.greengov.dto.login_register_dto.RegisterRequestDTO;

public interface AuthService {

	void register(RegisterRequestDTO dto);

	LoginResponseDTO login(LoginRequestDTO dto);
}