package com.example.demo.modelMapper;

import com.example.demo.dto.IncentiveResponseDTO;
import com.example.demo.model.Incentive;

public class IncentiveMapper {

    private IncentiveMapper() {
        // Private constructor to prevent instantiation
    }

    public static IncentiveResponseDTO toDTO(Incentive entity) {
        if (entity == null) {
            return null;
        }

        IncentiveResponseDTO dto = new IncentiveResponseDTO();
        
        // Direct mappings from ID fields
        dto.setIncentiveId(entity.getIncentiveId());
        dto.setApplicationId(entity.getApplicationId()); // Changed from .getApplication().getApplicationId()
        dto.setProgramId(entity.getProgramId());         // Changed from .getProgram().getProgramId()
        dto.setBeneficiaryId(entity.getBeneficiaryId()); // Changed from .getBeneficiary().getId()
        
        dto.setAmount(entity.getAmount());
        dto.setSanctionedDate(entity.getSanctionedDate());
        dto.setStatus(entity.getStatus());

        // Null check for optional ID field
        if (entity.getApprovedBy() != null) {
            dto.setApprovedByUserId(entity.getApprovedBy()); // Changed from .getApprovedBy().getId()
        }
        
        return dto;
    }
}