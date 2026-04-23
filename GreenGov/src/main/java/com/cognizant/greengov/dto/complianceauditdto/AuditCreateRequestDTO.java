package com.cognizant.greengov.dto.complianceauditdto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuditCreateRequestDTO {

	@NotNull(message = "Compliance Id cannot be null")
	private Long complianceId;

}