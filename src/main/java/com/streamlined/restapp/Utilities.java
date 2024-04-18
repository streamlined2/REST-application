package com.streamlined.restapp;

import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.web.util.UriComponentsBuilder;

import com.streamlined.restapp.exception.IncorrectDataException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Utilities {

	public <T> Stream<T> stream(Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false);
	}

	public void checkIfValid(Validator validator, Object entity, String entityType) {
		var violations = validator.validate(entity);
		if (!violations.isEmpty()) {
			throw new IncorrectDataException(
					"Incorrect %s data: %s".formatted(entityType, Utilities.getViolations(violations)));
		}
	}

	private <T> String getViolations(Set<ConstraintViolation<T>> violations) {
		return violations.stream().map(Utilities::formatViolation).collect(Collectors.joining(",", "[", "]"));
	}

	private <T> String formatViolation(ConstraintViolation<T> violation) {
		return "Error %s: property '%s' has invalid value '%s'".formatted(violation.getMessage(),
				violation.getPropertyPath(), violation.getInvalidValue());
	}

	public URI getResourceURI(HttpServletRequest servletRequest, Long id) {
		return UriComponentsBuilder.fromHttpUrl(servletRequest.getRequestURL().toString()).pathSegment("{id}")
				.build(id);
	}

}
