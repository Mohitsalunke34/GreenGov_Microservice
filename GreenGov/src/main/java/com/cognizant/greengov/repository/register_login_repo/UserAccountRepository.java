package com.cognizant.greengov.repository.register_login_repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.greengov.model.register_login.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

	Optional<UserAccount> findByUsername(String username);

	Optional<UserAccount> findByEmail(String email);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);
}
