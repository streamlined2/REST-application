package com.streamlined.restapp;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.streamlined.restapp.exception.IncorrectDataException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Utilities {

	public <T> Stream<T> stream(Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false);
	}

	public void checkIfValid(Validator validator, Object entity) {
		var violations = validator.validate(entity);
		if (!violations.isEmpty()) {
			throw new IncorrectDataException(
					"Incorrect country data: %s".formatted(Utilities.getViolations(violations)));
		}
	}

	private <T> String getViolations(Set<ConstraintViolation<T>> violations) {
		return violations.stream().map(Utilities::formatViolation).collect(Collectors.joining(",", "[", "]"));
	}

	private <T> String formatViolation(ConstraintViolation<T> violation) {
		return "Error %s: property '%s' has invalid value '%s'".formatted(violation.getMessage(),
				violation.getPropertyPath(), violation.getInvalidValue());
	}

}
