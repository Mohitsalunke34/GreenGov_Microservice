package com.cognizant.greengov.model.register_login;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.cognizant.greengov.model.Enums.PrimaryRole;
import com.cognizant.greengov.model.incentive_subsidy.Incentive;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_accounts", indexes = { @Index(name = "uk_user_uname", columnList = "username", unique = true),
		@Index(name = "uk_user_email", columnList = "email", unique = true) })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// It's a common filed class for participant and Officer
public class UserAccount {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Login credentials
	@Column(nullable = false, length = 100)
	private String username;

	@Column(nullable = false, length = 255)
	private String passwordHash; // store BCrypt hash

	@Column(nullable = false, length = 150)
	private String email;

	// High-level role selection at registration time
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private PrimaryRole primaryRole;

	// Account controls
	@Column(nullable = false)
	private boolean active = true; // set true for citizen/business, may be false for officer till approval

	private LocalDateTime createdAt;
	private LocalDateTime lastLoginAt;

	// Profiles (exactly one of these must exist depending on role)
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private ParticipantProfile participantProfile;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private OfficerProfile officerProfile;

	@OneToMany(mappedBy = "approvedBy", fetch = FetchType.LAZY)
	private List<Incentive> approvedIncentives = new ArrayList<>();

	@PrePersist
	public void onCreate() {
		if (createdAt == null)
			createdAt = LocalDateTime.now();
	}
}