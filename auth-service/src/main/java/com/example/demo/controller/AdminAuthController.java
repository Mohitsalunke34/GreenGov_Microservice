package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ErrorResponseDTO;
import com.example.demo.service.AuthService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

	private final AuthService service;

	public AdminAuthController(AuthService service) {
		this.service = service;
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {

		log.info("Admin login attempt: {}", username);

		try {
			return ResponseEntity.ok(Map.of("token", service.adminLogin(username, password)));
		} catch (RuntimeException ex) {
			log.warn("Admin login failed: {}", ex.getMessage());
			return ResponseEntity.status(401).body(new ErrorResponseDTO(ex.getMessage(), LocalDateTime.now()));
		}
	}
}