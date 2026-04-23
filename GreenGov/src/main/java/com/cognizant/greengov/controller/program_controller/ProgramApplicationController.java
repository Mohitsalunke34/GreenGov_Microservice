package com.cognizant.greengov.controller.program_controller;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.cognizant.greengov.dto.sustainability_dto.ProgramApplicationRequestDTO;
//import com.cognizant.greengov.service.sustainability_service.ProgramApplicationService;
//
//import jakarta.validation.Valid;
//
//@RestController
//@RequestMapping("/api/applications")
//public class ProgramApplicationController {
//
//	private final ProgramApplicationService service;
//
//	public ProgramApplicationController(ProgramApplicationService service) {
//		this.service = service;
//	}
//	//apply to a Program
//	@PostMapping
//	public ResponseEntity<Void> apply(@RequestParam Long participantId,
//			@RequestBody @Valid ProgramApplicationRequestDTO dto) {
//
//		service.apply(participantId, dto);
//		return ResponseEntity.status(HttpStatus.CREATED).build();
//	}
//}


 
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.greengov.dto.sustainability_dto.ProgramApplicationRequestDTO;
import com.cognizant.greengov.dto.sustainability_dto.ProgramApplicationResponseDTO;
import com.cognizant.greengov.exception.ProjectNotFound;
import com.cognizant.greengov.service.sustainability_service.ProgramApplicationService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
 
@RestController

@RequestMapping("/api/applications")

@Slf4j

@AllArgsConstructor

public class ProgramApplicationController {
 
	private final ProgramApplicationService service;
 
	/**

	 * Endpoint to submit a new application to a specific Energy Program.

	 * @param participantId The ID of the user applying.

	 * @param dto Validated request body containing program details.

	 * @return 201 Created status on success.

	 */

	@PostMapping

	public ResponseEntity<Void> apply(@RequestParam Long participantId,

			@RequestBody @Valid ProgramApplicationRequestDTO dto) {
 
		log.info("REST request: Participant ID {} is applying for Program ID: {}", participantId, dto.getProgramId());

		service.apply(participantId, dto);

		log.info("Application successfully created for Participant ID: {}", participantId);

		return ResponseEntity.status(HttpStatus.CREATED).build();

	}

	/**

	 * Endpoint to retrieve all program applications.

	 * @return List of ProgramApplicationResponseDTO.

	 */

	@GetMapping("/fetchAll")

    public ResponseEntity<List<ProgramApplicationResponseDTO>> fetchAll() {

		log.debug("REST request to fetch all program applications");

        return ResponseEntity.ok(service.getAllApplications());

    }

	/**

	 * Endpoint to fetch application details by a specific ID.

	 * @param applicationId Unique identifier of the application.

	 * @return Application details.

	 * @throws ProjectNotFound if ID is invalid.

	 */

    @GetMapping("/{applicationId}")

    public ResponseEntity<ProgramApplicationResponseDTO> fetchById(@PathVariable Long applicationId) throws ProjectNotFound {

    	log.debug("REST request to fetch application details for ID: {}", applicationId);

        return ResponseEntity.ok(service.getApplicationById(applicationId));

    }

    /**

	 * Endpoint to transition an application status to 'APPROVED'.

	 * @param applicationId Unique identifier of the application.

	 * @return Updated application details.

	 */

    @PatchMapping("/{applicationId}/approve")

    public ResponseEntity<ProgramApplicationResponseDTO> approve(@PathVariable Long applicationId) throws ProjectNotFound {

    	log.info("REST request to APPROVE application ID: {}", applicationId);

        return ResponseEntity.ok(service.approveApplication(applicationId));

    }
 
    /**

	 * Endpoint to transition an application status to 'REJECTED'.

	 * @param applicationId Unique identifier of the application.

	 * @return Updated application details.

	 */

    @PatchMapping("/{applicationId}/reject")

    public ResponseEntity<ProgramApplicationResponseDTO> reject(@PathVariable Long applicationId) throws ProjectNotFound {

    	log.info("REST request to REJECT application ID: {}", applicationId);

        return ResponseEntity.ok(service.rejectApplication(applicationId));

    }

}
 