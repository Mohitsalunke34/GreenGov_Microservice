package com.cognizant.greengov.repository.infra_resource_repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.greengov.model.resource_infrastructure.Infrastructure;
import com.cognizant.greengov.model.sustainability_renewable_proj.SustainabilityProject;

public interface InfrastructureRepository
        extends JpaRepository<Infrastructure, Long> {

    List<Infrastructure> findByProject(SustainabilityProject project);
}