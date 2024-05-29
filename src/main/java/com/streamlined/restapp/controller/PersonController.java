package com.streamlined.restapp.controller;

import java.util.stream.Stream;

import org.springframework.core.io.FileSystemResource;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.streamlined.restapp.Utilities;
import com.streamlined.restapp.dto.PersonDto;
import com.streamlined.restapp.dto.PersonListDto;
import com.streamlined.restapp.dto.PersonListRequest;
import com.streamlined.restapp.dto.UploadResponse;
import com.streamlined.restapp.exception.EntityNotFoundException;
import com.streamlined.restapp.service.PersonService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * Controller class for person entity
 */

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
		var savedPerson = personService.save(person);
		return ResponseEntity.created(Utilities.getResourceURI(servletRequest, savedPerson.id())).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> updatePerson(@PathVariable Long id, @RequestBody PersonDto person) {
		personService.save(id, person);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
		personService.removeById(id);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteAll() {
		personService.removeAllPersons();
		return ResponseEntity.ok().build();
	}

	@PostMapping("/_list")
	public PersonListDto getPersonList(@RequestBody PersonListRequest personListRequest) {
		return personService.getPersonList(personListRequest.getPageNumber(), personListRequest.getPageSize(),
				personListRequest.getPersonProbe());
	}

	@PostMapping(value = "/_report", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<FileSystemResource> getPersonListAsFile(@RequestBody PersonListRequest personListRequest) {
		var outputFile = personService.getFilteredPersonsAsFileResource(personListRequest.getPersonProbe());
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"%s\"".formatted(outputFile.fileName()));
		return ResponseEntity.ok().contentType(outputFile.mediaType()).headers(responseHeaders)
				.body(outputFile.fileResource());
	}

	@PostMapping(value = "/upload")
	public ResponseEntity<UploadResponse> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
		var uploadStatus = personService.uploadFile(multipartFile);
		return ResponseEntity.ok().body(uploadStatus);
	}

}
