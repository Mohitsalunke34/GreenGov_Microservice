package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.AdminOfficerDTO;
import com.example.demo.model.Enums.OfficerType;
import com.example.demo.model.Enums.ProfileStatus;
import com.example.demo.model.OfficerProfile;
import com.example.demo.repository.OfficerProfileRepo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OfficerApprovalServiceImpl implements OfficerApprovalService {

	private final OfficerProfileRepo repo;

	public OfficerApprovalServiceImpl(OfficerProfileRepo repo) {
		this.repo = repo;
	}

	// Centralized mapping method
	private AdminOfficerDTO toAdminDTO(OfficerProfile profile) {

		AdminOfficerDTO dto = new AdminOfficerDTO();

		dto.setOfficerProfileId(profile.getId());
		dto.setOfficerType(profile.getOfficerType().name());
		dto.setDepartment(profile.getDepartment());
		dto.setDesignation(profile.getDesignation());
		dto.setStatus(profile.getStatus().name());

		dto.setUserId(profile.getUser().getId());
		dto.setUsername(profile.getUser().getUsername());
		dto.setEmail(profile.getUser().getEmail());
		dto.setActive(profile.getUser().isActive());

		return dto;
	}

	@Override
	public List<AdminOfficerDTO> getAllOfficers() {
		return repo.findAll().stream().map(this::toAdminDTO).toList();
	}

	@Override
	public AdminOfficerDTO getOfficerById(Long id) {
		OfficerProfile profile = repo.findById(id).orElseThrow(() -> new RuntimeException("Officer profile not found"));
		return toAdminDTO(profile);
	}

	@Override
	public List<AdminOfficerDTO> pendingOfficers() {
		return repo.findByStatus(ProfileStatus.PENDING).stream().map(this::toAdminDTO).toList();
	}

	@Override

	public void approveOfficer(Long id) {

		OfficerProfile profile = repo.findById(id).orElseThrow(() -> new RuntimeException("Officer profile not found"));

		profile.setStatus(ProfileStatus.APPROVED);
		profile.setApprovedAt(LocalDateTime.now());
		profile.getUser().setActive(true);

		repo.save(profile);

		log.info("Officer approved: profileId={}", id);
	}

	@Override
	public void rejectOfficer(Long id) {

		OfficerProfile profile = repo.findById(id).orElseThrow(() -> new RuntimeException("Officer profile not found"));

		profile.setStatus(ProfileStatus.REJECTED);
		profile.getUser().setActive(false);

		repo.save(profile);

		log.info("Officer rejected: profileId={}", id);
	}

	@Override
	public List<OfficerProfile> getActiveDisbursementOfficers() {
		return repo.findByOfficerTypeAndStatus(OfficerType.DISBURSEMENT_OFFICER, ProfileStatus.APPROVED);
	}
}