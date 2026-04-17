package com.example.demo.controller;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.EnergyProgramRequestDto;
import com.example.demo.dto.EnergyProgramResponseDto;
import com.example.demo.exception.ProjectNotFound;
import com.example.demo.service.EnergyProgramService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/programs")
@Slf4j
@RequiredArgsConstructor
public class EnergyProgramController {

	private final EnergyProgramService service;

	/* ================= READ ================= */

	@GetMapping
	public ResponseEntity<List<EnergyProgramResponseDto>> getAllPrograms() {
		log.info("REST request to fetch all energy programs");
		return ResponseEntity.ok(service.getAllPrograms());
	}

	@GetMapping("/fetchById/{id}")
	public ResponseEntity<EnergyProgramResponseDto> getProgramById(@PathVariable Long id) throws ProjectNotFound {

		log.debug("REST request to fetch program ID {}", id);
		return ResponseEntity.ok(service.getProgramById(id));
	}

	/* ================= CREATE ================= */

	@PostMapping
	public ResponseEntity<EnergyProgramResponseDto> createProgram(@Valid @RequestBody EnergyProgramRequestDto request) {

		log.info("REST request to create Energy Program: {}", request.getTitle());
		return ResponseEntity.ok(service.createProgram(request));
	}

	/* ================= UPDATE ================= */

	@PutMapping("/{programId}")
	public ResponseEntity<EnergyProgramResponseDto> updateProgram(@PathVariable Long programId,
			@Valid @RequestBody EnergyProgramRequestDto request) throws ProjectNotFound {

		log.info("REST request to update program ID {}", programId);
		return ResponseEntity.ok(service.updateProgram(programId, request));
	}


	@PatchMapping("/{programId}/status")
	public ResponseEntity<EnergyProgramResponseDto> updateProgramStatus(@PathVariable Long programId,
			@RequestParam String status) throws ProjectNotFound {

		log.info("REST request to update status of program ID {} to {}", programId, status);
		return ResponseEntity.ok(service.updateProgramStatus(programId, status));
	}

	/* ================= BUDGET ================= */

	@PutMapping("/{id}/deduct-budget")
	public ResponseEntity<EnergyProgramResponseDto> deductProgramBudget(@PathVariable Long programId,
			@RequestParam BigDecimal amount) throws ProjectNotFound {

		log.info("REST request to deduct {} from program ID {}", amount, programId);
		return ResponseEntity.ok(service.deductBudget(programId, amount));
	}

	/* ================= DELETE ================= */

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteProgram(@PathVariable Long id) throws ProjectNotFound {

		log.warn("REST request to delete Energy Program ID {}", id);
		return ResponseEntity.ok(service.deleteProgram(id));
	}
}
