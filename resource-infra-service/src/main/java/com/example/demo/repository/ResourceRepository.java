package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Resources;

@Repository
public interface ResourceRepository extends JpaRepository<Resources, Long> {

	List<Resources> findByProjectId(long projectId);
}
