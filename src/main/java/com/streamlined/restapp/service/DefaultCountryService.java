package com.streamlined.restapp.service;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.streamlined.restapp.Utilities;
import com.streamlined.restapp.dao.CountryRepository;
import com.streamlined.restapp.data.Country;
import com.streamlined.restapp.dto.CountryDto;
import com.streamlined.restapp.mapper.CountryMapper;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

/**
 * Service class for country entity
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultCountryService implements CountryService {

	private final CountryRepository countryRepository;
	private final CountryMapper countryMapper;
	private final Validator validator;

	@Override
	public Stream<CountryDto> getAllCountries() {
		return Utilities.stream(countryRepository.findAll()).map(countryMapper::toDto);
	}

	@Override
	public Optional<CountryDto> getCountryById(Long id) {
		return countryRepository.findById(id).map(countryMapper::toDto);
	}

	@Override
	@Transactional
	public CountryDto save(CountryDto country) {
		Country entity = countryMapper.toEntity(country);
		entity.setId(country.id());
		Utilities.checkIfValid(validator, entity, "country");
		return countryMapper.toDto(countryRepository.save(entity));
	}

	@Override
	@Transactional
	public CountryDto save(Long id, CountryDto country) {
		Country entity = countryMapper.toEntity(country);
		entity.setId(id);
		Utilities.checkIfValid(validator, entity, "country");
		return countryMapper.toDto(countryRepository.save(entity));
	}

	@Override
	@Transactional
	public void removeById(Long id) {
		countryRepository.deleteById(id);
	}

}
