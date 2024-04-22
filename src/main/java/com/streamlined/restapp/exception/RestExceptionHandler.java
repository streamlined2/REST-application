package com.streamlined.restapp.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

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

	@ExceptionHandler(InvalidFormatException.class)
	public ResponseEntity<String> handleInvalidJsonFormatException(InvalidFormatException exception) {
		return ResponseEntity.badRequest().body(exception.getMessage());
	}

	@ExceptionHandler(IntrospectionException.class)
	public ResponseEntity<String> handleIntrospectionException(IntrospectionException exception) {
		return ResponseEntity.badRequest().body(exception.getMessage());
	}

	@ExceptionHandler(ReportException.class)
	public ResponseEntity<String> handleReportException(ReportException exception) {
		return ResponseEntity.badRequest().body(exception.getMessage());
	}

	@ExceptionHandler(ParseException.class)
	public ResponseEntity<String> handleParseException(ParseException exception) {
		return ResponseEntity.badRequest().body(exception.getMessage());
	}

	@ExceptionHandler(FileStorageException.class)
	public ResponseEntity<String> handleFileStorageException(FileStorageException exception) {
		return ResponseEntity.badRequest().body(exception.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleAnyOtherExceptionByDefault(Exception exception) {
		return ResponseEntity.badRequest().body(exception.getMessage());
	}

}
