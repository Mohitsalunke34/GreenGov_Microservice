//package com.cognizant.greengov.service.sustainability_service;
//
//import java.util.List;
//
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.validation.annotation.Validated;
//
//import com.cognizant.greengov.dto.sustainability_dto.EnergyProgramDTO;
//import com.cognizant.greengov.model.sustainability_renewable_proj.EnergyProgram;
//import com.cognizant.greengov.modelmapper.EnergyProgramMapper;
//import com.cognizant.greengov.repository.sustainability_repo.EnergyProgramRepository;
//
//@Service
//
//public class EnergyProgramServiceImpl implements EnergyProgramService {
//
//	private final EnergyProgramRepository programRepo;
//
//	public EnergyProgramServiceImpl(EnergyProgramRepository programRepo) {
//		this.programRepo = programRepo;
//	}
//
//	@Override
//	public List<EnergyProgramDTO> getAllPrograms() {
//		return programRepo.findAll().stream().map(EnergyProgramMapper::toDTO).toList();
//	}
//
//	@Override
//	@Transactional
//	public EnergyProgramDTO createprogram(@Validated EnergyProgramDTO dto) {
//
//		EnergyProgram program = new EnergyProgram();
//		program.setTitle(dto.getTitle());
//		program.setDescription(dto.getDescription());
//		program.setStartDate(dto.getStartDate());
//		program.setEndDate(dto.getEndDate());
//		program.setBudget(dto.getBudget());
//		program.setStatus(dto.getStatus());
//		program.setCreatedBy("System");
//		program.setUpdatedBy("System");
//
//		EnergyProgram saved = programRepo.save(program);
//		return EnergyProgramMapper.toDTO(saved);
//
//	}
//
//}

package com.cognizant.greengov.service.sustainability_service;
 
 
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.cognizant.greengov.dto.sustainability_dto.EnergyProgramDTO;
import com.cognizant.greengov.dto.sustainability_dto.EnergyProgramResponseDTO;
import com.cognizant.greengov.exception.ProjectNotFound;
import com.cognizant.greengov.model.sustainability_renewable_proj.EnergyProgram;
import com.cognizant.greengov.modelmapper.EnergyProgramMapper;
import com.cognizant.greengov.repository.sustainability_repo.EnergyProgramRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
 
//Service implementation for managing Energy Programs.
//This class handles the business logic for creating, updating, retrieving, and deleting energy-related initiatives.
@Service
@Slf4j
@RequiredArgsConstructor
public class EnergyProgramServiceImpl implements EnergyProgramService {
 
	private final EnergyProgramRepository programRepo;
 
	//Retrieves all energy programs stored in the database
	//Converts the list of entities into a list of Response DTOs.
	@Override
	public List<EnergyProgramResponseDTO> getAllPrograms() {
		List<EnergyProgramResponseDTO> dto = programRepo.findAll().stream().map(this::mapToResponse).toList();
		return dto;
	}
 
	/**
	 * Creates a new energy program.
	 * Sets system-level metadata (createdBy, updatedBy) before persisting.
	 * @param dto The validated program details from the request.
	 */
	@Override
	@Transactional
	public EnergyProgramDTO createprogram(@Validated EnergyProgramDTO dto) {
 
		EnergyProgram program = new EnergyProgram();
		program.setTitle(dto.getTitle());
		program.setDescription(dto.getDescription());
		program.setStartDate(dto.getStartDate());
		program.setEndDate(dto.getEndDate());
		program.setBudget(dto.getBudget());
		program.setStatus(dto.getStatus());
		program.setCreatedBy("System");
		program.setUpdatedBy("System");
 
		EnergyProgram saved = programRepo.save(program);
		return EnergyProgramMapper.toDTO(saved);
 
	}
	/**
	 * Updates the full details of an existing energy program.
	 * @throws ProjectNotFound if the ID provided does not exist in the database.
	 */
	@Override
    public EnergyProgramResponseDTO updateProgram(Long programId, EnergyProgramDTO request) throws ProjectNotFound {
        log.info("Attempting to update Energy Program ID: {}", programId);
 
        EnergyProgram existing = programRepo.findById(programId)
                .orElseThrow(() -> {
                    log.error("Update failed: Program ID {} not found.", programId);
                    return new ProjectNotFound("Energy Program not found with ID: " + programId);
                });
        existing.setTitle(request.getTitle());
        existing.setDescription(request.getDescription());
        existing.setStartDate(request.getStartDate());
        existing.setEndDate(request.getEndDate());
        existing.setBudget(request.getBudget());
        existing.setStatus(request.getStatus());
 
        EnergyProgram updated = programRepo.save(existing);
        log.info("Successfully updated Energy Program ID: {}", programId);
        return mapToResponse(updated);
    }
	//Updates only the status of an energy program (Partial update).
    @Override
    public EnergyProgramResponseDTO updateProgramStatus(Long programId, String status) throws ProjectNotFound {
        log.info("Patching status for program ID {}: {}", programId, status);
        EnergyProgram existing = programRepo.findById(programId)
                .orElseThrow(() -> new ProjectNotFound("Program not found."));
        existing.setStatus(status);
        return mapToResponse(programRepo.save(existing));
    }
 
    /**
	 * Deletes an energy program by its ID.
	 * Checks for existence before deletion to provide clear error feedback.
	 */
    @Override
    public String deleteProgram(Long programId) throws ProjectNotFound {
        log.info("Request to delete Energy Program ID: {}", programId);
 
        if (!programRepo.existsById(programId)) {
            log.warn("Deletion failed: ID {} does not exist.", programId);
            throw new ProjectNotFound("Deletion failed. ID not found.");
        }
        programRepo.deleteById(programId);
        log.info("Successfully deleted Energy Program ID: {}", programId);
        return "Energy Program deleted successfully.";
    }
 
    //Retrieves a single energy program by its unique ID.
    @Override
    public EnergyProgramResponseDTO getProgramById(Long programId) throws ProjectNotFound {
        log.debug("Fetching Energy Program ID: {}", programId); // Use debug for frequent lookups
 
        return programRepo.findById(programId)
                .map(this::mapToResponse)
                .orElseThrow(() -> {
                    log.warn("Get failed: Program ID {} not found.", programId);
                    return new ProjectNotFound("Energy Program ID not found.");
                });
    }
    /**
	 * Internal helper method to map the EnergyProgram entity to EnergyProgramResponseDTO.
	 * Uses the Builder pattern for clean and readable DTO construction.
	 */
    private EnergyProgramResponseDTO mapToResponse(EnergyProgram entity) {
    	if (entity == null) {
            return null;
        }
        return EnergyProgramResponseDTO.builder()
                .programId(entity.getProgramId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .budget(entity.getBudget())
                .status(entity.getStatus())
                // Add any other specific response fields here
                .build();
    }
 
	
 
}