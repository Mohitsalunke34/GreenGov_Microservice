package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.Enums.OfficerType;
import com.example.demo.model.Enums.ProfileStatus;
import com.example.demo.model.OfficerProfile;
import com.example.demo.repository.OfficerProfileRepo;

@Service
public class OfficerApprovalServiceImpl implements OfficerApprovalService {

	private final OfficerProfileRepo repo;

	public OfficerApprovalServiceImpl(OfficerProfileRepo repo) {
		this.repo = repo;
	}

	@Override
	public List<OfficerProfile> pendingOfficers() {
		return repo.findByStatus(ProfileStatus.PENDING);
	}

	@Override
	public void approveOfficer(Long id) {

		OfficerProfile profile = repo.findById(id).orElseThrow(() -> new RuntimeException("Officer profile not found"));

		profile.setStatus(ProfileStatus.APPROVED);
		profile.setApprovedAt(LocalDateTime.now());

		profile.getUser().setActive(true);

		repo.save(profile);
	}

	@Override
	public List<OfficerProfile> getActiveDisbursementOfficers() {

		return repo.findByOfficerTypeAndStatus(OfficerType.DISBURSEMENT_OFFICER, ProfileStatus.APPROVED);
	}

}