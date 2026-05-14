package com.example.demo.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramApplicationResponseDto {

	private Long applicationId;

	private Long applicantId;

	private Long programId;

	private LocalDate submittedDate;

	private String programTitle;

	private String status;
}