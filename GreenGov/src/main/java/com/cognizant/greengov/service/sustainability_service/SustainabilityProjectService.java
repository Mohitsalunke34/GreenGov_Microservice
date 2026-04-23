//package com.cognizant.greengov.service.sustainability_service;
//
//import java.util.List;
//
//import com.cognizant.greengov.model.sustainability_renewable_proj.SustainabilityProject;
//
//public interface SustainabilityProjectService {
//
//	SustainabilityProject createProject(SustainabilityProject project);
//
//	List<SustainabilityProject> getProjectsByStatus(String status);
//}
package com.cognizant.greengov.service.sustainability_service;

import java.util.List;

import com.cognizant.greengov.dto.sustainability_dto.SustainabilityRequest;
import com.cognizant.greengov.dto.sustainability_dto.SustainabilityResponse;
import com.cognizant.greengov.exception.ProjectNotFound;
import com.cognizant.greengov.model.sustainability_renewable_proj.SustainabilityProject;

public interface SustainabilityProjectService {

	SustainabilityProject createProject(SustainabilityProject project);

	List<SustainabilityProject> getProjectsByStatus(String status);
	

	SustainabilityResponse updateProjectStatus(Long projectId, String status) throws ProjectNotFound;

	SustainabilityResponse getProjectById(Long projectId) throws ProjectNotFound;

	List<SustainabilityResponse> getAllProjects();

	String deleteProject(Long projectId) throws ProjectNotFound;
}
