package com.streamlined.restapp.service;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.web.multipart.MultipartFile;

import com.streamlined.restapp.data.Person;
import com.streamlined.restapp.dto.PersonDto;
import com.streamlined.restapp.dto.PersonListDto;
import com.streamlined.restapp.dto.ReportDto;
import com.streamlined.restapp.dto.UploadResponse;

public interface PersonService {

	Stream<PersonDto> getAllPersons();

	Optional<PersonDto> getPersonById(Long id);

	PersonDto save(PersonDto person);

	PersonDto save(Long id, PersonDto person);

	void removeById(Long id);

	void removeAllPersons();

	ReportDto getFilteredPersonsAsFileResource(Person personPerson);

	PersonListDto getPersonList(int page, int size, Person probe);

	UploadResponse uploadFile(MultipartFile multipartFile);

}
