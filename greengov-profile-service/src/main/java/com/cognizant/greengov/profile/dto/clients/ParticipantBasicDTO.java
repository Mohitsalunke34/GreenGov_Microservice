package com.cognizant.greengov.profile.dto.clients;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// For Compliance microservice client 
public class ParticipantBasicDTO {

	private Long id;
	private boolean verified;
}