package com.cognizant.greengov.service.register_login_service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cognizant.greengov.dto.login_register_dto.LoginRequestDTO;
import com.cognizant.greengov.dto.login_register_dto.LoginResponseDTO;
import com.cognizant.greengov.dto.login_register_dto.RegisterRequestDTO;
import com.cognizant.greengov.model.Enums.PrimaryRole;
import com.cognizant.greengov.model.register_login.OfficerProfile;
import com.cognizant.greengov.model.register_login.ParticipantProfile;
import com.cognizant.greengov.model.register_login.UserAccount;
import com.cognizant.greengov.modelmapper.UserMapper;
import com.cognizant.greengov.repository.register_login_repo.OfficerProfileRepository;
import com.cognizant.greengov.repository.register_login_repo.ParticipantProfileRepository;
import com.cognizant.greengov.repository.register_login_repo.UserAccountRepository;
import com.cognizant.greengov.security.JwtService;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

	private final UserAccountRepository userRepo;
	private final ParticipantProfileRepository participantRepo;
	private final OfficerProfileRepository officerRepo;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthServiceImpl(UserAccountRepository userRepo, ParticipantProfileRepository participantRepo,
			OfficerProfileRepository officerRepo, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.userRepo = userRepo;
		this.participantRepo = participantRepo;
		this.officerRepo = officerRepo;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	@Override
	public void register(RegisterRequestDTO dto) {

		if (userRepo.existsByUsername(dto.getUsername())) {
			throw new IllegalArgumentException("Username already exists");
		}

		UserAccount user = UserMapper.toEntity(dto);
		user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
		userRepo.save(user);

		if (dto.getPrimaryRole() == PrimaryRole.CITIZEN || dto.getPrimaryRole() == PrimaryRole.BUSINESS_OWNER) {

			ParticipantProfile profile = new ParticipantProfile();
			profile.setUser(user);
			profile.setEntityType(dto.getEntityType());
			profile.setLegalName(dto.getLegalName());
			profile.setAddress(dto.getAddress());
			profile.setContactInfoJson(dto.getContactInfoJson());

			participantRepo.save(profile);
		}

		if (dto.getPrimaryRole() == PrimaryRole.OFFICER) {

			OfficerProfile officer = new OfficerProfile();
			officer.setUser(user);
			officer.setOfficerType(dto.getOfficerType());
			officer.setDepartment(dto.getDepartment());
			officer.setDesignation(dto.getDesignation());
			officer.setOfficeCode(dto.getOfficeCode());
			officer.setSubmittedAt(LocalDateTime.now());

			officerRepo.save(officer);
		}
	}

	@Override
	public LoginResponseDTO login(LoginRequestDTO dto) {

	    UserAccount user = userRepo.findByUsername(dto.getUsername())
	            .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

	    if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
	        throw new IllegalArgumentException("Invalid credentials");
	    }

	    // OFFICER-SPECIFIC CHECK
	    if (user.getPrimaryRole() == PrimaryRole.OFFICER) {

	        // officer profile must exist
	        if (user.getOfficerProfile() == null) {
	            throw new IllegalStateException("Officer profile not found");
	        }

	        String status = user.getOfficerProfile().getStatus().name();

	        if ("PENDING".equals(status)) {
	            throw new IllegalStateException(
	                "Officer account is pending approval by admin");
	        }

	        if ("REJECTED".equals(status)) {
	            throw new IllegalStateException(
	                "Officer account has been rejected");
	        }

	    }

	    // ✅ Claims for JWT
	    boolean officerApproved =
	            user.getPrimaryRole() == PrimaryRole.OFFICER &&
	            user.getOfficerProfile() != null &&
	            "APPROVED".equals(user.getOfficerProfile().getStatus().name());

	    String token = jwtService.generateToken(
	            user.getUsername(),
	            Map.of(
	                "role", user.getPrimaryRole().name(),
	                "officerApproved", officerApproved
	            )
	    );

	    return new LoginResponseDTO(
	            user.getId(),
	            user.getUsername(),
	            user.getPrimaryRole(),
	            token
	    );
	}
}
