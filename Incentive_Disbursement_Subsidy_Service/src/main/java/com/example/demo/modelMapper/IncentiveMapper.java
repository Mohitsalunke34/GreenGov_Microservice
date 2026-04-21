package com.example.demo.modelMapper;

import com.example.demo.dto.IncentiveResponseDTO;
import com.example.demo.model.Incentive;

public class IncentiveMapper {

    private IncentiveMapper() {
        // Prevent instantiation
    }

    public static IncentiveResponseDTO toDTO(Incentive entity) {
        if (entity == null) {
            return null;
        }

        IncentiveResponseDTO dto = new IncentiveResponseDTO();

        // ID mappings
        dto.setIncentiveId(entity.getIncentiveId());
        dto.setApplicationId(entity.getApplicationId());
        dto.setProgramId(entity.getProgramId());
        dto.setBeneficiaryId(entity.getBeneficiaryId());

        // Amount mappings ✅
        dto.setAmount(entity.getAmount());
        dto.setRemainingAmount(entity.getRemainingAmount()); // ✅ THIS WAS MISSING

        // Meta
        dto.setSanctionedDate(entity.getSanctionedDate());
        dto.setStatus(entity.getStatus());

        // Optional approved-by mapping
        if (entity.getApprovedBy() != null) {
            dto.setApprovedByUserId(entity.getApprovedBy());
        }

        return dto;
    }
}