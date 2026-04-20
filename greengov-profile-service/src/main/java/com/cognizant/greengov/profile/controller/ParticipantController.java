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
import com.cognizant.greengov.profile.service.ParticipantService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/participants")
@RequiredArgsConstructor
public class ParticipantController {

	private final ParticipantService participantService;

	@PostMapping("/register")
	public ResponseEntity<EntityProfileResponseDto> registerParticipant(
			@Valid @RequestBody ParticipantRegistrationRequestDto request) {

		EntityProfileResponseDto response = participantService.registerParticipant(request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<EntityProfileResponseDto> getParticipantDetails(@PathVariable Long id) {
		return ResponseEntity.ok(participantService.getParticipantDetails(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<EntityProfileResponseDto> updateParticipantDetails(@PathVariable Long id,
			@Valid @RequestBody ParticipantUpdateRequestDto request) {
		return ResponseEntity.ok(participantService.updateParticipantDetails(id, request));
	}

	@PostMapping("/{id}/documents")
	public ResponseEntity<DocumentResponseDto> uploadDocument(@PathVariable Long id,
			@Valid @RequestBody DocumentUploadRequestDto request) {
		return new ResponseEntity<>(participantService.uploadDocument(id, request), HttpStatus.CREATED);
	}

	@GetMapping("/{id}/documents")
	public ResponseEntity<List<DocumentResponseDto>> getParticipantDocuments(@PathVariable Long id) {
		return ResponseEntity.ok(participantService.getParticipantDocuments(id));
	}

	@PutMapping("/{id}/verification-status")
	public ResponseEntity<Void> updateParticipantStatus(@PathVariable Long id,
			@Valid @RequestBody VerificationStatusUpdateDto statusDto) {
		participantService.updateParticipantStatus(id, statusDto);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{participantId}/documents/{documentId}/status")
	public ResponseEntity<Void> updateDocumentStatus(@PathVariable Long participantId, @PathVariable Long documentId,
			@Valid @RequestBody VerificationStatusUpdateDto statusDto) {
		participantService.updateDocumentStatus(documentId, statusDto);
		return ResponseEntity.ok().build();
	}
}