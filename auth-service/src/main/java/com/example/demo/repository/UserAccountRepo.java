package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.UserAccount;

public interface UserAccountRepo extends JpaRepository<UserAccount, Long> {
	Optional<UserAccount> findByUsername(String username);

	Optional<UserAccount> findByEmail(String email);
}