package com.cognizant.greengov.dto.login_register_dto;

import com.cognizant.greengov.model.Enums.PrimaryRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {

    private Long userId;
    private String username;
    private PrimaryRole primaryRole;

    // JWT will be plugged here later
    private String token;
}