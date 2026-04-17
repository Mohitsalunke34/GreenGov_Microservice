package com.example.demo.client;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dto.ApplicationDTO;
import com.example.demo.dto.ProgramDTO;

@FeignClient(name = "PROGRAM-MANAGEMENT-SERVICE")
public interface ProgramClient {

    @GetMapping("/api/applications/fetchById/{id}")
    ApplicationDTO getApplicationById(@PathVariable("id") Long id);

    @GetMapping("/api/programs/fetchById/{id}")
    ProgramDTO getProgramById(@PathVariable("id") Long id);

    @PutMapping("/api/programs/{id}/deduct-budget")
    void deductProgramBudget(
        @PathVariable("id") Long programId,
        @RequestParam("amount") BigDecimal amount
    );
}