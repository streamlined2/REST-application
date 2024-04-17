package com.streamlined.restapp;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Utilities {

	public <T> Stream<T> stream(Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false);
	}

}
