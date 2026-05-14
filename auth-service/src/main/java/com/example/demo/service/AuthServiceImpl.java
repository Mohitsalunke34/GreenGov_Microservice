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
import com.example.demo.dto.client.UserBasicDTO;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

	// ---------------- REGISTER ----------------
	@Override
	@Transactional
	public Long register(RegisterRequestDTO request) {

		log.info("Registering user={}", request.getUsername());

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

		UserAccount savedUser = userRepo.save(user);

		if (request.getPrimaryRole() == PrimaryRole.OFFICER) {

			if (request.getOfficerType() == null) {
				throw new IllegalArgumentException("Officer type is required");
			}

			OfficerProfile profile = OfficerProfile.builder().user(user).officerType(request.getOfficerType())
					.department(request.getDepartment()).designation(request.getDesignation())
					.status(ProfileStatus.PENDING).build();

			officerRepo.save(profile);

			log.info("Officer profile created | userId={} | officerType={} | status={}", savedUser.getId(),
					profile.getOfficerType(), profile.getStatus());
		}

		// RETURN USER ID
		return savedUser.getId();

	}

	// ---------------- USER LOGIN ----------------
	@Override
	public String userLogin(String username, String password) {

		UserAccount user = userRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

		if (!encoder.matches(password, user.getPasswordHash())) {
			throw new RuntimeException("Invalid username or password");
		}

		if (!user.isActive()) {
			throw new RuntimeException("Account inactive. Await admin approval.");
		}
		// setting the ROLE for token to be interpreted later through JWT
		List<String> roles = List.of("ROLE_" + user.getPrimaryRole().name());
		List<String> authorities = new ArrayList<>();

		// checking if the user is an officer so that we can store the authority field
		// in authorities arraylist
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
		claims.put("userId", user.getId());
		claims.put("roles", roles);
		claims.put("authorities", authorities);

		return jwtService.generateToken(username, claims);
	}

	// ---------------- ADMIN LOGIN ----------------
	@Override
	public String adminLogin(String username, String password) {

		Admin admin = adminRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("Admin not found"));

		if (!password.equals(admin.getPasswordHash())) {
			throw new RuntimeException("Invalid admin credentials");
		}

		if (!admin.isActive()) {
			throw new RuntimeException("Admin account inactive");
		}

		Map<String, Object> claims = new HashMap<>();
		claims.put("userId", admin.getId());
		claims.put("roles", List.of("ROLE_ADMIN"));
		claims.put("authorities", List.of("ADMIN"));

		return jwtService.generateToken(username, claims);

	}

	// ---------------- FETCH USERS ----------------
	@Override
	public List<UserProfileDTO> getUserByPrimaryRole() {

		List<PrimaryRole> allowedRoles = List.of(PrimaryRole.CITIZEN, PrimaryRole.BUSINESS_OWNER);

		return userRepo.findByPrimaryRoleIn(allowedRoles).stream()
				.map(user -> UserProfileDTO.builder().id(user.getId()).username(user.getUsername())
						.email(user.getEmail()).primaryRole(user.getPrimaryRole()).active(user.isActive())
						.createdAt(user.getCreatedAt()).lastLoginAt(user.getLastLoginAt()).build())
				.toList();
	}

	// ---------------- BASIC USER ----------------
	@Override
	public UserBasicDTO getUserBasicById(Long userId) {

		UserAccount user = userRepo.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

		UserBasicDTO dto = new UserBasicDTO();
		dto.setId(user.getId());
		dto.setUsername(user.getUsername());
		dto.setEmail(user.getEmail());
		dto.setPrimaryRole(user.getPrimaryRole().name());
		dto.setActive(user.isActive());

		return dto;
	}
}