package com.example.demo.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.dto.client_dto.SubjectLookupDTO;

@FeignClient(name = "ENERGYPROGRAMSERVICE")
public interface EnergyProgramClient {

	@GetMapping("/api/programs/{id}/exists")
	Boolean programExists(@PathVariable Long id);

	@GetMapping("/api/programs/subjects")
	List<SubjectLookupDTO> getProgramSubjects();

}
