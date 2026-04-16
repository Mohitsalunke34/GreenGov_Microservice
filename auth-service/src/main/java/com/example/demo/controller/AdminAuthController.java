package com.example.demo.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.AuthService;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

	private final AuthService service;

	public AdminAuthController(AuthService service) {
		this.service = service;
	}

	@PostMapping("/login")
	public Map<String, String> login(@RequestParam String username, @RequestParam String password) {
		return Map.of("token", service.adminLogin(username, password));
	}

}