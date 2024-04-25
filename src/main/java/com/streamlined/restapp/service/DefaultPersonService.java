package com.streamlined.restapp.service;

import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.streamlined.restapp.Utilities;
import com.streamlined.restapp.controller.UploadResponse;
import com.streamlined.restapp.dao.PersonRepository;
import com.streamlined.restapp.model.EssentialPersonDto;
import com.streamlined.restapp.model.Person;
import com.streamlined.restapp.model.PersonDto;
import com.streamlined.restapp.model.PersonListDto;
import com.streamlined.restapp.model.PersonMapper;
import com.streamlined.restapp.model.ReportDto;
import com.streamlined.restapp.parser.PersonParser;
import com.streamlined.restapp.reporter.Reporter;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultPersonService implements PersonService {

	private final PersonRepository personRepository;
	private final PersonMapper personMapper;
	private final Validator validator;
	private final Reporter reporter;
	private final PersonParser personParser;

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
		entity.setId(id);
		ServiceUtilities.checkIfValid(validator, entity, "person");
		return personMapper.toDto(personRepository.save(entity));
	}

	@Override
	public void removeById(Long id) {
		personRepository.deleteById(id);
	}

	@Override
	public void removeAllPersons() {
		personRepository.deleteAll();
	}

	@Override
	public PersonListDto getPersonList(int pageNumber, int pageSize, Person personProbe) {
		Example<Person> example = Example.of(personProbe, getPersonMatcher());
		var pageable = PageRequest.of(pageNumber, pageSize);
		Page<Person> personList = personRepository.findAll(example, pageable);
		return new PersonListDto(personList.map(personMapper::toListDto).toList(), personList.getTotalPages());
	}

	private ExampleMatcher getPersonMatcher() {
		return ExampleMatcher.matchingAny()
				.withIgnorePaths("countryOfOrigin.id", "countryOfOrigin.continent", "countryOfOrigin.population",
						"countryOfOrigin.square", "citizenship.id", "citizenship.continent", "citizenship.population",
						"citizenship.square")
				.withIgnoreNullValues();
	}

	@Override
	public Stream<EssentialPersonDto> getFilteredPersonStream(Person personProbe) {
		return getFilteredPersonEntityStream(personProbe).map(personMapper::toListDto);
	}

	private Stream<Person> getFilteredPersonEntityStream(Person personProbe) {
		Example<Person> example = Example.of(personProbe);
		return personRepository.findAll(example).stream();
	}

	@Override
	public ReportDto getFilteredPersonsAsFileResource(Person personProbe) {
		var personStream = getFilteredPersonEntityStream(personProbe);
		return new ReportDto(reporter.getFileResource(personStream), reporter.getFileName(), reporter.getMediaType());
	}

	@Override
	public UploadResponse uploadFile(MultipartFile multipartFile) {
		Path folder = null;
		try {
			folder = ServiceUtilities.copyToTemporaryFolder(multipartFile);
			var personStream = personParser.stream(folder);
			int succeededEntries = 0;
			int failedEntries = 0;
			for (var i = personStream.iterator(); i.hasNext();) {
				try {
					personRepository.save(i.next());
					succeededEntries++;
				} catch (Exception e) {
					failedEntries++;
				}
			}
			return new UploadResponse(succeededEntries, failedEntries);
		} finally {
			ServiceUtilities.cleanTemporaryFolder(folder);
		}
	}

}
