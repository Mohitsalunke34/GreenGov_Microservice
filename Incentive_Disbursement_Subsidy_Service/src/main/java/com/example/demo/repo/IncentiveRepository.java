package com.example.demo.repo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Incentive;

@Repository
public interface IncentiveRepository extends JpaRepository<Incentive, Long> {

    // 1. Changed from ProgramApplication object to Long applicationId
    Optional<Incentive> findByApplicationId(Long applicationId);

    // 2. Changed from ParticipantProfile object to Long beneficiaryId
    List<Incentive> findByBeneficiaryId(Long beneficiaryId);

    // 3. Changed from EnergyProgram object to Long programId
    List<Incentive> findByProgramId(Long programId);
    
    // 4. JpaRepository already provides findById(Long id), 
    // but if you prefer your custom naming:
    Optional<Incentive> findByIncentiveId(Long incentiveId);
    
    // 5. Standard delete by ID
    void deleteByIncentiveId(Long incentiveId);
    
    // 6. Updated Query to use the simple long field 'programId'
    @Query("SELECT SUM(i.amount) FROM Incentive i WHERE i.programId = :programId")
    BigDecimal sumAmountByProgramId(@Param("programId") Long programId);
}