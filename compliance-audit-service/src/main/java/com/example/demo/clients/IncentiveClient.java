package com.example.demo.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.dto.client_dto.SubjectLookupDTO;

@FeignClient(name = "INCENTIVE-SERVICE")
public interface IncentiveClient {

	@GetMapping("/api/incentives/{id}/exists")
	Boolean incentiveExists(@PathVariable Long id);

	@GetMapping("/api/incentives/subjects")
	List<SubjectLookupDTO> getIncentiveSubjects();

}
