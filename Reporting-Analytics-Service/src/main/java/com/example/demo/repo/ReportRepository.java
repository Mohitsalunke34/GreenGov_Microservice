package com.example.demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.ReportScope;
import com.example.demo.model.Reports;

public interface ReportRepository extends JpaRepository<Reports, Long> {

    List<Reports> findByScope(ReportScope scope);
}