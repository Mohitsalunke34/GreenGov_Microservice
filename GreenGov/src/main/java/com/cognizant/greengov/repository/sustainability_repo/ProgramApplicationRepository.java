//package com.cognizant.greengov.repository.sustainability_repo;
//
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import com.cognizant.greengov.model.register_login.ParticipantProfile;
//import com.cognizant.greengov.model.sustainability_renewable_proj.EnergyProgram;
//import com.cognizant.greengov.model.sustainability_renewable_proj.ProgramApplication;
//
//public interface ProgramApplicationRepository
//        extends JpaRepository<ProgramApplication, Long> {
//
//    List<ProgramApplication> findByApplicant(ParticipantProfile applicant);
//
//    List<ProgramApplication> findByProgram(EnergyProgram program);
//
//    Optional<ProgramApplication> findByApplicantAndProgram(
//            ParticipantProfile applicant,
//            EnergyProgram program
//    );
//}

package com.cognizant.greengov.repository.sustainability_repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.greengov.model.register_login.ParticipantProfile;
import com.cognizant.greengov.model.sustainability_renewable_proj.EnergyProgram;
import com.cognizant.greengov.model.sustainability_renewable_proj.ProgramApplication;

public interface ProgramApplicationRepository
        extends JpaRepository<ProgramApplication, Long> {

    List<ProgramApplication> findByApplicant(ParticipantProfile applicant);

    List<ProgramApplication> findByProgram(EnergyProgram program);

    Optional<ProgramApplication> findByApplicantAndProgram(
            ParticipantProfile applicant,
            EnergyProgram program
    );
    
//    List<ProgramApplication> findByApplicantId(Long applicantId);
}
