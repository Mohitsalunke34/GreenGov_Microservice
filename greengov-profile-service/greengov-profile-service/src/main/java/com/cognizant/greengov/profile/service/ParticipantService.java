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

	EntityProfileResponseDto getParticipantDetails(Long participantId);

	EntityProfileResponseDto getParticipantByUserId(Long userId);

	EntityProfileResponseDto updateParticipantDetails(Long participantId, ParticipantUpdateRequestDto request);

	DocumentResponseDto uploadDocument(Long participantId, DocumentUploadRequestDto request);

	List<DocumentResponseDto> getParticipantDocuments(Long participantId);

	void updateParticipantStatus(Long participantId, VerificationStatusUpdateDto statusDto);

	void updateDocumentStatus(Long documentId, VerificationStatusUpdateDto statusDto);

	ParticipantBasicDTO getParticipantBasic(Long participantId);
}