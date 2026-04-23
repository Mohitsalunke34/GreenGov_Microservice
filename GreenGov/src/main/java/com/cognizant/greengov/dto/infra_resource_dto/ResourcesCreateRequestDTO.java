package com.cognizant.greengov.dto.infra_resource_dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResourcesCreateRequestDTO {

    @NotNull
    private Long projectId;

    @NotBlank
    private String type;

    @NotNull
    private Double quantity;
}