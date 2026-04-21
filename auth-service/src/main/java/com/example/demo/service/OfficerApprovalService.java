package com.example.demo.service;

import java.util.List;

import com.example.demo.model.OfficerProfile;

public interface OfficerApprovalService {
	List<OfficerProfile> pendingOfficers();

	void approveOfficer(Long officerProfileId);

	List<OfficerProfile> getActiveDisbursementOfficers();
}