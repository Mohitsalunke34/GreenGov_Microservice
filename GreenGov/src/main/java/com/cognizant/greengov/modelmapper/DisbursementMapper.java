package com.cognizant.greengov.modelmapper;

import com.cognizant.greengov.dto.disburse_incentive_dto.DisbursementResponseDTO;
import com.cognizant.greengov.model.incentive_subsidy.Disbursement;

public class DisbursementMapper {

	private DisbursementMapper() {
	}

	public static DisbursementResponseDTO toDTO(Disbursement entity) {
		DisbursementResponseDTO dto = new DisbursementResponseDTO();
		dto.setDisbursementId(entity.getDisbursementId());
		dto.setIncentiveId(entity.getIncentive().getIncentiveId());
		dto.setOfficerUserId(entity.getOfficer().getId());
		dto.setAmount(entity.getAmount());
		dto.setPaymentDate(entity.getPaymentDate());
		dto.setStatus(entity.getStatus());
		return dto;
	}
}