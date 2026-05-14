package com.example.demo.dto.client_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApprovedApplicationLookupDTO {
	private Long applicationId;
	private Long programId;
	private Long participantId;
}
