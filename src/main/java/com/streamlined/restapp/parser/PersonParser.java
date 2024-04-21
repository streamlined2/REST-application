package com.streamlined.restapp.parser;

import java.nio.file.Path;
import java.util.stream.Stream;

import com.streamlined.restapp.model.Person;

public interface PersonParser {

	public Stream<Person> stream(Path path);

}
