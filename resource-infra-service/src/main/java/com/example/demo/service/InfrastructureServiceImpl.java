package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.client.ProjectClient;
import com.example.demo.dto.InfrastructureCreateRequestDTO;
import com.example.demo.dto.InfrastructureResponseDTO;
import com.example.demo.dto.InfrastructureStatusDTO;
import com.example.demo.dto.ProjectResponseDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Infrastructure;
import com.example.demo.repository.InfrastructureRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class InfrastructureServiceImpl implements InfrastructureService {

	private static final Logger logger = LoggerFactory.getLogger(InfrastructureServiceImpl.class);
	
	private final InfrastructureRepository infraRepository;
	private final ProjectClient projectClient;

	@Override
	public InfrastructureResponseDTO addInfrastructure(InfrastructureCreateRequestDTO dto) {
		logger.info("Adding new infrastructure for Project ID: {}", dto.getProjectId());

		ProjectResponseDTO project = fetchProject(dto.getProjectId());

		Infrastructure infra = Infrastructure.builder()
				.projectId(dto.getProjectId())
				.type(dto.getType())
				.location(dto.getLocation())
				.capacity(dto.getCapacity())
				.status("Planned") // Default status
				.build();

		Infrastructure saved = infraRepository.save(infra);
		return mapToResponseDTO(saved);
	}

	@Override
	public InfrastructureResponseDTO updateInfrastructure(long infraId, InfrastructureCreateRequestDTO dto) {
		Infrastructure existing = infraRepository.findById(infraId)
				.orElseThrow(() -> new ResourceNotFoundException("Infrastructure not found with ID: " + infraId));

		// Verify target project exists
		fetchProject(dto.getProjectId());

		existing.setProjectId(dto.getProjectId());
		existing.setType(dto.getType());
		existing.setLocation(dto.getLocation());
		existing.setCapacity(dto.getCapacity());

		return mapToResponseDTO(infraRepository.save(existing));
	}

	@Override
	public InfrastructureResponseDTO updateStatus(InfrastructureStatusDTO dto) {
		Infrastructure infra = infraRepository.findById(dto.getInfraId())
				.orElseThrow(() -> new ResourceNotFoundException("Infrastructure not found"));

		infra.setStatus(dto.getStatus());
		return mapToResponseDTO(infraRepository.save(infra));
	}

	@Override
	@Transactional(readOnly = true)
	public InfrastructureResponseDTO getInfrastructure(long infraId) {
		return infraRepository.findById(infraId)
				.map(this::mapToResponseDTO)
				.orElseThrow(() -> new ResourceNotFoundException("Infrastructure not found"));
	}

	@Override
	@Transactional(readOnly = true)
	public List<InfrastructureResponseDTO> getAllInfrastructure() {
		return infraRepository.findAll().stream()
				.map(this::mapToResponseDTO)
				.collect(Collectors.toList());
	}

	@Override
	public void deleteInfrastructure(long infraId) {
		if (!infraRepository.existsById(infraId)) {
			throw new ResourceNotFoundException("Cannot delete: ID " + infraId + " not found.");
		}
		infraRepository.deleteById(infraId);
	}

	/**
	 * Helper to call the Project Microservice
	 */
	private ProjectResponseDTO fetchProject(long projectId) {
		try {
			ResponseEntity<ProjectResponseDTO> response = projectClient.getProjectById(projectId);
			if (response.getBody() == null) {
				throw new ResourceNotFoundException("Project not found with ID: " + projectId);
			}
			return response.getBody();
		} catch (Exception e) {
			logger.error("Error communicating with Project Service: {}", e.getMessage());
			throw new RuntimeException("Project verification failed for ID: " + projectId);
		}
	}

	private InfrastructureResponseDTO mapToResponseDTO(Infrastructure entity) {
		InfrastructureResponseDTO response = new InfrastructureResponseDTO();
		response.setInfraId(entity.getInfraId());
		response.setProjectId(entity.getProjectId());
		response.setType(entity.getType());
		response.setLocation(entity.getLocation());
		response.setCapacity(entity.getCapacity());
		response.setStatus(entity.getStatus());
		return response;
	}
}