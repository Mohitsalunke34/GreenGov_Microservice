package com.example.demo.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.demo.dto.ErrorResponseDTO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(ServiceUnavailableException.class)
	public ResponseEntity<ErrorResponseDTO> handleServiceUnreachable(ServiceUnavailableException ex,
			HttpServletRequest req) {
		log.warn("Service unreachable: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body((new ErrorResponseDTO(ex.getMessage(), LocalDateTime.now())));
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ErrorResponseDTO> handleConflict(IllegalStateException ex) {

		log.warn("Conflict: {}", ex.getMessage());

		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(new ErrorResponseDTO(ex.getMessage(), LocalDateTime.now()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponseDTO> handleBadRequest(IllegalArgumentException ex) {

		log.warn("Bad request: {}", ex.getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponseDTO(ex.getMessage(), LocalDateTime.now()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponseDTO> handleUnexpected(Exception ex) {

		log.error("Unexpected error", ex);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorResponseDTO("Unexpected internal error", LocalDateTime.now()));
	}

}
