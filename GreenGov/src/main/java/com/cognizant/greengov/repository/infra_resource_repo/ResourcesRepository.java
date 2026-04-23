package com.cognizant.greengov.repository.infra_resource_repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.greengov.model.resource_infrastructure.Resources;
import com.cognizant.greengov.model.sustainability_renewable_proj.SustainabilityProject;

public interface ResourcesRepository
        extends JpaRepository<Resources, Long> {

    List<Resources> findByProject(SustainabilityProject project);
}