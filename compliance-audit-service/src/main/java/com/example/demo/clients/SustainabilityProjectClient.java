package com.example.demo.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.dto.client_dto.SubjectLookupDTO;

@FeignClient(name = "SUSTAINABILITYPROJECTSERVICE")
public interface SustainabilityProjectClient {

	@GetMapping("/api/projects/{id}/exists")
	Boolean projectExists(@PathVariable Long id);

	@GetMapping("/api/projects/subjects")
	List<SubjectLookupDTO> getProjectSubjects();

}
