package com.example.demo.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.EnergyProgramRequestDto;
import com.example.demo.dto.EnergyProgramResponseDto;
import com.example.demo.exception.ProjectNotFound;
import com.example.demo.service.EnergyProgramService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/programs")
@Slf4j
@RequiredArgsConstructor
public class EnergyProgramController {

	private final EnergyProgramService service;

	/* ================= READ ================= */

	@GetMapping("/fetchAll")
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

	@PostMapping("/create")
	public ResponseEntity<EnergyProgramResponseDto> createProgram(@Valid @RequestBody EnergyProgramRequestDto request) {

		log.info("REST request to create Energy Program: {}", request.getTitle());
		return ResponseEntity.ok(service.createProgram(request));
	}

	/* ================= UPDATE ================= */

	@PutMapping("/updateProgramByID/{programId}")
	public ResponseEntity<EnergyProgramResponseDto> updateProgram(@PathVariable Long programId,
			@Valid @RequestBody EnergyProgramRequestDto request) throws ProjectNotFound {

		log.info("REST request to update program ID {}", programId);
		return ResponseEntity.ok(service.updateProgram(programId, request));
	}

	@PatchMapping("/updateProgramStatus/{programId}/status")
	public ResponseEntity<EnergyProgramResponseDto> updateProgramStatus(@PathVariable Long programId,
			@RequestParam String status) throws ProjectNotFound {

		log.info("REST request to update status of program ID {} to {}", programId, status);
		return ResponseEntity.ok(service.updateProgramStatus(programId, status));
	}

	/* ================= BUDGET ================= */


	@PutMapping("/{id}/deduct-budget")
	public ResponseEntity<EnergyProgramResponseDto> deductProgramBudget(@PathVariable("id") Long programId, // ✅ FIX
			@RequestParam BigDecimal amount) throws ProjectNotFound {

		log.info("REST request to deduct {} from program ID {}", amount, programId);
		return ResponseEntity.ok(service.deductBudget(programId, amount));
	}


	/* ================= DELETE ================= */

	@DeleteMapping("/deleteProgramById/{id}")
	public ResponseEntity<String> deleteProgram(@PathVariable Long id) throws ProjectNotFound {

		log.warn("REST request to delete Energy Program ID {}", id);
		return ResponseEntity.ok(service.deleteProgram(id));
	}
}
