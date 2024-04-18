package com.streamlined.restapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncorrectDataException extends RuntimeException {

	public IncorrectDataException(String message) {
		super(message);
	}

	public IncorrectDataException(String message, Throwable cause) {
		super(message, cause);
	}

}
