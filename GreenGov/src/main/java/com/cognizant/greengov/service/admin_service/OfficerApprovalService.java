package com.cognizant.greengov.service.admin_service;

import java.util.List;

import com.cognizant.greengov.model.register_login.OfficerProfile;

public interface OfficerApprovalService {

	List<OfficerProfile> getPendingOfficers();

	void approveOfficer(Long officerProfileId);

	void rejectOfficer(Long officerProfileId);
	
//	void citizenBussinessApprove(Long participantId);
}
