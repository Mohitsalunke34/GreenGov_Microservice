package com.cognizant.greengov.dto.infra_resource_dto;

import lombok.Data;

@Data
public class ResourcesResponseDTO {

    private Long resourceId;
    private Long projectId;

    private String type;
    private Double quantity;

    private String status;
}