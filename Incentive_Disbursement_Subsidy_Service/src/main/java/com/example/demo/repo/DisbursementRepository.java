package com.example.demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Disbursement;
import com.example.demo.model.Incentive;

@Repository
public interface DisbursementRepository extends JpaRepository<Disbursement, Long> {

    /**
     * Fetch all disbursements for a given incentive.
     */
    List<Disbursement> findByIncentive(Incentive incentive);

    /**
     * Fetch all disbursements processed by an officer.
     * Officer belongs to another microservice → use ID.
     */
    List<Disbursement> findByOfficerUserId(Long officerUserId);
    

	@Query("SELECT COALESCE(SUM(d.amount), 0) FROM Disbursement d")
	Double getTotalDisbursedAmount();

}