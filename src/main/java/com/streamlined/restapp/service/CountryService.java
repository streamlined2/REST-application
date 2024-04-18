package com.streamlined.restapp.service;

import java.util.Optional;
import java.util.stream.Stream;

import com.streamlined.restapp.model.CountryDto;

public interface CountryService {

	Stream<CountryDto> getAllCountries();

	Optional<CountryDto> getCountryById(Long id);

	CountryDto save(CountryDto country);

	CountryDto save(Long id, CountryDto country);

	void removeById(Long id);

}