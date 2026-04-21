package com.cognizant.greengov.profile.service;

import java.util.List;

import com.cognizant.greengov.profile.dto.DocumentResponseDto;
import com.cognizant.greengov.profile.dto.DocumentUploadRequestDto;
import com.cognizant.greengov.profile.dto.EntityProfileResponseDto;
import com.cognizant.greengov.profile.dto.ParticipantRegistrationRequestDto;
import com.cognizant.greengov.profile.dto.ParticipantUpdateRequestDto;
import com.cognizant.greengov.profile.dto.VerificationStatusUpdateDto;
import com.cognizant.greengov.profile.dto.clients.ParticipantBasicDTO;

public interface ParticipantService {
	EntityProfileResponseDto registerParticipant(ParticipantRegistrationRequestDto request);

	EntityProfileResponseDto getParticipantDetails(Long id);

	EntityProfileResponseDto updateParticipantDetails(Long id, ParticipantUpdateRequestDto request);

	DocumentResponseDto uploadDocument(Long profileId, DocumentUploadRequestDto request);

	List<DocumentResponseDto> getParticipantDocuments(Long profileId);

	void updateParticipantStatus(Long profileId, VerificationStatusUpdateDto statusDto);

	void updateDocumentStatus(Long documentId, VerificationStatusUpdateDto statusDto);

	// for Compliance microservice client
	ParticipantBasicDTO getParticipantBasic(Long participantId);
}