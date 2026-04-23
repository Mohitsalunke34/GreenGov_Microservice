package com.example.demo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.dto.UserDetailDTO;

@FeignClient(name="AUTH-SERVICE")
public interface UserClient {
	@GetMapping("/api/auth/users/{id}/basic")
	ResponseEntity<UserDetailDTO> getUserBasicById(@PathVariable ("id") long id);
}
