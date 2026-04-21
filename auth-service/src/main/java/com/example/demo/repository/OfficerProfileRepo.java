package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Enums.OfficerType;
import com.example.demo.model.Enums.ProfileStatus;
import com.example.demo.model.OfficerProfile;

public interface OfficerProfileRepo extends JpaRepository<OfficerProfile, Long> {

	List<OfficerProfile> findByStatus(ProfileStatus status);

	List<OfficerProfile> findByOfficerTypeAndStatus(OfficerType officerType, ProfileStatus status);

}