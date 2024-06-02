package com.streamlined.restapp.service.reporter;

import java.util.stream.Stream;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;

import com.streamlined.restapp.data.Person;

public interface Reporter {

	FileSystemResource getFileResource(Stream<Person> personStream);
	
	MediaType getMediaType();
	
	String getFileName();

}