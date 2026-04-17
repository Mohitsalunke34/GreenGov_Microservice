package com.cognizant.greengov.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cognizant.greengov.profile.model.register_login.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
}