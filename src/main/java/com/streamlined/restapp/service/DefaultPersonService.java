package com.streamlined.restapp.service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.streamlined.restapp.Utilities;
import com.streamlined.restapp.dao.PersonRepository;
import com.streamlined.restapp.model.PersonDto;
import com.streamlined.restapp.model.PersonListDto;
import com.streamlined.restapp.model.PersonMapper;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultPersonService implements PersonService {

	private final PersonRepository personRepository;
	private final PersonMapper personMapper;
	private final Validator validator;

	@Override
	public Stream<PersonDto> getAllPersons() {
		return Utilities.stream(personRepository.findAll()).map(personMapper::toDto);
	}

	@Override
	public Optional<PersonDto> getPersonById(Long id) {
		return personRepository.findById(id).map(personMapper::toDto);
	}

	@Override
	public PersonDto save(PersonDto person) {
		return save(person.id(), person);
	}

	@Override
	public PersonDto save(Long id, PersonDto person) {
		var entity = personMapper.toEntity(person);
		Utilities.checkIfValid(validator, entity, "person");
		return personMapper.toDto(personRepository.save(id, entity));
	}

	@Override
	public void removeById(Long id) {
		personRepository.deleteById(id);
	}

	@Override
	public PersonListDto getPersonList(int pageNumber, int pageSize, Map<String, Object> filterParameters) {
		var personList = personRepository.getPersonList(pageNumber, pageSize, filterParameters)
				.map(personMapper::toListDto).toList();
		return new PersonListDto(personList, personRepository.getTotalPages(pageSize, filterParameters));
	}

}
