package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.client.ParticipantStatusClient;
import com.example.demo.dto.ProgramApplicationRequestDto;
import com.example.demo.dto.ProgramApplicationResponseDto;
import com.example.demo.model.EnergyProgram;
import com.example.demo.model.ProgramApplication;
import com.example.demo.modelmapper.ProgramApplicationMapper;
import com.example.demo.repository.EnergyProgramRepository;
import com.example.demo.repository.ProgramApplicationRepository;
import com.example.demo.exception.ProjectNotFound;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProgramApplicationServiceImpl implements ProgramApplicationService {

	private final ProgramApplicationRepository applicationRepository;
	private final EnergyProgramRepository programRepository;
	private final ParticipantStatusClient participantStatusClient;

	/* ================= APPLY ================= */

	@Override
	public ProgramApplicationResponseDto apply(ProgramApplicationRequestDto request) {

		log.info("Applying for program {} by applicant {}", request.getProgramId(), request.getApplicantId());

		boolean isVerified = participantStatusClient.isVerified(request.getApplicantId());

		if (!isVerified) {
			log.warn("Application denied: applicant {} is NOT VERIFIED", request.getApplicantId());
			throw new IllegalStateException("You are not verified, hence not eligible to apply for this program");
		}

		EnergyProgram program = programRepository.findById(request.getProgramId())
				.orElseThrow(() -> new ProjectNotFound("Energy Program not found with ID: " + request.getProgramId()));

		if (applicationRepository.findByApplicantIdAndProgram(request.getApplicantId(), program).isPresent()) {

			throw new IllegalStateException("You have already applied for this program");
		}

		ProgramApplication application = new ProgramApplication();

		application.setApplicantId(request.getApplicantId());
		application.setProgram(program);
		application.setSubmittedDate(LocalDate.now());
		application.setStatus("PENDING");

		ProgramApplication saved = applicationRepository.save(application);

		log.info("Application {} created with status PENDING", saved.getApplicationId());

		return ProgramApplicationMapper.toDto(saved);
	}

	/* ================= READ ================= */

	@Override
	@Transactional(readOnly = true)
	public ProgramApplicationResponseDto getApplicationById(Long applicationId) {

		return applicationRepository.findById(applicationId).map(ProgramApplicationMapper::toDto)
				.orElseThrow(() -> new ProjectNotFound("Application not found with ID: " + applicationId));
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProgramApplicationResponseDto> getAllApplications() {

		return applicationRepository.findAll().stream().map(ProgramApplicationMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProgramApplicationResponseDto> getApplicationsByApplicant(Long applicantId) {

		return applicationRepository.findByApplicantId(applicantId).stream().map(ProgramApplicationMapper::toDto)
				.collect(Collectors.toList());
	}

	/* ================= REVIEW ================= */

	@Override
	public ProgramApplicationResponseDto approveApplication(Long applicationId) {

		ProgramApplication application = fetchApplication(applicationId);

		application.setStatus("APPROVED");

		log.info("Application {} APPROVED", applicationId);

		return ProgramApplicationMapper.toDto(applicationRepository.save(application));
	}

	@Override
	public ProgramApplicationResponseDto rejectApplication(Long applicationId) {

		ProgramApplication application = fetchApplication(applicationId);

		application.setStatus("REJECTED");

		log.info("Application {} REJECTED", applicationId);

		return ProgramApplicationMapper.toDto(applicationRepository.save(application));
	}

	/* ================= INTERNAL ================= */

	private ProgramApplication fetchApplication(Long id) {

		return applicationRepository.findById(id)
				.orElseThrow(() -> new ProjectNotFound("Application not found with ID: " + id));
	}
}