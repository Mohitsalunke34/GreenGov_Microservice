package com.example.demo.dto;

import lombok.Data;

@Data
public class ResourceResponseDTO {

    private Long resourceId;
    private Long projectId;

    private String type;
    private Double quantity;
    private String projectTitle;
    private String status;

	
}
