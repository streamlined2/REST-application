package com.streamlined.restapp.service;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.streamlined.restapp.Utilities;
import com.streamlined.restapp.dao.PersonRepository;
import com.streamlined.restapp.model.PersonDto;
import com.streamlined.restapp.model.PersonMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultPersonService implements PersonService {

	private final PersonRepository personRepository;
	private final PersonMapper personMapper;

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
		return personMapper.toDto(personRepository.save(id, personMapper.toEntity(person)));
	}

	@Override
	public void removeById(Long id) {
		personRepository.deleteById(id);
	}

}
