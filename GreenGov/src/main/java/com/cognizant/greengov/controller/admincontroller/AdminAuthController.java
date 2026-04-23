package com.cognizant.greengov.controller.admincontroller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.greengov.dto.AdminDTO.AdminLoginRequestDTO;
import com.cognizant.greengov.dto.AdminDTO.AdminLoginResponseDTO;
import com.cognizant.greengov.service.admin_service.AdminAuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

	private final AdminAuthService service;

	public AdminAuthController(AdminAuthService service) {
		this.service = service;
	}

	@PostMapping("/login")
	public ResponseEntity<AdminLoginResponseDTO> login(@RequestBody @Valid AdminLoginRequestDTO dto) {
		return ResponseEntity.ok(service.login(dto));
	}
}