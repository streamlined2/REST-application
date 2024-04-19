package com.streamlined.restapp.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.streamlined.restapp.Utilities;
import com.streamlined.restapp.exception.EntityNotFoundException;
import com.streamlined.restapp.model.PersonDto;
import com.streamlined.restapp.model.PersonListDto;
import com.streamlined.restapp.service.PersonService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/person")
public class PersonController {

	private static final String PAGE_PARAMETER = "page";
	private static final int DEFAULT_PAGE_NUMBER = 1;
	private static final String SIZE_PARAMETER = "size";
	private static final int DEFAULT_SIZE_VALUE = 5;

	private final PersonService personService;

	@GetMapping
	public Stream<PersonDto> getAllPersons() {
		return personService.getAllPersons();
	}

	@GetMapping("/{id}")
	public PersonDto getPersonById(@PathVariable Long id) {
		return personService.getPersonById(id)
				.orElseThrow(() -> new EntityNotFoundException("Person with id %d not found".formatted(id)));
	}

	@PostMapping
	public ResponseEntity<Void> addPerson(@RequestBody PersonDto person, HttpServletRequest servletRequest) {
		personService.save(person);
		return ResponseEntity.created(Utilities.getResourceURI(servletRequest, person.id())).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> updatePerson(@PathVariable Long id, @RequestBody PersonDto person) {
		personService.save(person);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
		personService.removeById(id);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/_list")
	public PersonListDto getPersonList(@RequestBody Map<String, Object> parameters) {
		return personService.getPersonList(getPageNumber(parameters), getPageSize(parameters),
				getFilterParameters(parameters));
	}

	private int getPageNumber(Map<String, Object> parameters) {
		var pageNumber = parameters.get(PAGE_PARAMETER);
		if (pageNumber == null) {
			return DEFAULT_PAGE_NUMBER;
		}
		try {
			if (pageNumber instanceof Integer i) {
				return i.intValue();
			} else if (pageNumber instanceof String s) {
				return Integer.parseInt(s);
			}
			return DEFAULT_PAGE_NUMBER;
		} catch (NumberFormatException e) {
			return DEFAULT_PAGE_NUMBER;
		}
	}

	private int getPageSize(Map<String, Object> parameters) {
		var pageSize = parameters.get(SIZE_PARAMETER);
		if (pageSize == null) {
			return DEFAULT_SIZE_VALUE;
		}
		try {
			if (pageSize instanceof Integer i) {
				return i.intValue();
			} else if (pageSize instanceof String s) {
				return Integer.parseInt(s);
			}
			return DEFAULT_SIZE_VALUE;
		} catch (NumberFormatException e) {
			return DEFAULT_SIZE_VALUE;
		}
	}

	private Map<String, Object> getFilterParameters(Map<String, Object> parameters) {
		return parameters.entrySet().stream().filter(this::isNotReservedParameter)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	private boolean isNotReservedParameter(Map.Entry<String, Object> entry) {
		return isNotReservedParameter(entry.getKey(), PAGE_PARAMETER, SIZE_PARAMETER);
	}

	private boolean isNotReservedParameter(Object obj, Object... values) {
		return !Arrays.asList(values).contains(obj);
	}

}
