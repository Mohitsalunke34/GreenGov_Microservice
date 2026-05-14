package com.example.demo.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.client.NotificationClient; // Added
import com.example.demo.dto.EnergyProgramRequestDto;
import com.example.demo.dto.EnergyProgramResponseDto;
import com.example.demo.exception.ProjectNotFound;
import com.example.demo.model.EnergyProgram;
import com.example.demo.modelmapper.EnergyProgramMapper;
import com.example.demo.repository.EnergyProgramRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class EnergyProgramServiceImpl implements EnergyProgramService {

	private final EnergyProgramRepository programRepo;
	private final NotificationClient notificationClient; // Added

	/* ================= READ ================= */

	@Override
	@Transactional(readOnly = true)
	public List<EnergyProgramResponseDto> getAllPrograms() {
		return programRepo.findAll().stream().map(EnergyProgramMapper::toDto).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public EnergyProgramResponseDto getProgramById(Long programId) throws ProjectNotFound {

		return programRepo.findById(programId).map(EnergyProgramMapper::toDto).orElseThrow(() -> {
			log.warn("Energy Program ID {} not found", programId);
			return new ProjectNotFound("Energy Program not found with ID: " + programId);
		});
	}

	/* ================= CREATE ================= */

	@Override
	public EnergyProgramResponseDto createProgram(EnergyProgramRequestDto dto) {

		EnergyProgram program = new EnergyProgram();
		program.setTitle(dto.getTitle());
		program.setDescription(dto.getDescription());
		program.setStartDate(dto.getStartDate());
		program.setEndDate(dto.getEndDate());
		program.setBudget(dto.getBudget());
		program.setRemainingProgramBudget(dto.getBudget());
		program.setStatus(dto.getStatus());

		EnergyProgram saved = programRepo.save(program);
		log.info("Created Energy Program ID {}", saved.getProgramId());

		return EnergyProgramMapper.toDto(saved);
	}

	/* ================= UPDATE ================= */

	@Override
	public EnergyProgramResponseDto updateProgram(Long programId, EnergyProgramRequestDto request)
			throws ProjectNotFound {

		EnergyProgram existing = fetchProgram(programId);

		existing.setTitle(request.getTitle());
		existing.setDescription(request.getDescription());
		existing.setStartDate(request.getStartDate());
		existing.setEndDate(request.getEndDate());
		existing.setBudget(request.getBudget());
		existing.setStatus(request.getStatus());

		EnergyProgram updated = programRepo.save(existing);
		log.info("Updated Energy Program ID {}", programId);

		return EnergyProgramMapper.toDto(updated);
	}

	@Override
	public EnergyProgramResponseDto updateProgramStatus(Long programId, String status) throws ProjectNotFound {

		EnergyProgram program = fetchProgram(programId);
		program.setStatus(status);

		EnergyProgram updated = programRepo.save(program);

		return EnergyProgramMapper.toDto(updated);
	}

	/* ================= DELETE ================= */

	@Override
	public String deleteProgram(Long programId) throws ProjectNotFound {

		if (!programRepo.existsById(programId)) {
			throw new ProjectNotFound("Energy Program not found with ID: " + programId);
		}

		programRepo.deleteById(programId);
		log.warn("Deleted Energy Program ID {}", programId);

		return "Energy Program deleted successfully";
	}

	/* ================= BUDGET DEDUCTION ================= */

	@Override
	public EnergyProgramResponseDto deductBudget(Long programId, BigDecimal amount) throws ProjectNotFound {

		if (amount == null || amount.signum() <= 0) {
			throw new IllegalArgumentException("Budget deduction amount must be positive");
		}

		EnergyProgram program = fetchProgram(programId);

		if (program.getRemainingProgramBudget() == null) {
			program.setRemainingProgramBudget(program.getBudget());
		}

		if (amount.compareTo(program.getRemainingProgramBudget()) > 0) {
			throw new IllegalStateException("Insufficient program budget");
		}

		BigDecimal updatedRemainingBudget = program.getRemainingProgramBudget().subtract(amount);
		program.setRemainingProgramBudget(updatedRemainingBudget);

		if (updatedRemainingBudget.compareTo(BigDecimal.ZERO) == 0) {
			program.setStatus("INACTIVE");
			log.info("Program ID {} marked as INACTIVE due to zero remaining budget", programId);

		}

		EnergyProgram updated = programRepo.save(program);
		log.info("Deducted {} from program ID {}. Remaining budget: {}", amount, programId, updatedRemainingBudget);

		return EnergyProgramMapper.toDto(updated);
	}

	/* ================= INTERNAL HELPERS ================= */

	private EnergyProgram fetchProgram(Long programId) throws ProjectNotFound {
		return programRepo.findById(programId)
				.orElseThrow(() -> new ProjectNotFound("Energy Program not found with ID: " + programId));
	}

	@Override
	@Transactional(readOnly = true)
	public boolean programExists(Long programId) {
		return programRepo.existsById(programId);
	}

	@Override
	@Transactional(readOnly = true)
	public EnergyProgramResponseDto getProgramByTitle(String title) throws ProjectNotFound {
		return programRepo.findByTitle(title).map(EnergyProgramMapper::toDto).orElseThrow(() -> {
			log.warn("Energy Program with title '{}' not found", title);
			return new ProjectNotFound("Energy Program not found with title: " + title);
		});
	}

}