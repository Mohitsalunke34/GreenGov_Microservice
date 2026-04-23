package com.cognizant.greengov.service.disburse_incentive_service;

import java.util.List;

import com.cognizant.greengov.dto.disburse_incentive_dto.IncentiveCreateRequestDTO;
import com.cognizant.greengov.dto.disburse_incentive_dto.IncentiveResponseDTO;

public interface IncentiveService {

	// Officer creates incentive
	IncentiveResponseDTO createIncentive(IncentiveCreateRequestDTO dto, Long officerUserId);

	// Fetch incentive for a specific application
	IncentiveResponseDTO getByApplication(Long applicationId);

	// Fetch all incentives for a beneficiary (citizen/business)
	List<IncentiveResponseDTO> getByBeneficiary(Long participantId);

	// Fetch incentive by incentiveId
	public IncentiveResponseDTO getByIncentiveId(Long incentiveId);

	// delete the incentive by id
	IncentiveResponseDTO deleteIncentive(Long incentiveId);

	List<IncentiveResponseDTO> getAllIncentives();
}