package com.streamlined.restapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.streamlined.restapp.data.Color;
import com.streamlined.restapp.data.Country;
import com.streamlined.restapp.data.Person;
import com.streamlined.restapp.data.Sex;

public record PersonListRequest(Integer page, Integer size, String name, LocalDate birthday, Sex sex, Color eyeColor,
		Color hairColor, BigDecimal weight, BigDecimal height, Country countryOfOrigin, Country citizenship,
		String favoriteMeals) {

	private static final int DEFAULT_PAGE_NUMBER = 0;
	private static final int DEFAULT_PAGE_SIZE = 10;

	public Person getPersonProbe() {
		return Person.builder().name(name).birthday(birthday).sex(sex).eyeColor(eyeColor).hairColor(hairColor)
				.weight(weight).height(height).countryOfOrigin(countryOfOrigin).citizenship(citizenship)
				.favoriteMeals(favoriteMeals).build();
	}

	public int getPageNumber() {
		return page == null ? DEFAULT_PAGE_NUMBER : page.intValue();
	}

	public int getPageSize() {
		return size == null ? DEFAULT_PAGE_SIZE : size.intValue();
	}
}
