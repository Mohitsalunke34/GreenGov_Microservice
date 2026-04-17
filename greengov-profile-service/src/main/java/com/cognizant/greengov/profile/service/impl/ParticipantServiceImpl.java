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
import com.cognizant.greengov.profile.dto.UserProfileDTO;
import com.cognizant.greengov.profile.dto.VerificationStatusUpdateDto;
import com.cognizant.greengov.profile.exception.ResourceNotFoundException;
import com.cognizant.greengov.profile.model.VerificationStatus;
import com.cognizant.greengov.profile.model.register_login.Document;
import com.cognizant.greengov.profile.model.register_login.ParticipantProfile;
import com.cognizant.greengov.profile.repository.DocumentRepository;
import com.cognizant.greengov.profile.repository.ParticipantProfileRepository;
import com.cognizant.greengov.profile.service.ParticipantService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {

	private final ParticipantProfileRepository profileRepository;
	private final DocumentRepository documentRepository;
	private final UserClient userClient;

	@Override
	@Transactional
	public EntityProfileResponseDto registerParticipant(ParticipantRegistrationRequestDto request) {

		List<UserProfileDTO> users;
		try {
			users = userClient.getUserByPrimaryRole();
		} catch (FeignException e) {
//			throw new ResourceNotFoundException("Unable to communicate with User Service");
			throw e;

		}

		// ✅ Just validate user existence & role
		users.stream().filter(u -> u.getId().equals(request.getUserId())).findFirst()
				.orElseThrow(() -> new ResourceNotFoundException(
						"User not found or not eligible (Citizen/Business) with ID: " + request.getUserId()));

		ParticipantProfile profile = ParticipantProfile.builder().userId(request.getUserId())
				.entityType(request.getEntityType()).legalName(request.getLegalName()).address(request.getAddress())
				.contactInfoJson(request.getContactInfo()).status(VerificationStatus.PENDING)
				.documents(new ArrayList<>()).build();

		ParticipantProfile savedProfile = profileRepository.save(profile);

		return mapToProfileResponseDto(savedProfile);
	}

	@Override
	public EntityProfileResponseDto getParticipantDetails(Long id) {
		ParticipantProfile profile = profileRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Profile not found with ID: " + id));

		return mapToProfileResponseDto(profile);
	}

	@Override
	@Transactional
	public EntityProfileResponseDto updateParticipantDetails(Long id, ParticipantUpdateRequestDto request) {
		ParticipantProfile profile = profileRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Profile not found with ID: " + id));

		profile.setLegalName(request.getLegalName());
		profile.setAddress(request.getAddress());
		profile.setContactInfoJson(request.getContactInfo());

		ParticipantProfile updatedProfile = profileRepository.save(profile);

		return mapToProfileResponseDto(updatedProfile);
	}

	@Override
	@Transactional
	public DocumentResponseDto uploadDocument(Long profileId, DocumentUploadRequestDto request) {

		ParticipantProfile profile = profileRepository.findById(profileId)
				.orElseThrow(() -> new ResourceNotFoundException("Profile not found with ID: " + profileId));

		Document document = Document
				.builder().documentType(request.getDocumentType().name()).fileUrl("simulated/path/to/"
						+ request.getDocumentType().name() + "_" + System.currentTimeMillis() + ".pdf")
				.profile(profile).verificationStatus(VerificationStatus.PENDING).build();

		Document savedDocument = documentRepository.save(document);

		return mapToDocumentResponseDto(savedDocument);
	}

	@Override
	public List<DocumentResponseDto> getParticipantDocuments(Long profileId) {
		ParticipantProfile profile = profileRepository.findById(profileId)
				.orElseThrow(() -> new ResourceNotFoundException("Profile not found with ID: " + profileId));

		return profile.getDocuments().stream().map(this::mapToDocumentResponseDto).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void updateParticipantStatus(Long profileId, VerificationStatusUpdateDto statusDto) {
		ParticipantProfile profile = profileRepository.findById(profileId)
				.orElseThrow(() -> new ResourceNotFoundException("Profile not found with ID: " + profileId));

		profile.setStatus(statusDto.getStatus());
		profileRepository.save(profile);
	}

	@Override
	@Transactional
	public void updateDocumentStatus(Long documentId, VerificationStatusUpdateDto statusDto) {
		Document document = documentRepository.findById(documentId)
				.orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + documentId));

		document.setVerificationStatus(statusDto.getStatus());
		documentRepository.save(document);
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

	private DocumentResponseDto mapToDocumentResponseDto(Document document) {
		DocumentResponseDto response = new DocumentResponseDto();
		response.setId(document.getId());
		response.setDocumentType(com.cognizant.greengov.profile.model.DocumentType.valueOf(document.getDocumentType()));
		response.setFileUri(document.getFileUrl());
		response.setUploadedDate(LocalDateTime.now());
		response.setVerificationStatus(document.getVerificationStatus() != null ? document.getVerificationStatus()
				: VerificationStatus.PENDING);
		return response;
	}
}