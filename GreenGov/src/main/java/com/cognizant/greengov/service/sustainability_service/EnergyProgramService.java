//package com.cognizant.greengov.service.sustainability_service;
//
//import java.util.List;
//
//import com.cognizant.greengov.dto.sustainability_dto.EnergyProgramDTO;
//
//public interface EnergyProgramService {
//
//	List<EnergyProgramDTO> getAllPrograms();
//	EnergyProgramDTO createprogram(EnergyProgramDTO EnPr);
//
//}

package com.cognizant.greengov.service.sustainability_service;

import java.util.List;

import com.cognizant.greengov.dto.sustainability_dto.EnergyProgramDTO;
import com.cognizant.greengov.dto.sustainability_dto.EnergyProgramResponseDTO;
//import com.cognizant.greengov.dto.sustainability_dto.EnergyProgramResponseDTO;
import com.cognizant.greengov.exception.ProjectNotFound;

public interface EnergyProgramService {

	List<EnergyProgramResponseDTO> getAllPrograms();
	EnergyProgramDTO createprogram(EnergyProgramDTO EnPr);
	EnergyProgramResponseDTO updateProgram(Long programId, EnergyProgramDTO request) throws ProjectNotFound;
    EnergyProgramResponseDTO updateProgramStatus(Long programId, String status) throws ProjectNotFound;
    String deleteProgram(Long programId) throws ProjectNotFound;
    EnergyProgramResponseDTO getProgramById(Long programId) throws ProjectNotFound;

}