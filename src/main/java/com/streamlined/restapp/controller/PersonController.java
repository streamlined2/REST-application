package com.streamlined.restapp.controller;

import java.util.Map;
import java.util.stream.Stream;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
		return ResponseEntity.created(ControllerUtilities.getResourceURI(servletRequest, person.id())).build();
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
		return personService.getPersonList(ControllerUtilities.getPageNumber(parameters),
				ControllerUtilities.getPageSize(parameters), ControllerUtilities.getFilterParameters(parameters));
	}

	@PostMapping(value = "/_report", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<FileSystemResource> getPersonListAsFile(HttpEntity<Map<String, Object>> entity) {
		var parameters = entity.getBody();
		var outputFile = personService
				.getFilteredPersonsAsFileResource(ControllerUtilities.getFilterParameters(parameters));
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"%s\"".formatted(outputFile.fileName()));
		return ResponseEntity.ok().contentType(outputFile.mediaType()).headers(responseHeaders)
				.body(outputFile.fileResource());
	}

}
