// To get Energy Program Dropdown from Energy Program microservice.
package com.example.demo.dto.client_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectLookupDTO {
	private Long id;
	private String name;
}