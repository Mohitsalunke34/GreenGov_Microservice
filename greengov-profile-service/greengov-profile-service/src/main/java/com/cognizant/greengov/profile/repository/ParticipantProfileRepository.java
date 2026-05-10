package com.cognizant.greengov.profile.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cognizant.greengov.profile.model.register_login.ParticipantProfile;

@Repository
public interface ParticipantProfileRepository extends JpaRepository<ParticipantProfile, Long> {
    Optional<ParticipantProfile> findByUserId(Long userId);
}