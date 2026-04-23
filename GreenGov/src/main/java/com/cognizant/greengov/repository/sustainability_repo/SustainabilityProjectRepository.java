package com.cognizant.greengov.repository.sustainability_repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.greengov.model.register_login.ParticipantProfile;
import com.cognizant.greengov.model.sustainability_renewable_proj.SustainabilityProject;

public interface SustainabilityProjectRepository extends JpaRepository<SustainabilityProject, Long> {

	// Used to show all projects owned by a citizen or business

	List<SustainabilityProject> findByOwner(ParticipantProfile owner);

	// Used by officers and admins
	// Example: list projects by lifecycle status

	List<SustainabilityProject> findByStatus(String status);

	// Used for analytics & dashboards

	List<SustainabilityProject> findByOwnerAndStatus(ParticipantProfile owner, String status);
}