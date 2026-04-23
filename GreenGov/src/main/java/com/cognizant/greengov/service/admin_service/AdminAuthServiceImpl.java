package com.cognizant.greengov.service.admin_service;

import java.util.Map;
import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cognizant.greengov.dto.AdminDTO.AdminLoginRequestDTO;
import com.cognizant.greengov.dto.AdminDTO.AdminLoginResponseDTO;
import com.cognizant.greengov.model.register_login.Admin;
import com.cognizant.greengov.repository.AdminRepository; // or repository.admin.AdminRepository
import com.cognizant.greengov.security.JwtService;

@Service
@Transactional(readOnly = true)
public class AdminAuthServiceImpl implements AdminAuthService {

    private final AdminRepository adminRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AdminAuthServiceImpl(AdminRepository adminRepo,
                                PasswordEncoder passwordEncoder,
                                JwtService jwtService) {
        this.adminRepo = adminRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AdminLoginResponseDTO login(AdminLoginRequestDTO dto) {

        Admin admin = adminRepo.findByUsername(dto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid admin credentials"));

        if (!admin.isActive()) {
            throw new IllegalStateException("Admin is inactive");
        }

        
        if (!Objects.equals(dto.getPassword(), admin.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid admin credentials");
        }

        String token = jwtService.generateToken(
                admin.getUsername(),
                Map.of("role", "ADMIN")   // minimal claim
        );

        return new AdminLoginResponseDTO(admin.getId(), admin.getUsername(), token);
    }
}