package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.EnergyProgram;

public interface EnergyProgramRepository extends JpaRepository<EnergyProgram, Long> {

	// Used for public / citizen listing
	// Example: show all ACTIVE programs

	List<EnergyProgram> findByStatus(String status);

	// Used for program lifecycle views
	// Example: programs starting in future

	List<EnergyProgram> findByStartDateAfter(LocalDate date);

	// Used for reporting & archival
	// Example: completed programs

	List<EnergyProgram> findByEndDateBefore(LocalDate date);

	// Used for timeline dashboards

	List<EnergyProgram> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate startDate, LocalDate endDate);
	
//	Optional findById(Long programId);
	
	
	
}