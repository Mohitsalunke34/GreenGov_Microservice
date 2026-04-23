package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailDTO {
	private Long id;
	private String username;
	private String email;
	private String primaryRole;
	private boolean active;
}
