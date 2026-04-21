package com.example.demo.modelMapper;


import com.example.demo.dto.DisbursementResponseDTO;
import com.example.demo.model.Disbursement;

public class DisbursementMapper {

    private DisbursementMapper() {}

    public static DisbursementResponseDTO toDTO(Disbursement entity) {

        DisbursementResponseDTO dto = new DisbursementResponseDTO();
        dto.setDisbursementId(entity.getDisbursementId());
        dto.setIncentiveId(entity.getIncentive().getIncentiveId());
        dto.setOfficerUserId(entity.getOfficerUserId());
        dto.setAmount(entity.getAmount());
        dto.setPaymentDate(entity.getPaymentDate());
        dto.setStatus(entity.getStatus());

        return dto;
    }
}