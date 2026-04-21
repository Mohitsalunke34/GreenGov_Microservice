package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Enums.PrimaryRole;
import com.example.demo.model.UserAccount;

public interface UserAccountRepo extends JpaRepository<UserAccount, Long> {
	Optional<UserAccount> findByUsername(String username);

	List<UserAccount> findByPrimaryRoleIn(List<PrimaryRole> roles);

	Optional<UserAccount> findByEmail(String email);

}