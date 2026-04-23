package com.cognizant.greengov.dto.infra_resource_dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InfrastructureStatusDTO {
    private long infraId;
    private String status;
}