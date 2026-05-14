package com.example.demo.service;

import java.math.BigDecimal;
import java.util.List;

import com.example.demo.dto.EnergyProgramRequestDto;
import com.example.demo.dto.EnergyProgramResponseDto;
import com.example.demo.exception.ProjectNotFound;

public interface EnergyProgramService {

	List<EnergyProgramResponseDto> getAllPrograms();

	EnergyProgramResponseDto createProgram(EnergyProgramRequestDto request);

	EnergyProgramResponseDto updateProgram(Long programId, EnergyProgramRequestDto request) throws ProjectNotFound;

	EnergyProgramResponseDto updateProgramStatus(Long programId, String status) throws ProjectNotFound;

	String deleteProgram(Long programId) throws ProjectNotFound;

	EnergyProgramResponseDto getProgramById(Long programId) throws ProjectNotFound;

	EnergyProgramResponseDto deductBudget(Long programId, BigDecimal amount) throws ProjectNotFound;

	boolean programExists(Long programId);

	EnergyProgramResponseDto getProgramByTitle(String title) throws ProjectNotFound;
}