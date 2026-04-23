package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.RegisterRequestDTO;
import com.example.demo.dto.UserProfileDTO;
import com.example.demo.dto.client.UserBasicDTO;

public interface AuthService {

	String userLogin(String username, String password);

	String adminLogin(String username, String password);

	Long register(RegisterRequestDTO request);

	List<UserProfileDTO> getUserByPrimaryRole();

	UserBasicDTO getUserBasicById(Long userId);
}