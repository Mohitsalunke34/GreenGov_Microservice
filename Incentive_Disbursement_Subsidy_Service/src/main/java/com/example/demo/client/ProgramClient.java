package com.example.demo.client;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dto.ApplicationDTO;
import com.example.demo.dto.ProgramDTO;
import com.example.demo.dto.client_dto.ApprovedApplicationLookupDTO;

@FeignClient(name = "ENERGYPROGRAMSERVICE")
public interface ProgramClient {

	@GetMapping("/api/applications/approved/by-participant/{participantId}")
	List<ApprovedApplicationLookupDTO> getApprovedApplicationsByParticipant(
			@PathVariable("participantId") Long participantId);

	@GetMapping("/api/applications/fetchById/{id}")
	ApplicationDTO getApplicationById(@PathVariable("id") Long id);

	@GetMapping("/api/programs/fetchById/{id}")
	ProgramDTO getProgramById(@PathVariable("id") Long id);

	@PutMapping("/api/programs/{id}/deduct-budget")
	void deductProgramBudget(@PathVariable("id") Long programId, @RequestParam("amount") BigDecimal amount);

}