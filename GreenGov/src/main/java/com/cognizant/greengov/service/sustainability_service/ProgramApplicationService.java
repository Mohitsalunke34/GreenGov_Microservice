//package com.cognizant.greengov.service.sustainability_service;
//
//import com.cognizant.greengov.dto.sustainability_dto.ProgramApplicationRequestDTO;
//
//public interface ProgramApplicationService {
//
//    void apply(Long participantId, ProgramApplicationRequestDTO dto);
//}

package com.cognizant.greengov.service.sustainability_service;

import java.util.List;

import com.cognizant.greengov.dto.sustainability_dto.ProgramApplicationRequestDTO;
import com.cognizant.greengov.dto.sustainability_dto.ProgramApplicationResponseDTO;
import com.cognizant.greengov.exception.ProjectNotFound;

public interface ProgramApplicationService {

    void apply(Long participantId, ProgramApplicationRequestDTO dto);
    ProgramApplicationResponseDTO approveApplication(Long applicationId) throws ProjectNotFound;
    ProgramApplicationResponseDTO rejectApplication(Long applicationId) throws ProjectNotFound;
    ProgramApplicationResponseDTO getApplicationById(Long applicationId) throws ProjectNotFound;
    List<ProgramApplicationResponseDTO> getAllApplications();
//    List<ProgramApplicationResponseDTO> getApplicationsByEntity(Long entityId);
}