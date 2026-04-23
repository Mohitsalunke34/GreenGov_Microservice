package com.cognizant.greengov.repository.register_login_repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.greengov.model.Enums.ProfileStatus;
import com.cognizant.greengov.model.register_login.OfficerProfile;

public interface OfficerProfileRepository extends JpaRepository<OfficerProfile, Long> {

	List<OfficerProfile> findByStatus(ProfileStatus status);
}