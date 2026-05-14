package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

	Optional<EnergyProgram> findByTitle(String title);

	// Used for timeline dashboards

	List<EnergyProgram> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate startDate, LocalDate endDate);

//	Optional findById(Long programId);
	// ✅ Count programs by status (ACTIVE / INACTIVE / CLOSED)
	long countByStatus(String status);

	// ✅ Sum of total budget
	@Query("SELECT COALESCE(SUM(p.budget), 0) FROM EnergyProgram p")
	Double sumTotalBudget();

	// ✅ Sum of remaining program budget
	@Query("SELECT COALESCE(SUM(p.remainingProgramBudget), 0) FROM EnergyProgram p")
	Double sumRemainingBudget();

}