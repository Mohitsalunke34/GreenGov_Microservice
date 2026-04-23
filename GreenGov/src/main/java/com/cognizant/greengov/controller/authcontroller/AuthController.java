package com.cognizant.greengov.controller.authcontroller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.greengov.dto.login_register_dto.LoginRequestDTO;
import com.cognizant.greengov.dto.login_register_dto.LoginResponseDTO;
import com.cognizant.greengov.dto.login_register_dto.RegisterRequestDTO;
import com.cognizant.greengov.service.register_login_service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequestDTO dto) {
		authService.register(dto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
		return ResponseEntity.ok(authService.login(dto));
	}
}