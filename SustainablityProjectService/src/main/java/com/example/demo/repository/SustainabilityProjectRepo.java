package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.SustainabilityProject;

public interface SustainabilityProjectRepo extends JpaRepository<SustainabilityProject, Long> {

	List<SustainabilityProject> findByStatus(String status);

	Long countBystatus(String status);
}