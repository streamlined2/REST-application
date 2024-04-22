package com.streamlined.restapp.service;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.streamlined.restapp.Utilities;
import com.streamlined.restapp.controller.UploadResponse;
import com.streamlined.restapp.dao.PersonRepository;
import com.streamlined.restapp.model.EssentialPersonDto;
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
		ServiceUtilities.checkIfValid(validator, entity, "person");
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
		int totalPages = personRepository.getTotalPages(pageSize, filterParameters);
		return new PersonListDto(personList, totalPages);
	}

	@Override
	public Stream<EssentialPersonDto> getFilteredPersonStream(Map<String, Object> filterParameters) {
		return personRepository.getFilteredPersonStream(filterParameters).map(personMapper::toListDto);
	}

	@Override
	public ReportDto getFilteredPersonsAsFileResource(Map<String, Object> filterParameters) {
		var personStream = personRepository.getFilteredPersonStream(filterParameters);
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
