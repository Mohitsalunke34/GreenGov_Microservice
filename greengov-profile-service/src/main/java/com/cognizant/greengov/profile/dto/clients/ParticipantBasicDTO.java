package com.cognizant.greengov.profile.dto.clients;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
// For Compliance microservice client 
public class ParticipantBasicDTO {

	private Long id;
	private String legalName;
	private boolean verified;
	
	public ParticipantBasicDTO(Long id, String legalName) {
		super();
		this.id = id;
		this.legalName = legalName;
	}

	public ParticipantBasicDTO() {
		super();
	}

}
