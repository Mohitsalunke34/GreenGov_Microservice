package com.example.demo.model;

import java.time.LocalDateTime;

import com.example.demo.model.Enums.PrimaryRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
public class UserAccount {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String username;

	@Column(nullable = false, length = 255)
	private String passwordHash;

	@Column(nullable = false, length = 150)
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private PrimaryRole primaryRole;

	@Column(nullable = false)
	private boolean active = true;

	private LocalDateTime createdAt;
	private LocalDateTime lastLoginAt;

	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
	private OfficerProfile officerProfile;

	@PrePersist
	public void onCreate() {
		if (createdAt == null)
			createdAt = LocalDateTime.now();
	}
}