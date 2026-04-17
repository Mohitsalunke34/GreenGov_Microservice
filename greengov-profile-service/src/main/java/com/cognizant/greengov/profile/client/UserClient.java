package com.cognizant.greengov.profile.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.cognizant.greengov.profile.dto.UserProfileDTO;

@FeignClient(name = "AUTH-SERVICE")
public interface UserClient {

	@GetMapping("/api/auth/findAllCitizenAndBusiness")
	List<UserProfileDTO> getUserByPrimaryRole();
}