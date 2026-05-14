package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.AdminOfficerDTO;
import com.example.demo.model.OfficerProfile;

public interface OfficerApprovalService {
	List<AdminOfficerDTO> pendingOfficers();

	void approveOfficer(Long officerProfileId);

	List<OfficerProfile> getActiveDisbursementOfficers();

	List<AdminOfficerDTO> getAllOfficers();

	AdminOfficerDTO getOfficerById(Long id);

	void rejectOfficer(Long id);
}