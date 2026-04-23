package com.cognizant.greengov.repository.sustainability_repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.greengov.model.register_login.UserAccount;
import com.cognizant.greengov.model.sustainability_renewable_proj.EnergyProgram;

public interface EnergyProgramRepository extends JpaRepository<EnergyProgram, Long> {

	// Used for public / citizen listing
	// Example: show all ACTIVE programs

	List<EnergyProgram> findByStatus(String status);

	// Used for admin / officer dashboards
	// Example: programs created by a particular admin or officer

	List<EnergyProgram> findByOwner(UserAccount owner);

	// Used for program lifecycle views
	// Example: programs starting in future

	List<EnergyProgram> findByStartDateAfter(LocalDate date);

	// Used for reporting & archival
	// Example: completed programs

	List<EnergyProgram> findByEndDateBefore(LocalDate date);

	// Used for timeline dashboards

	List<EnergyProgram> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate startDate, LocalDate endDate);
	
	
}