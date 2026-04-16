package com.example.demo.service;

import com.example.demo.dto.RegisterRequestDTO;

public interface AuthService {

	String userLogin(String username, String password);

	String adminLogin(String username, String password);

	void register(RegisterRequestDTO request);
}