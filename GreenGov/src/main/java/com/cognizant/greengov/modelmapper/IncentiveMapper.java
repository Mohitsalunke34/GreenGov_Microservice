package com.cognizant.greengov.modelmapper;

import com.cognizant.greengov.dto.disburse_incentive_dto.IncentiveResponseDTO;
import com.cognizant.greengov.model.incentive_subsidy.Incentive;

public class IncentiveMapper {

	private IncentiveMapper() {
	}

	public static IncentiveResponseDTO toDTO(Incentive entity) {
		IncentiveResponseDTO dto = new IncentiveResponseDTO();
		dto.setIncentiveId(entity.getIncentiveId());
		dto.setApplicationId(entity.getApplication().getApplicationId());
		dto.setProgramId(entity.getProgram().getProgramId());
		dto.setBeneficiaryId(entity.getBeneficiary().getId());
		dto.setAmount(entity.getAmount());
		dto.setSanctionedDate(entity.getSanctionedDate());
		dto.setStatus(entity.getStatus());

		if (entity.getApprovedBy() != null) {
			dto.setApprovedByUserId(entity.getApprovedBy().getId());
		}
		return dto;
	}
}