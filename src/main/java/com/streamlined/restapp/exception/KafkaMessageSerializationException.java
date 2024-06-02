package com.streamlined.restapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.experimental.StandardException;

@StandardException
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class KafkaMessageSerializationException extends RuntimeException {
}
