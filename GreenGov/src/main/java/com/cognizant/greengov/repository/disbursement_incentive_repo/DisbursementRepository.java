package com.cognizant.greengov.repository.disbursement_incentive_repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.greengov.model.incentive_subsidy.Disbursement;
import com.cognizant.greengov.model.incentive_subsidy.Incentive;
import com.cognizant.greengov.model.register_login.UserAccount;

public interface DisbursementRepository extends JpaRepository<Disbursement, Long> {

	List<Disbursement> findByIncentive(Incentive incentive);

	List<Disbursement> findByOfficer(UserAccount officer);
}