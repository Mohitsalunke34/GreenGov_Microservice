package com.cognizant.greengov.service.disburse_incentive_service;

import java.util.List;

import com.cognizant.greengov.dto.disburse_incentive_dto.DisbursementProcessResponse;
import com.cognizant.greengov.dto.disburse_incentive_dto.DisbursementResponseDTO;

public interface DisbursementService {

	DisbursementProcessResponse disburse(Long incentiveId, Double amount, Long officerUserId);

	List<DisbursementResponseDTO> getAllDisbursement(Long incentiveId);

	DisbursementResponseDTO getByDisbursementId(Long incentiveId, Long disbursementId);
}