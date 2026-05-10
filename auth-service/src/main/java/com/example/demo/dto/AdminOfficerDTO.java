package com.example.demo.dto;

import lombok.Data;

@Data
public class AdminOfficerDTO {
    private Long officerProfileId;
    private String officerType;
    private String department;
    private String designation;
    private String status;

    // User info
    private Long userId;
    private String username;
    private String email;
    private boolean active;
}




