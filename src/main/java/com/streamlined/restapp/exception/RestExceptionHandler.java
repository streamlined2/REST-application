package com.streamlined.restapp.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException exception) {
		return ResponseEntity.badRequest().body(exception.getMessage());
	}

	@ExceptionHandler(IncorrectDataException.class)
	public ResponseEntity<String> handleIncorrectDataException(IncorrectDataException exception) {
		return ResponseEntity.badRequest().body(exception.getMessage());
	}

}
