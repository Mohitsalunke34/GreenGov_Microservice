package com.example.demo.dto;



import lombok.Data;

@Data
public class OfficerDTO {
 
    private Long userId;
    private String username;
 
    /** DISBURSEMENT_OFFICER, COMPLIANCE_OFFICER, etc */
    private String officerType;
 
    private String department;
    private String designation;
}