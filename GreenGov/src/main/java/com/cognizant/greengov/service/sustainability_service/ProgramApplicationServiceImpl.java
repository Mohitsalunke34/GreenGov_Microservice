//package com.cognizant.greengov.service.sustainability_service;
//
//import java.time.LocalDate;
//
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.cognizant.greengov.dto.sustainability_dto.ProgramApplicationRequestDTO;
//import com.cognizant.greengov.model.register_login.ParticipantProfile;
//import com.cognizant.greengov.model.sustainability_renewable_proj.EnergyProgram;
//import com.cognizant.greengov.model.sustainability_renewable_proj.ProgramApplication;
//import com.cognizant.greengov.repository.register_login_repo.ParticipantProfileRepository;
//import com.cognizant.greengov.repository.sustainability_repo.EnergyProgramRepository;
//import com.cognizant.greengov.repository.sustainability_repo.ProgramApplicationRepository;
//
//@Service
//@Transactional
//public class ProgramApplicationServiceImpl implements ProgramApplicationService {
//
//	private final ProgramApplicationRepository appRepo;
//	private final EnergyProgramRepository programRepo;
//	private final ParticipantProfileRepository participantRepo;
//
//	public ProgramApplicationServiceImpl(ProgramApplicationRepository appRepo, EnergyProgramRepository programRepo,
//			ParticipantProfileRepository participantRepo) {
//		this.appRepo = appRepo;
//		this.programRepo = programRepo;
//		this.participantRepo = participantRepo;
//	}
//
//	@Override
//	public void apply(Long participantId, ProgramApplicationRequestDTO dto) {
//
//		ParticipantProfile applicant = participantRepo.findById(participantId)
//				.orElseThrow(() -> new IllegalArgumentException("Participant not found"));
//
//		EnergyProgram program = programRepo.findById(dto.getProgramId())
//				.orElseThrow(() -> new IllegalArgumentException("Program not found"));
//
//		if (appRepo.findByApplicantAndProgram(applicant, program).isPresent()) {
//			throw new IllegalStateException("Already applied");
//		}
//
//		ProgramApplication app = new ProgramApplication();
//		app.setApplicant(applicant);
//		app.setProgram(program);
//		app.setSubmittedDate(LocalDate.now());
//		app.setStatus("PENDING");
//
//		appRepo.save(app);
//	}
//	
//}

package com.cognizant.greengov.service.sustainability_service;
 
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cognizant.greengov.dto.sustainability_dto.ProgramApplicationRequestDTO;
import com.cognizant.greengov.dto.sustainability_dto.ProgramApplicationResponseDTO;
import com.cognizant.greengov.exception.ProjectNotFound;
import com.cognizant.greengov.model.register_login.ParticipantProfile;
import com.cognizant.greengov.model.sustainability_renewable_proj.EnergyProgram;
import com.cognizant.greengov.model.sustainability_renewable_proj.ProgramApplication;
import com.cognizant.greengov.modelmapper.ProgramApplicationMapper;
import com.cognizant.greengov.repository.register_login_repo.ParticipantProfileRepository;
import com.cognizant.greengov.repository.sustainability_repo.EnergyProgramRepository;
import com.cognizant.greengov.repository.sustainability_repo.ProgramApplicationRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
 
/*Service implementation for handling Program Applications.

* Manages the lifecycle of an application from submission to approval/rejection.*/

@Service

@Transactional

@AllArgsConstructor

@Slf4j

public class ProgramApplicationServiceImpl implements ProgramApplicationService {
 
	private final ProgramApplicationRepository appRepo;

	private final EnergyProgramRepository programRepo;

	private final ParticipantProfileRepository participantRepo;
 
	/*Processes a new application for a specific energy program.

	 * Validates participant and program existence, and prevents duplicate applications.*/

	@Override

	public void apply(Long participantId, ProgramApplicationRequestDTO dto) {

		log.info("Processing application request for Participant ID: {} on Program ID: {}", participantId, dto.getProgramId());

		// 1. Verify if the participant exists

		ParticipantProfile applicant = participantRepo.findById(participantId)

				.orElseThrow(() -> {

					log.error("Application failed: Participant ID {} not found", participantId);

					return new IllegalArgumentException("Participant not found");

				});

		// 2. Verify if the energy program exists

		EnergyProgram program = programRepo.findById(dto.getProgramId())

				.orElseThrow(() -> {

					log.error("Application failed: Program ID {} not found", dto.getProgramId());

					return new IllegalArgumentException("Program not found");

				});

		// 3. Business Rule: A participant cannot apply for the same program twice

		if (appRepo.findByApplicantAndProgram(applicant, program).isPresent()) {

			log.warn("Duplicate application attempt: Participant {} already applied for Program {}", participantId, dto.getProgramId());

			throw new IllegalStateException("Already applied");

		}

		// 4. Map data to new Application entity and set default values

		ProgramApplication app = new ProgramApplication();

		app.setApplicant(applicant);

		app.setProgram(program);

		app.setSubmittedDate(LocalDate.now());

		app.setStatus("PENDING");

		// 5. Persist to database

		appRepo.save(app);

		log.info("Application successfully submitted for Participant ID: {} (Status: PENDING)", participantId);

	}
 
 
	//Retrieves a specific application by its ID.

	@Override

	public  ProgramApplicationResponseDTO getApplicationById(Long applicationId) throws ProjectNotFound {

		log.debug("Fetching application details for ID: {}", applicationId);

		return  appRepo.findById(applicationId).map(this::mapToResponse)

				.orElseThrow(() ->{

					log.warn("Application retrieval failed: ID {} not found", applicationId);

					return new ProjectNotFound("Application ID not found.");

				});

	}
 
//	Retrieves all program applications currently in the system.

	@Override

	public List<ProgramApplicationResponseDTO> getAllApplications() {

		log.debug("Retrieving all program applications from database");

		return appRepo.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());

	}
 
	//Private utility method to convert a ProgramApplication entity to a Response DTO.

	private ProgramApplicationResponseDTO mapToResponse(ProgramApplication entity) {

		ProgramApplicationResponseDTO dto = new ProgramApplicationResponseDTO();
 
		dto.setApplicationId(entity.getApplicationId());

		dto.setProgramId(entity.getProgram().getProgramId());

		dto.setApplicantId(entity.getApplicant().getId());

		dto.setSubmittedDate(entity.getSubmittedDate());

		dto.setStatus(entity.getStatus());
 
		return dto;

	}
 
	//Sets an application's status to 'APPROVED'.

	//Uses ProgramApplicationMapper for conversion.

	@Override

	public ProgramApplicationResponseDTO approveApplication(Long applicationId) throws ProjectNotFound {

		log.info("Attempting to APPROVE application ID: {}", applicationId);

		ProgramApplication existing = appRepo.findById(applicationId)

				.orElseThrow(() -> {

					log.error("Approval failed: Application ID {} not found", applicationId);

					return new ProjectNotFound("Application not found");

				});

		existing.setStatus("APPROVED");

		ProgramApplication savedApp = appRepo.save(existing);

		log.info("Application ID: {} successfully APPROVED", applicationId);

		return ProgramApplicationMapper.toDTO(savedApp);

	}
 
	//Sets an application's status to 'REJECTED'.

	//Uses internal mapToResponse method for conversion.

	@Override

	public ProgramApplicationResponseDTO rejectApplication(Long applicationId) throws ProjectNotFound {

		log.info("Attempting to REJECT application ID: {}", applicationId);

		ProgramApplication existing = appRepo.findById(applicationId)

				.orElseThrow(() -> {

					log.error("Rejection failed: Application ID {} not found", applicationId);

					return new ProjectNotFound("Application not found");

				});

		existing.setStatus("REJECTED");

		ProgramApplication savedApp = appRepo.save(existing);

		log.info("Application ID: {} successfully REJECTED", applicationId);

		return mapToResponse(savedApp);

	}
 
 
}
 