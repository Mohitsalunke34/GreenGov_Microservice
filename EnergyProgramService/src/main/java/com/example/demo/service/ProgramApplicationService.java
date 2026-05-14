package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.ProgramApplicationRequestDto;
import com.example.demo.dto.ProgramApplicationResponseDto;
import com.example.demo.dto.client_dto.ApprovedApplicationLookupDTO;
import com.example.demo.exception.ProjectNotFound;

public interface ProgramApplicationService {

	ProgramApplicationResponseDto apply(ProgramApplicationRequestDto request);

	ProgramApplicationResponseDto approveApplication(Long applicationId) throws ProjectNotFound;

	ProgramApplicationResponseDto rejectApplication(Long applicationId) throws ProjectNotFound;

	ProgramApplicationResponseDto getApplicationById(Long applicationId) throws ProjectNotFound;

	List<ProgramApplicationResponseDto> getAllApplications();

	List<ProgramApplicationResponseDto> getApplicationsByApplicant(Long applicantId);

	List<ApprovedApplicationLookupDTO> getApprovedApplicationsByParticipant(Long participantId);
}