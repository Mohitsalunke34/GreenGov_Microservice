package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ErrorResponseDTO;
import com.example.demo.dto.RegisterRequestDTO;
import com.example.demo.dto.RegisterResponseDTO;
import com.example.demo.dto.UserProfileDTO;
import com.example.demo.dto.client.UserBasicDTO;
import com.example.demo.model.Enums.PrimaryRole;
import com.example.demo.service.AuthService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService service;

	public AuthController(AuthService service) {
		this.service = service;
	}

	// ---------------- REGISTER ----------------
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {

		log.info("Registration request for username={}", request.getUsername());

		try {
			Long userId = service.register(request);

			String msg = request.getPrimaryRole() == PrimaryRole.OFFICER
					? "Registration successful. Await admin approval."
					: "Registration successful. You can now login.";

			return ResponseEntity.status(201).body(new RegisterResponseDTO(userId, msg));

		} catch (IllegalArgumentException ex) {
			log.warn("Registration failed: {}", ex.getMessage());
			return ResponseEntity.status(409).body(new ErrorResponseDTO(ex.getMessage(), LocalDateTime.now()));

		} catch (RuntimeException ex) {
			log.error("Unexpected registration error", ex);
			return ResponseEntity.status(400).body(new ErrorResponseDTO(ex.getMessage(), LocalDateTime.now()));
		}
	}

	// ---------------- USER LOGIN ----------------
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {

		log.info("Login attempt for username={}", username);

		try {
			String token = service.userLogin(username, password);
			return ResponseEntity.ok(Map.of("token", token));

		} catch (RuntimeException ex) {
			log.warn("Login failed for username={}: {}", username, ex.getMessage());
			return ResponseEntity.status(401).body(new ErrorResponseDTO(ex.getMessage(), LocalDateTime.now()));
		}
	}

	// ---------------- FETCH USERS ----------------
	@GetMapping("/findAllCitizenAndBusiness")
	public ResponseEntity<List<UserProfileDTO>> getUserByPrimaryRole() {
		log.info("Fetching citizen and business users");
		return ResponseEntity.ok(service.getUserByPrimaryRole());
	}

	// ---------------- BASIC USER INFO (Feign) ----------------
	@GetMapping("/users/{id}/basic")
	public ResponseEntity<?> getUserBasic(@PathVariable Long id) {

		log.info("Fetching basic user info for id={}", id);

		try {
			UserBasicDTO dto = service.getUserBasicById(id);
			return ResponseEntity.ok(dto);

		} catch (RuntimeException ex) {
			log.warn("User not found: {}", ex.getMessage());
			return ResponseEntity.status(404).body(new ErrorResponseDTO(ex.getMessage(), LocalDateTime.now()));
		}
	}
}
