package com.example.demo.service;

import java.util.List;
import java.util.Map;

import com.example.demo.dto.IncentiveCreateRequestDTO;
import com.example.demo.dto.IncentiveResponseDTO;

public interface IncentiveService {

	// Officer creates incentive
	IncentiveResponseDTO createIncentive(IncentiveCreateRequestDTO dto, Long officerUserId);

	// Fetch incentive for a specific application
//	Optional<Incentive> findByApplicationId(Long applicationId);
	IncentiveResponseDTO getByApplication(Long applicationId);

	// Fetch all incentives for a beneficiary (citizen/business)
	List<IncentiveResponseDTO> getByBeneficiary(Long participantId);

	// Fetch incentive by incentiveId
	public IncentiveResponseDTO getByIncentiveId(Long incentiveId);

	// delete the incentive by id
	IncentiveResponseDTO deleteIncentive(Long incentiveId);

	List<IncentiveResponseDTO> getAllIncentives();

	Map<String, Object> getIncentiveReportMetrics();

	boolean incentiveExists(Long incentiveId);
}