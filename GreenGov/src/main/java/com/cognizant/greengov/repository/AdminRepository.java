package com.cognizant.greengov.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.greengov.model.register_login.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
	Optional<Admin> findByUsername(String username);

	boolean existsByUsername(String username);
}