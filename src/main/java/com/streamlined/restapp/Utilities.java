package com.streamlined.restapp;

import java.net.URI;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.stream.Collectors;

import com.streamlined.restapp.exception.FileStorageException;
import com.streamlined.restapp.exception.IncorrectDataException;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class Utilities {

	public static final String PERSON_SOURCE_DIRECTORY_PREFIX = "person_";
	public static final String PERSON_SOURCE_FILE_NAME = "person.json";

	private static final int BUFFER_SIZE = 8 * 1024;
	
	public <T> Stream<T> stream(Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false);
	}

	public URI getResourceURI(HttpServletRequest servletRequest, Long id) {
		return UriComponentsBuilder.fromHttpUrl(servletRequest.getRequestURL().toString()).pathSegment("{id}")
				.build(id);
	}

	public void checkIfValid(Validator validator, Object entity, String entityType) {
		var violations = validator.validate(entity);
		if (!violations.isEmpty()) {
			var violationDescription = Utilities.getViolations(violations);
			log.error("Incorrect {} data: {}", entityType, violationDescription);
			throw new IncorrectDataException("Incorrect %s data: %s".formatted(entityType, violationDescription));
		}
	}

	private <T> String getViolations(Set<ConstraintViolation<T>> violations) {
		return violations.stream().map(Utilities::formatViolation).collect(Collectors.joining(",", "[", "]"));
	}

	private <T> String formatViolation(ConstraintViolation<T> violation) {
		return "Error %s: property '%s' has invalid value '%s'".formatted(violation.getMessage(),
				violation.getPropertyPath(), violation.getInvalidValue());
	}

	public Path copyToTemporaryFolder(MultipartFile multipartFile) {
		try (var inputStream = new BufferedInputStream(multipartFile.getInputStream(), BUFFER_SIZE)) {
			var folder = Files.createTempDirectory(PERSON_SOURCE_DIRECTORY_PREFIX);
			Path file = folder.resolve(PERSON_SOURCE_FILE_NAME);
			Files.copy(inputStream, file, StandardCopyOption.REPLACE_EXISTING);
			return folder;
		} catch (IOException e) {
			log.error("Cannot copy uploaded file");
			throw new FileStorageException("Cannot copy uploaded file", e);
		}
	}

	public void cleanTemporaryFolder(Path folder) {
		if (folder != null) {
			try (var fileStream = Files.walk(folder)) {
				for (var i = fileStream.iterator(); i.hasNext();) {
					var path = i.next();
					if (!Files.isDirectory(path)) {
						Files.delete(path);
					}
				}
				Files.delete(folder);
			} catch (IOException e) {
				log.error("Error while cleaning temporary folder with files");
				throw new FileStorageException("Error while cleaning temporary folder with files", e);
			}
		}
	}

}
