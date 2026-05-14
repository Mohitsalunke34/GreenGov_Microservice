package com.cognizant.greengov.profile.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cognizant.greengov.profile.client.UserClient;
import com.cognizant.greengov.profile.dto.DocumentResponseDto;
import com.cognizant.greengov.profile.dto.DocumentUploadRequestDto;
import com.cognizant.greengov.profile.dto.EntityProfileResponseDto;
import com.cognizant.greengov.profile.dto.ParticipantRegistrationRequestDto;
import com.cognizant.greengov.profile.dto.ParticipantUpdateRequestDto;
import com.cognizant.greengov.profile.dto.VerificationStatusUpdateDto;
import com.cognizant.greengov.profile.dto.clients.ParticipantBasicDTO;
import com.cognizant.greengov.profile.exception.ResourceNotFoundException;
import com.cognizant.greengov.profile.model.VerificationStatus;
import com.cognizant.greengov.profile.model.register_login.Document;
import com.cognizant.greengov.profile.model.register_login.ParticipantProfile;
import com.cognizant.greengov.profile.repository.DocumentRepository;
import com.cognizant.greengov.profile.repository.ParticipantProfileRepository;
import com.cognizant.greengov.profile.service.ParticipantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipantServiceImpl implements ParticipantService {

	private final ParticipantProfileRepository profileRepository;
	private final DocumentRepository documentRepository;
	private final UserClient userClient;

	@Override
	@Transactional
	public EntityProfileResponseDto registerParticipant(ParticipantRegistrationRequestDto request) {
		log.debug("Executing registerParticipant logic for userId: {}", request.getUserId());

		ParticipantProfile profile = ParticipantProfile.builder().userId(request.getUserId())
				.entityType(request.getEntityType()).legalName(request.getLegalName()).address(request.getAddress())
				.contactInfoJson(request.getContactInfo()).status(VerificationStatus.PENDING).build();

		ParticipantProfile savedProfile = profileRepository.save(profile);
		log.debug("Participant profile saved to database successfully with ID: {}", savedProfile.getId());

		return mapToProfileResponseDto(savedProfile);
	}

	@Override
	public EntityProfileResponseDto getParticipantDetails(Long participantId) {
		log.debug("Fetching participant details from database for profile ID: {}", participantId);
		ParticipantProfile profile = profileRepository.findById(participantId).orElseThrow(() -> {
			log.error("Failed to find participant profile with ID: {}", participantId);
			return new ResourceNotFoundException("Profile not found with ID: " + participantId);
		});
		return mapToProfileResponseDto(profile);
	}

	@Override
	public EntityProfileResponseDto getParticipantByUserId(Long userId) {
		log.debug("Fetching participant details from database for user ID: {}", userId);
		ParticipantProfile profile = profileRepository.findByUserId(userId).orElseThrow(() -> {
			log.error("Failed to find participant profile for User ID: {}", userId);
			return new ResourceNotFoundException("Profile not found for User ID: " + userId);
		});
		return mapToProfileResponseDto(profile);
	}

	@Override
	@Transactional
	public EntityProfileResponseDto updateParticipantDetails(Long participantId, ParticipantUpdateRequestDto request) {
		log.debug("Updating participant details for profile ID: {}", participantId);
		ParticipantProfile profile = profileRepository.findById(participantId).orElseThrow(() -> {
			log.error("Update failed. Profile not found with ID: {}", participantId);
			return new ResourceNotFoundException("Profile not found with ID: " + participantId);
		});

		if (request.getLegalName() != null) {
			profile.setLegalName(request.getLegalName());
		}
		if (request.getAddress() != null) {
			profile.setAddress(request.getAddress());
		}
		if (request.getContactInfo() != null) {
			profile.setContactInfoJson(request.getContactInfo());
		}

		ParticipantProfile updatedProfile = profileRepository.save(profile);
		log.debug("Participant details updated successfully for profile ID: {}", updatedProfile.getId());
		return mapToProfileResponseDto(updatedProfile);
	}

	@Override
	@Transactional
	public DocumentResponseDto uploadDocument(Long participantId, DocumentUploadRequestDto request) {
		log.debug("Processing document upload for profile ID: {}", participantId);
		ParticipantProfile profile = profileRepository.findById(participantId).orElseThrow(() -> {
			log.error("Document upload failed. Profile not found with ID: {}", participantId);
			return new ResourceNotFoundException("Profile not found with ID: " + participantId);
		});

		Document document = new Document();
		document.setProfile(profile);
		document.setDocumentType(request.getDocumentType().name());
		document.setFileUrl(request.getBase64Content());
		document.setVerificationStatus(VerificationStatus.PENDING);

		Document savedDocument = documentRepository.save(document);
		log.debug("Document saved successfully with ID: {}", savedDocument.getId());

		return mapToDocumentResponseDto(savedDocument);
	}

	@Override
	public List<DocumentResponseDto> getParticipantDocuments(Long participantId) {
		log.debug("Retrieving documents for profile ID: {}", participantId);
		if (!profileRepository.existsById(participantId)) {
			log.error("Document retrieval failed. Profile not found with ID: {}", participantId);
			throw new ResourceNotFoundException("Profile not found with ID: " + participantId);
		}

		List<Document> documents = documentRepository.findByProfileId(participantId);
		log.debug("Retrieved {} documents for profile ID: {}", documents.size(), participantId);
		return documents.stream().map(this::mapToDocumentResponseDto).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void updateParticipantStatus(Long participantId, VerificationStatusUpdateDto statusDto) {
		log.debug("Updating verification status to {} for profile ID: {}", statusDto.getStatus(), participantId);
		ParticipantProfile profile = profileRepository.findById(participantId).orElseThrow(() -> {
			log.error("Status update failed. Profile not found with ID: {}", participantId);
			return new ResourceNotFoundException("Profile not found with ID: " + participantId);
		});

		profile.setStatus(statusDto.getStatus());
		profileRepository.save(profile);
		log.debug("Verification status updated successfully for profile ID: {}", participantId);
	}

	@Override
	@Transactional
	public void updateDocumentStatus(Long documentId, VerificationStatusUpdateDto statusDto) {
		log.debug("Updating document status to {} for document ID: {}", statusDto.getStatus(), documentId);
		Document document = documentRepository.findById(documentId).orElseThrow(() -> {
			log.error("Document status update failed. Document not found with ID: {}", documentId);
			return new ResourceNotFoundException("Document not found with ID: " + documentId);
		});

		document.setVerificationStatus(statusDto.getStatus());
		documentRepository.save(document);
		log.debug("Document status updated successfully for document ID: {}", documentId);
	}

	@Override
	public ParticipantBasicDTO getParticipantBasic(Long participantId) {
		log.debug("Fetching basic participant information for profile ID: {}", participantId);
		ParticipantProfile profile = profileRepository.findById(participantId).orElseThrow(() -> {
			log.error("Basic info retrieval failed. Profile not found with ID: {}", participantId);
			return new ResourceNotFoundException("Profile not found with ID: " + participantId);
		});

		ParticipantBasicDTO dto = new ParticipantBasicDTO();
		dto.setId(profile.getId());
		dto.setVerified(profile.getStatus() == VerificationStatus.VERIFIED);

		return dto;
	}

	private EntityProfileResponseDto mapToProfileResponseDto(ParticipantProfile profile) {
		EntityProfileResponseDto response = new EntityProfileResponseDto();
		response.setId(profile.getId());
		response.setLegalName(profile.getLegalName());
		response.setEntityType(profile.getEntityType());
		response.setAddress(profile.getAddress());
		response.setContactInfo(profile.getContactInfoJson());
		response.setStatus(profile.getStatus());

		if (profile.getDocuments() != null) {
			response.setDocuments(
					profile.getDocuments().stream().map(this::mapToDocumentResponseDto).collect(Collectors.toList()));
		} else {
			response.setDocuments(new ArrayList<>());
		}

		return response;
	}

	@Override
	public List<ParticipantBasicDTO> getAllParticipantBasics() {
		return profileRepository.findAll().stream().map(p -> new ParticipantBasicDTO(p.getId(), p.getLegalName()))
				.toList();
	}

	@Override
	public List<EntityProfileResponseDto> getAllParticipants() {
		log.debug("Fetching all participant profiles from database for compliance queue");
		List<ParticipantProfile> profiles = profileRepository.findAll();
		return profiles.stream().map(this::mapToProfileResponseDto).collect(Collectors.toList());
	}

	private DocumentResponseDto mapToDocumentResponseDto(Document document) {
		DocumentResponseDto response = new DocumentResponseDto();
		response.setId(document.getId());
		response.setDocumentType(com.cognizant.greengov.profile.model.DocumentType.valueOf(document.getDocumentType()));
		response.setFileUri(document.getFileUrl());
		response.setUploadedDate(LocalDateTime.now());
		response.setVerificationStatus(document.getVerificationStatus());
		return response;
	}
}