package com.example.demo.dto;

import lombok.Data;

@Data
public class ProjectAnalyticsDTO {

    private Integer totalProjects;           // COUNT(*)
    private Integer activeProjects;          // status = ACTIVE
    private Integer completedProjects;       // status = COMPLETED
}