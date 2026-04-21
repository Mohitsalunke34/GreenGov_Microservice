package com.example.demo.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.dto.UserBasicDTO;

@FeignClient(name = "AUTH-SERVICE")
public interface UserClient {

	@GetMapping("/api/auth/users/{id}/basic")
	UserBasicDTO getUserById(@PathVariable Long id);
}
