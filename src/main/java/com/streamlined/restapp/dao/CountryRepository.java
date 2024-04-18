package com.streamlined.restapp.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.streamlined.restapp.exception.EntityNotFoundException;
import com.streamlined.restapp.exception.IncorrectDataException;
import com.streamlined.restapp.model.Continent;
import com.streamlined.restapp.model.Country;

@Repository
public class CountryRepository {

	private final Map<Long, Country> countries = new HashMap<>(Map.ofEntries(
			Map.entry(1L,
					Country.builder().id(1L).name("Great Britain").continent(Continent.EUROPE).capital("London")
							.population(60000000).square(1745813.01).build()),
			Map.entry(2L,
					Country.builder().id(2L).name("USA").continent(Continent.NORTH_AMERICA).capital("Washington")
							.population(250000000).square(6361952.20).build()),
			Map.entry(3L,
					Country.builder().id(3L).name("Germany").continent(Continent.EUROPE).capital("Berlin")
							.population(75000000).square(1279813.01).build()),
			Map.entry(4L,
					Country.builder().id(4L).name("Canada").continent(Continent.NORTH_AMERICA).capital("Ottawa")
							.population(125000000).square(6345813.01).build()),
			Map.entry(5L, Country.builder().id(5L).name("Netherlands").continent(Continent.EUROPE).capital("Amsterdam")
					.population(35000000).square(425853.01).build())));

	public Iterable<Country> findAll() {
		return countries.values();
	}

	public Optional<Country> findById(Long id) {
		return Optional.ofNullable(countries.get(id));
	}

	public Country save(Long id, Country country) {
		if (isDuplicateCountryName(id, country)) {
			throw new IncorrectDataException("Country list already contains name: %s".formatted(country.getName()));
		}
		country.setId(id);
		countries.put(id, country);
		return country;
	}

	private boolean isDuplicateCountryName(Long id, Country country) {
		return !countries.values().stream()
				.filter(c -> !Objects.equals(c.getId(), id) && Objects.equals(c.getName(), country.getName())).findAny()
				.isEmpty();
	}

	public void deleteById(Long id) {
		if (findById(id).isEmpty()) {
			throw new EntityNotFoundException("Country with id %d not found".formatted(id));
		}
		countries.remove(id);
	}

}
