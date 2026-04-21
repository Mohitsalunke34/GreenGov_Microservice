package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.DisbursementProcessResponse;
import com.example.demo.dto.DisbursementResponseDTO;

public interface DisbursementService {

	DisbursementProcessResponse disburse(Long incentiveId, Double amount, Long officerUserId);

	List<DisbursementResponseDTO> getAllDisbursement(Long incentiveId);

	DisbursementResponseDTO getByDisbursementId(Long incentiveId, Long disbursementId);
}