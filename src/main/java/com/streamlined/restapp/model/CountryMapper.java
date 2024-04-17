package com.streamlined.restapp.model;

import org.springframework.stereotype.Component;

@Component
public class CountryMapper {

	public CountryDto toDto(Country country) {
		return CountryDto.builder().id(country.getId()).name(country.getName()).continent(country.getContinent())
				.capital(country.getCapital()).population(country.getPopulation()).square(country.getSquare()).build();
	}

	public Country toEntity(CountryDto country) {
		return Country.builder().id(country.id()).name(country.name()).continent(country.continent())
				.capital(country.capital()).population(country.population()).square(country.square()).build();
	}

}
