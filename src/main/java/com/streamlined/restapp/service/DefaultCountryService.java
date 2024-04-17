package com.streamlined.restapp.service;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import com.streamlined.restapp.dao.CountryRepository;
import com.streamlined.restapp.model.CountryDto;
import com.streamlined.restapp.model.CountryMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultCountryService implements CountryService {

	private final CountryRepository countryRepository;
	private final CountryMapper countryMapper;

	@Override
	public Stream<CountryDto> getAllCountries() {
		return Streamable.of(countryRepository.findAll()).map(countryMapper::toDto).stream();
	}

	@Override
	public Optional<CountryDto> getCountryById(Long id) {
		return countryRepository.findById(id).map(countryMapper::toDto);
	}

	@Override
	public CountryDto save(CountryDto country) {
		return save(country.id(), country);
	}

	@Override
	public CountryDto save(Long id, CountryDto country) {
		return countryMapper.toDto(countryRepository.save(id, countryMapper.toEntity(country)));
	}

	@Override
	public void removeById(Long id) {
		countryRepository.deleteById(id);
	}

}
