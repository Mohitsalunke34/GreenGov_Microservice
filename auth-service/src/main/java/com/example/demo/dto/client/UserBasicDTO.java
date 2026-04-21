package com.example.demo.dto.client;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBasicDTO {

	private Long id;
	private String username;
	private String primaryRole;
	private boolean active;
}
