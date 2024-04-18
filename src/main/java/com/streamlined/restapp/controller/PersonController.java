package com.streamlined.restapp.controller;

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

}
