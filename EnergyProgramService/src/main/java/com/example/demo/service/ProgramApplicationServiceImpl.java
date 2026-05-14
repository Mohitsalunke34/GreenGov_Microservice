package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.client.NotificationClient; // Added
import com.example.demo.client.ParticipantStatusClient;
import com.example.demo.dto.NotificationRequestDTO; // Added
import com.example.demo.dto.ProgramApplicationRequestDto;
import com.example.demo.dto.ProgramApplicationResponseDto;
import com.example.demo.dto.client_dto.ApprovedApplicationLookupDTO;
import com.example.demo.exception.ProjectNotFound;
import com.example.demo.model.EnergyProgram;
import com.example.demo.model.ProgramApplication;
import com.example.demo.modelmapper.ProgramApplicationMapper;
import com.example.demo.repository.EnergyProgramRepository;
import com.example.demo.repository.ProgramApplicationRepository;

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
	private final NotificationClient notificationClient; // Added

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

		// Trigger Notification
		sendInternalNotification("New application submitted for program: " + program.getProgramId(),
				"APPLICATION_SUBMITTED", saved.getApplicationId());

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
		ProgramApplication saved = applicationRepository.save(application);

		// Trigger Notification
		sendInternalNotification("Your application " + applicationId + " has been APPROVED", "APPLICATION_APPROVED",
				applicationId);

		return ProgramApplicationMapper.toDto(saved);
	}

	@Override
	public ProgramApplicationResponseDto rejectApplication(Long applicationId) {
		ProgramApplication application = fetchApplication(applicationId);
		application.setStatus("REJECTED");

		log.info("Application {} REJECTED", applicationId);
		ProgramApplication saved = applicationRepository.save(application);

		// Trigger Notification
		sendInternalNotification("Your application " + applicationId + " has been REJECTED", "APPLICATION_REJECTED",
				applicationId);

		return ProgramApplicationMapper.toDto(saved);
	}

	@Override
	public List<ApprovedApplicationLookupDTO> getApprovedApplicationsByParticipant(Long participantId) {

		return applicationRepository.findByApplicantIdAndStatus(participantId, "APPROVED").stream()
				.map(app -> new ApprovedApplicationLookupDTO(app.getApplicationId(), // ✅ OK
						app.getProgram().getProgramId(), // ✅ FIXED
						app.getApplicantId() // ✅ OK
				)).collect(Collectors.toList());
	}

	/* ================= INTERNAL / HELPERS ================= */

	private ProgramApplication fetchApplication(Long id) {
		return applicationRepository.findById(id)
				.orElseThrow(() -> new ProjectNotFound("Application not found with ID: " + id));
	}

	private void sendInternalNotification(String message, String category, Long entityId) {
		try {
			NotificationRequestDTO notifyReq = NotificationRequestDTO.builder().userId(1L) // Defaulting to system/admin
																							// user ID 1
					.message(message).category(category).entityId(entityId).sendEmail(false).email("dummy@greengov.com")
					.build();

			notificationClient.createNotification(notifyReq);
			log.info("Notification successfully sent to Notification-Service");
		} catch (Exception e) {
			log.error("DETAILED NOTIFICATION ERROR: ", e);
		}
	}
}