package com.streamlined.restapp.service.country;

import java.util.Optional;
import java.util.stream.Stream;

import com.streamlined.restapp.dto.CountryDto;

public interface CountryService {

	Stream<CountryDto> getAllCountries();

	Optional<CountryDto> getCountryById(Long id);

	CountryDto save(CountryDto country);

	CountryDto save(Long id, CountryDto country);

	void removeById(Long id);

}
