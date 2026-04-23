package com.cognizant.greengov.repository.register_login_repo;
 
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cognizant.greengov.model.register_login.ParticipantProfile;
 
@Repository
public interface ParticipantProfileRepository extends JpaRepository<ParticipantProfile, Long> {
    // This allows you to find the profile created by the AuthService
    Optional<ParticipantProfile> findByUserId(Long userId);
}