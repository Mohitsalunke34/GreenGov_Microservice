package com.cognizant.greengov.service.admin_service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cognizant.greengov.model.Enums.ProfileStatus;
import com.cognizant.greengov.model.register_login.OfficerProfile;
import com.cognizant.greengov.model.register_login.ParticipantProfile;
import com.cognizant.greengov.repository.register_login_repo.OfficerProfileRepository;
import com.cognizant.greengov.repository.register_login_repo.ParticipantProfileRepository;

@Service
@Transactional
public class OfficerApprovalServiceImpl implements OfficerApprovalService {

	private final OfficerProfileRepository officerRepo;
//	private final ParticipantProfileRepository participantRepo;

	// FIX: Both repositories must be in the constructor for Spring to inject them
	public OfficerApprovalServiceImpl(OfficerProfileRepository officerRepo, 
                                     ParticipantProfileRepository participantRepo) {
		this.officerRepo = officerRepo;
//		this.participantRepo = participantRepo;
	}

	@Override
	public List<OfficerProfile> getPendingOfficers() {
		return officerRepo.findByStatus(ProfileStatus.PENDING);
	}

	@Override
	public void approveOfficer(Long officerProfileId) {
		OfficerProfile officer = officerRepo.findById(officerProfileId)
				.orElseThrow(() -> new IllegalArgumentException("Officer profile not found"));

		officer.setStatus(ProfileStatus.APPROVED);
		officer.setApprovedAt(LocalDateTime.now());
        // No need for .save() because of @Transactional dirty checking
	}

	@Override
	public void rejectOfficer(Long officerProfileId) {
		OfficerProfile officer = officerRepo.findById(officerProfileId)
				.orElseThrow(() -> new IllegalArgumentException("Officer profile not found"));

		officer.setStatus(ProfileStatus.REJECTED);
	}
	
//	@Override
//	public void citizenBussinessApprove(Long participantId) {
//        // FIX: Changed variable type from Optional to ParticipantProfile 
//        // because .orElseThrow() unwraps the value.
//		ParticipantProfile participant = participantRepo.findById(participantId)
//				.orElseThrow(() -> new IllegalArgumentException("Participant not found"));
//		
//        // FIX: Logic check - if the method is "Approve", status should be APPROVED
//		participant.SET(ProfileStatus.PENDING);
//	}
}