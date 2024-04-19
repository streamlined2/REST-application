package com.streamlined.restapp.service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.streamlined.restapp.model.PersonDto;
import com.streamlined.restapp.model.PersonListDto;

public interface PersonService {

	Stream<PersonDto> getAllPersons();

	Optional<PersonDto> getPersonById(Long id);

	PersonDto save(PersonDto person);

	PersonDto save(Long id, PersonDto person);

	void removeById(Long id);

	PersonListDto getPersonList(int page, int size, Map<String, Object> filterParameters);

}
