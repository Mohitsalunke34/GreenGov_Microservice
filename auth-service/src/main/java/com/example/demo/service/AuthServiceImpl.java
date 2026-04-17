package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.RegisterRequestDTO;
import com.example.demo.dto.UserProfileDTO;
import com.example.demo.model.Admin;
import com.example.demo.model.Enums.PrimaryRole;
import com.example.demo.model.Enums.ProfileStatus;
import com.example.demo.model.OfficerProfile;
import com.example.demo.model.UserAccount;
import com.example.demo.repository.AdminRepo;
import com.example.demo.repository.OfficerProfileRepo;
import com.example.demo.repository.UserAccountRepo;
import com.example.demo.security.JwtService;

import jakarta.transaction.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

	private final UserAccountRepo userRepo;
	private final OfficerProfileRepo officerRepo;
	private final AdminRepo adminRepo;
	private final PasswordEncoder encoder;
	private final JwtService jwtService;

	public AuthServiceImpl(UserAccountRepo userRepo, AdminRepo adminRepo, PasswordEncoder encoder,
			JwtService jwtService, OfficerProfileRepo officerRepo) {
		this.userRepo = userRepo;
		this.adminRepo = adminRepo;
		this.encoder = encoder;
		this.jwtService = jwtService;
		this.officerRepo = officerRepo;
	}

	@Override
	@Transactional
	public void register(RegisterRequestDTO request) {

		if (userRepo.findByUsername(request.getUsername()).isPresent()) {
			throw new IllegalArgumentException("Username already exists");
		}

		if (userRepo.findByEmail(request.getEmail()).isPresent()) {
			throw new IllegalArgumentException("Email already exists");
		}

		boolean active = request.getPrimaryRole() != PrimaryRole.OFFICER;

		UserAccount user = UserAccount.builder().username(request.getUsername()).email(request.getEmail())
				.passwordHash(encoder.encode(request.getPassword())).primaryRole(request.getPrimaryRole())
				.active(active).build();

		userRepo.save(user);

		// Officer-specific logic
		if (request.getPrimaryRole() == PrimaryRole.OFFICER) {

			if (request.getOfficerType() == null) {
				throw new RuntimeException("Officer type is required");
			}

			OfficerProfile profile = OfficerProfile.builder().user(user).officerType(request.getOfficerType())
					.department(request.getDepartment()).designation(request.getDesignation())
					.status(ProfileStatus.PENDING).build();

			officerRepo.save(profile);
		}
	}

	@Override
	public String userLogin(String username, String password) {

		UserAccount user = userRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

		if (!user.isActive() || !encoder.matches(password, user.getPasswordHash())) {
			throw new RuntimeException("Invalid credentials");
		}

		List<String> roles = List.of("ROLE_" + user.getPrimaryRole().name());
		List<String> authorities = new ArrayList<>();

		if (user.getPrimaryRole() == PrimaryRole.OFFICER) {
			OfficerProfile profile = user.getOfficerProfile();
			if (profile == null || profile.getStatus() != ProfileStatus.APPROVED) {
				throw new RuntimeException("Officer not approved");
			}
			authorities.add(profile.getOfficerType().name());
		}

		user.setLastLoginAt(LocalDateTime.now());
		userRepo.save(user);

		Map<String, Object> claims = new HashMap<>();
		claims.put("roles", roles);
		claims.put("authorities", authorities);

		return jwtService.generateToken(username, claims);
	}

	@Override
	public String adminLogin(String username, String password) {

		Admin admin = adminRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("Admin not found"));

		if (!admin.isActive() || !encoder.matches(password, admin.getPasswordHash())) {
			throw new RuntimeException("Invalid admin credentials");
		}

		Map<String, Object> claims = Map.of("roles", List.of("ROLE_ADMIN"), "authorities", List.of("ADMIN"));

		return jwtService.generateToken(username, claims);
	}

	@Override
	public List<UserProfileDTO> getUserByPrimaryRole() {

		// Include only CITIZEN and BUSINESS
		List<PrimaryRole> allowedRoles = List.of(PrimaryRole.CITIZEN, PrimaryRole.BUSINESS_OWNER);

		List<UserAccount> users = userRepo.findByPrimaryRoleIn(allowedRoles);

		return users.stream()
				.map(user -> UserProfileDTO.builder().id(user.getId()).username(user.getUsername())
						.email(user.getEmail()).primaryRole(user.getPrimaryRole()).active(user.isActive())
						.createdAt(user.getCreatedAt()).lastLoginAt(user.getLastLoginAt()).build())
				.toList();
	}
}