package com.streamlined.restapp.service;

import java.util.Optional;
import java.util.stream.Stream;

import com.streamlined.restapp.model.PersonDto;

public interface PersonService {

	Stream<PersonDto> getAllPersons();

	Optional<PersonDto> getPersonById(Long id);

	PersonDto save(PersonDto person);

	PersonDto save(Long id, PersonDto person);
	
	void removeById(Long id);

}
