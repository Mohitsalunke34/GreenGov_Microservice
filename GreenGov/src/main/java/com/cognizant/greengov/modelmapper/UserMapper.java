package com.cognizant.greengov.modelmapper;

import com.cognizant.greengov.dto.login_register_dto.RegisterRequestDTO;
import com.cognizant.greengov.model.register_login.UserAccount;

public class UserMapper {

	private UserMapper() {
	}

	public static UserAccount toEntity(RegisterRequestDTO dto) {
		UserAccount user = new UserAccount();
		user.setUsername(dto.getUsername());
		user.setEmail(dto.getEmail());
		user.setPrimaryRole(dto.getPrimaryRole());
		user.setActive(true);
		return user;
	}
}