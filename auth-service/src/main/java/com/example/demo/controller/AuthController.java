package com.example.demo.controller;

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

import com.example.demo.dto.RegisterRequestDTO;
import com.example.demo.dto.RegisterResponseDTO;
import com.example.demo.dto.UserProfileDTO;
import com.example.demo.dto.client.UserBasicDTO;
import com.example.demo.model.Enums.PrimaryRole;
import com.example.demo.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService service;

	public AuthController(AuthService service) {
		this.service = service;
	}

	// Register
	@PostMapping("/register")
	public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequestDTO request) {

		service.register(request);

		String msg = request.getPrimaryRole() == PrimaryRole.OFFICER ? "Registration successful. Await admin approval."
				: "Registration successful. You can now login.";

		return ResponseEntity.ok(new RegisterResponseDTO(msg));
	}

	@PostMapping("/login")
	public Map<String, String> login(@RequestParam String username, @RequestParam String password) {
		return Map.of("token", service.userLogin(username, password));
	}

	@GetMapping("/findAllCitizenAndBusiness")
	public List<UserProfileDTO> getUserByPrimaryRole() {
		return service.getUserByPrimaryRole();
	}

	/**
	 * BASIC USER INFO Used by other microservices via Feign
	 */
	@GetMapping("/users/{id}/basic")
	public ResponseEntity<UserBasicDTO> getUserBasic(@PathVariable Long id) {

		return ResponseEntity.ok(service.getUserBasicById(id));
	}
}