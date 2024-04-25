package com.streamlined.restapp.service;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.web.multipart.MultipartFile;

import com.streamlined.restapp.controller.UploadResponse;
import com.streamlined.restapp.model.EssentialPersonDto;
import com.streamlined.restapp.model.Person;
import com.streamlined.restapp.model.PersonDto;
import com.streamlined.restapp.model.PersonListDto;
import com.streamlined.restapp.model.ReportDto;

public interface PersonService {

	Stream<PersonDto> getAllPersons();

	Optional<PersonDto> getPersonById(Long id);

	PersonDto save(PersonDto person);

	PersonDto save(Long id, PersonDto person);

	void removeById(Long id);

	void removeAllPersons();

	Stream<EssentialPersonDto> getFilteredPersonStream(Person probe);

	ReportDto getFilteredPersonsAsFileResource(Person personPerson);

	PersonListDto getPersonList(int page, int size, Person probe);

	UploadResponse uploadFile(MultipartFile multipartFile);

}
