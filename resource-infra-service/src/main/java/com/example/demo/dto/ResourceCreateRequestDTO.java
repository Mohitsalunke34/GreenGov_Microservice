package com.example.demo.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class ResourceCreateRequestDTO {

	@NotNull
    private Long projectId;

    @NotBlank
    private String type;

    @NotNull
    private Double quantity;

}