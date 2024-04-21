package com.streamlined.restapp.service;

import java.util.Set;
import java.util.stream.Collectors;

import com.streamlined.restapp.exception.IncorrectDataException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServiceUtilities {

	public void checkIfValid(Validator validator, Object entity, String entityType) {
		var violations = validator.validate(entity);
		if (!violations.isEmpty()) {
			throw new IncorrectDataException(
					"Incorrect %s data: %s".formatted(entityType, ServiceUtilities.getViolations(violations)));
		}
	}

	private <T> String getViolations(Set<ConstraintViolation<T>> violations) {
		return violations.stream().map(ServiceUtilities::formatViolation).collect(Collectors.joining(",", "[", "]"));
	}

	private <T> String formatViolation(ConstraintViolation<T> violation) {
		return "Error %s: property '%s' has invalid value '%s'".formatted(violation.getMessage(),
				violation.getPropertyPath(), violation.getInvalidValue());
	}

}
