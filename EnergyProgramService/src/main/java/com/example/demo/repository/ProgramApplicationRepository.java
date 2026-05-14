package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.ProgramApplication;
import com.example.demo.model.EnergyProgram;

public interface ProgramApplicationRepository extends JpaRepository<ProgramApplication, Long> {

	List<ProgramApplication> findByApplicantId(Long applicantId);

	List<ProgramApplication> findByProgram(EnergyProgram program);

	Optional<ProgramApplication> findByApplicantIdAndProgram(Long applicantId, EnergyProgram program);

	List<ProgramApplication> findByApplicantIdAndStatus(Long applicantId, String status);

}