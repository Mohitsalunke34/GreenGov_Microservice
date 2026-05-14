package com.cognizant.greengov.profile.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.greengov.profile.dto.DocumentResponseDto;
import com.cognizant.greengov.profile.dto.DocumentUploadRequestDto;
import com.cognizant.greengov.profile.dto.EntityProfileResponseDto;
import com.cognizant.greengov.profile.dto.ParticipantRegistrationRequestDto;
import com.cognizant.greengov.profile.dto.ParticipantUpdateRequestDto;
import com.cognizant.greengov.profile.dto.VerificationStatusUpdateDto;
import com.cognizant.greengov.profile.dto.clients.ParticipantBasicDTO;
import com.cognizant.greengov.profile.service.ParticipantService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/participants")
@RequiredArgsConstructor
@Slf4j
public class ParticipantController {

	private final ParticipantService participantService;

	@PostMapping("/register")
	public ResponseEntity<EntityProfileResponseDto> registerParticipant(
			@Valid @RequestBody ParticipantRegistrationRequestDto request) {
		log.info("Received request to register new participant for userId: {}", request.getUserId());
		EntityProfileResponseDto response = participantService.registerParticipant(request);
		log.info("Successfully registered participant with profile ID: {}", response.getId());
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<EntityProfileResponseDto> getParticipantDetails(@PathVariable Long id) {
		log.info("Received request to fetch participant details for profile ID: {}", id);
		return ResponseEntity.ok(participantService.getParticipantDetails(id));
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<EntityProfileResponseDto> getParticipantByUserId(@PathVariable Long userId) {
		log.info("Received request to fetch participant details for user ID: {}", userId);
		return ResponseEntity.ok(participantService.getParticipantByUserId(userId));
	}

	@PutMapping("/{id}")
	public ResponseEntity<EntityProfileResponseDto> updateParticipantDetails(@PathVariable Long id,
			@Valid @RequestBody ParticipantUpdateRequestDto request) {
		log.info("Received request to update participant details for profile ID: {}", id);
		return ResponseEntity.ok(participantService.updateParticipantDetails(id, request));
	}

	@PostMapping("/{id}/documents")
	public ResponseEntity<DocumentResponseDto> uploadDocument(@PathVariable Long id,
			@Valid @RequestBody DocumentUploadRequestDto request) {
		log.info("Received request to upload document type {} for profile ID: {}", request.getDocumentType(), id);
		return new ResponseEntity<>(participantService.uploadDocument(id, request), HttpStatus.CREATED);
	}

	@GetMapping("/{id}/documents")
	public ResponseEntity<List<DocumentResponseDto>> getParticipantDocuments(@PathVariable Long id) {
		log.info("Received request to fetch documents for profile ID: {}", id);
		return ResponseEntity.ok(participantService.getParticipantDocuments(id));
	}

	@PutMapping("/{id}/verification-status")
	public ResponseEntity<Void> updateParticipantStatus(@PathVariable Long id,
			@Valid @RequestBody VerificationStatusUpdateDto statusDto) {
		log.info("Received request to update verification status to {} for profile ID: {}", statusDto.getStatus(), id);
		participantService.updateParticipantStatus(id, statusDto);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{participantId}/documents/{documentId}/status")
	public ResponseEntity<Void> updateDocumentStatus(@PathVariable Long participantId, @PathVariable Long documentId,
			@Valid @RequestBody VerificationStatusUpdateDto statusDto) {
		log.info("Received request to update document ID: {} status to {} for profile ID: {}", documentId, statusDto.getStatus(), participantId);
		participantService.updateDocumentStatus(documentId, statusDto);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{id}/basic")
	public ResponseEntity<ParticipantBasicDTO> getParticipantBasic(@PathVariable Long id) {
		log.info("Received internal Feign request to fetch basic details for profile ID: {}", id);
		return ResponseEntity.ok(participantService.getParticipantBasic(id));
	}
}