package com.streamlined.restapp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.streamlined.restapp.RestApplication;
import com.streamlined.restapp.Utilities;
import com.streamlined.restapp.dao.CountryRepository;
import com.streamlined.restapp.data.Continent;
import com.streamlined.restapp.data.Country;
import com.streamlined.restapp.dto.CountryDto;
import com.streamlined.restapp.mapper.CountryMapper;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = RestApplication.class)
@AutoConfigureMockMvc
class CountryControllerTest {

	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper mapper;
	@Autowired
	private CountryRepository countryRepository;
	@Autowired
	private CountryMapper countryMapper;

	@Test
	void testGetAllCountriesSuccess() throws Exception {
		final List<Country> countryList = List.of(
				Country.builder().name("USA").continent(Continent.NORTH_AMERICA).capital("Washington")
						.population(334914895).square(8080470).build(),
				Country.builder().name("United Kingdom").continent(Continent.EUROPE).capital("London")
						.population(67596281).square(244376).build(),
				Country.builder().name("Canada").continent(Continent.NORTH_AMERICA).capital("Ottawa")
						.population(40769890).square(9984670).build(),
				Country.builder().name("Netherlands").continent(Continent.EUROPE).capital("Amsterdam")
						.population(18072300).square(42531).build(),
				Country.builder().name("Germany").continent(Continent.EUROPE).capital("Berlin").population(84607016)
						.square(357600).build(),
				Country.builder().name("France").continent(Continent.EUROPE).capital("Paris").population(68373433)
						.square(643801).build(),
				Country.builder().name("Spain").continent(Continent.EUROPE).capital("Madrid").population(48592909)
						.square(505994).build(),
				Country.builder().name("Portugal").continent(Continent.EUROPE).capital("Lisbon").population(10467366)
						.square(92230).build(),
				Country.builder().name("Belgium").continent(Continent.EUROPE).capital("Brussels").population(11697557)
						.square(30689).build(),
				Country.builder().name("Poland").continent(Continent.EUROPE).capital("Warsaw").population(38036118)
						.square(312696).build(),
				Country.builder().name("Romania").continent(Continent.EUROPE).capital("Bucharest").population(19051562)
						.square(238398).build(),
				Country.builder().name("Bulgaria").continent(Continent.EUROPE).capital("Sofia").population(6447710)
						.square(110994).build(),
				Country.builder().name("Czech Republic").continent(Continent.EUROPE).capital("Prague")
						.population(10900555).square(78871).build(),
				Country.builder().name("Slovakia").continent(Continent.EUROPE).capital("Bratislava").population(5460185)
						.square(49035).build(),
				Country.builder().name("Hungary").continent(Continent.EUROPE).capital("Budapest").population(9597085)
						.square(93030).build(),
				Country.builder().name("Greece").continent(Continent.EUROPE).capital("Athens").population(10413982)
						.square(131957).build(),
				Country.builder().name("Italy").continent(Continent.EUROPE).capital("Rome").population(58853482)
						.square(301340).build(),
				Country.builder().name("Ukraine").continent(Continent.EUROPE).capital("Kyiv").population(33365000)
						.square(603628).build());

		countryRepository.deleteAll();
		Iterable<Country> savedCountries = countryRepository.saveAll(countryList);

		try {
			MvcResult mvcResult = mvc.perform(get("/api/country"))
					.andExpectAll(status().isOk(), content().contentType(MediaType.APPLICATION_JSON)).andReturn();

			String content = mvcResult.getResponse().getContentAsString();
			CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class,
					CountryDto.class);
			Object value = mapper.readValue(content, collectionType);

			List<CountryDto> countries = countryList.stream().map(countryMapper::toDto).toList();

			assertThat(value).isNotNull().asList().hasSize(countries.size())
					.containsExactlyInAnyOrderElementsOf(countries);

		} finally {
			countryRepository.deleteAllById(Utilities.stream(savedCountries).map(Country::getId).toList());
		}
	}

	@Test
	void testGetCountryByIdSuccess() throws Exception {
		final Country country = createNewCountry("SomeValidCountryName");
		try {
			MvcResult mvcResult = mvc.perform(get("/api/country/{id}", country.getId()))
					.andExpectAll(status().isOk(), content().contentType(MediaType.APPLICATION_JSON)).andReturn();
			String content = mvcResult.getResponse().getContentAsString();
			Object value = mapper.readValue(content, CountryDto.class);

			assertThat(value).isNotNull().usingRecursiveComparison().isEqualTo(countryMapper.toDto(country));
		} finally {
			countryRepository.delete(country);
		}
	}

	@Test
	void testGetCountryByIdFailNotFound() throws Exception {
		Optional<Long> nonExistentId = getNonExistentId();
		if (nonExistentId.isEmpty()) {
			fail("All country id occupied");
		}

		mvc.perform(get("/api/country/{id}", nonExistentId.get())).andExpectAll(status().isNotFound());
	}

	private Optional<Long> getNonExistentId() {
		final Optional<Long> maxId = Utilities.stream(countryRepository.findAll()).map(Country::getId)
				.max(Comparator.naturalOrder());
		if (maxId.isEmpty()) {
			return Optional.of(1L);
		} else if (maxId.get() < Long.MAX_VALUE) {
			return Optional.of(maxId.get() + 1);
		}
		final Optional<Long> minId = Utilities.stream(countryRepository.findAll()).map(Country::getId)
				.min(Comparator.naturalOrder());
		if (minId.isPresent() && minId.get() > 1) {
			return Optional.of(minId.get() - 1);
		}
		return Optional.empty();
	}

	@Test
	void testAddCountrySuccess() throws Exception {
		final Country newCountry = Country.builder().name("SomeFancyCountryName").continent(Continent.ASIA)
				.capital("Capital").population(1428627663).square(3287263).build();
		countryRepository.deleteByName(newCountry.getName());
		String requestBody = mapper.writeValueAsString(newCountry);

		MvcResult mvcResult = mvc
				.perform(post("/api/country").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isCreated()).andReturn();

		String locationHeader = mvcResult.getResponse().getHeader("Location");
		assertThat(locationHeader).matchesSatisfying(Pattern.compile("http://localhost/api/country/(\\d+)"),
				matcher -> {
					Long entityId = Long.valueOf(matcher.group(1));
					try {
						newCountry.setId(entityId);
						assertThat(countryRepository.findById(entityId)).contains(newCountry);
					} finally {
						countryRepository.deleteById(entityId);
					}
				});
	}

	@ParameterizedTest
	@ValueSource(strings = { "USA", "   ", "US" })
	void testAddCountryFailInvalidCountryName(String countryName) throws Exception {
		Country existingCountry = createNewCountry("USA");
		try {
			final Country newCountry = Country.builder().name(countryName).continent(Continent.ASIA)
					.capital("New Delhi").population(1428627663).square(3287263).build();
			String requestBody = mapper.writeValueAsString(newCountry);

			mvc.perform(post("/api/country").contentType(MediaType.APPLICATION_JSON).content(requestBody))
					.andExpectAll(status().isBadRequest());

		} finally {
			countryRepository.delete(existingCountry);
		}
	}

	@Test
	void testAddCountryFailNullContinent() throws Exception {
		final Country newCountry = Country.builder().name("India").continent(null).capital("New Delhi")
				.population(1428627663).square(3287263).build();
		String requestBody = mapper.writeValueAsString(newCountry);

		mvc.perform(post("/api/country").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isBadRequest());
	}

	@ParameterizedTest
	@ValueSource(strings = { "   ", "AB" })
	void testAddCountryFailInvalidCapital(String capital) throws Exception {
		final Country newCountry = Country.builder().name("India").continent(Continent.ASIA).capital(capital)
				.population(1428627663).square(3287263).build();
		String requestBody = mapper.writeValueAsString(newCountry);

		mvc.perform(post("/api/country").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isBadRequest());
	}

	@ParameterizedTest
	@ValueSource(ints = { -100, 0 })
	void testAddCountryFailNegativeZeroPopulation(int population) throws Exception {
		final Country newCountry = Country.builder().name("India").continent(Continent.ASIA).capital("New Delhi")
				.population(population).square(3287263).build();
		String requestBody = mapper.writeValueAsString(newCountry);

		mvc.perform(post("/api/country").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isBadRequest());
	}

	@ParameterizedTest
	@ValueSource(doubles = { -10_000D, 0D })
	void testAddCountryFailNegativeZeroArea(double area) throws Exception {
		final Country newCountry = Country.builder().name("India").continent(Continent.ASIA).capital("New Delhi")
				.population(1428627663).square(area).build();
		String requestBody = mapper.writeValueAsString(newCountry);

		mvc.perform(post("/api/country").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isBadRequest());
	}

	@Test
	void testUpdateCountrySuccess() throws Exception {
		Country newCountry = createNewCountry("SomeFancyName");
		try {
			CountryDto updatedCountry = CountryDto.builder().id(newCountry.getId()).name(newCountry.getName())
					.continent(Continent.ASIA).capital("Capital").population(1428627663).square(3287263).build();
			String requestBody = mapper.writeValueAsString(updatedCountry);

			mvc.perform(put("/api/country/{id}", newCountry.getId()).contentType(MediaType.APPLICATION_JSON)
					.content(requestBody)).andExpectAll(status().isOk());

			Optional<CountryDto> updatedEntity = countryRepository.findById(newCountry.getId())
					.map(countryMapper::toDto);
			assertThat(updatedEntity).contains(updatedCountry);
		} finally {
			countryRepository.deleteById(newCountry.getId());
		}
	}

	private Country createNewCountry(String name) {
		Country newCountry = Country.builder().name(name).continent(Continent.NORTH_AMERICA)
				.capital("Name of the Capital").population(1).square(1).build();
		Optional<Country> entity = countryRepository.findByName(newCountry.getName());
		if (entity.isPresent()) {
			countryRepository.delete(entity.get());
		}
		newCountry = countryRepository.save(newCountry);
		return newCountry;
	}

	@Test
	void testUpdateCountryFailNonUniqueCountryName() throws Exception {
		Country country = createNewCountry("SomeFancyName");
		Country otherCountry = createNewCountry("YetAnotherFancyName");
		try {
			otherCountry.setName(country.getName());
			String requestBody = mapper.writeValueAsString(otherCountry);

			mvc.perform(put("/api/country/{id}", otherCountry.getId()).contentType(MediaType.APPLICATION_JSON)
					.content(requestBody)).andExpectAll(status().isBadRequest());
		} finally {
			countryRepository.delete(country);
			countryRepository.delete(otherCountry);
		}
	}

	@ParameterizedTest
	@ValueSource(strings = { "   ", "US" })
	void testUpdateCountryFailInvalidCountryName(String countryName) throws Exception {
		Country country = createNewCountry("SomeFancyName");
		try {
			country.setName(countryName);
			String requestBody = mapper.writeValueAsString(country);

			mvc.perform(put("/api/country/{id}", country.getId()).contentType(MediaType.APPLICATION_JSON)
					.content(requestBody)).andExpectAll(status().isBadRequest());

		} finally {
			countryRepository.delete(country);
		}
	}

	@Test
	void testUpdateCountryFailNullContinent() throws Exception {
		Country country = createNewCountry("SomeFancyName");
		try {
			country.setContinent(null);
			String requestBody = mapper.writeValueAsString(country);

			mvc.perform(put("/api/country/{id}", country.getId()).contentType(MediaType.APPLICATION_JSON)
					.content(requestBody)).andExpectAll(status().isBadRequest());

		} finally {
			countryRepository.delete(country);
		}
	}

	@ParameterizedTest
	@ValueSource(strings = { "   ", "AB" })
	void testUpdateCountryFailInvalidCapital(String capital) throws Exception {
		Country country = createNewCountry("SomeFancyName");
		try {
			country.setCapital(capital);
			String requestBody = mapper.writeValueAsString(country);

			mvc.perform(put("/api/country/{id}", country.getId()).contentType(MediaType.APPLICATION_JSON)
					.content(requestBody)).andExpectAll(status().isBadRequest());

		} finally {
			countryRepository.delete(country);
		}
	}

	@ParameterizedTest
	@ValueSource(ints = { -100, 0 })
	void testUpdateCountryFailNegativeZeroPopulation(int population) throws Exception {
		Country country = createNewCountry("SomeFancyName");
		try {
			country.setPopulation(population);
			String requestBody = mapper.writeValueAsString(country);

			mvc.perform(put("/api/country/{id}", country.getId()).contentType(MediaType.APPLICATION_JSON)
					.content(requestBody)).andExpectAll(status().isBadRequest());

		} finally {
			countryRepository.delete(country);
		}
	}

	@ParameterizedTest
	@ValueSource(doubles = { -10_000D, 0D })
	void testUpdateCountryFailNegativeZeroArea(double area) throws Exception {
		Country country = createNewCountry("SomeFancyName");
		try {
			country.setSquare(area);
			String requestBody = mapper.writeValueAsString(country);

			mvc.perform(put("/api/country/{id}", country.getId()).contentType(MediaType.APPLICATION_JSON)
					.content(requestBody)).andExpectAll(status().isBadRequest());

		} finally {
			countryRepository.delete(country);
		}
	}

	@Test
	void testDeleteCountrySuccess() throws Exception {
		Country country = createNewCountry("SomeFancyName");
		try {
			mvc.perform(delete("/api/country/{id}", country.getId())).andExpect(status().isOk());

			Optional<Country> deletedEntity = countryRepository.findById(country.getId());
			assertThat(deletedEntity).isEmpty();
		} finally {
			countryRepository.delete(country);
		}
	}

	@Test
	void testDeleteCountryFailNotFound() throws Exception {
		final Optional<Long> nonExistingCountryId = getNonExistentId();
		if (nonExistingCountryId.isEmpty()) {
			fail("All country id occupied");
		}

		mvc.perform(delete("/api/country/{id}", nonExistingCountryId.get())).andExpect(status().isOk());

		Optional<Country> nonExistingEntity = countryRepository.findById(nonExistingCountryId.get());
		assertThat(nonExistingEntity).isEmpty();
	}

}
