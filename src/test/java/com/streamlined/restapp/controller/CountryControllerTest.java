package com.streamlined.restapp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
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
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamlined.restapp.RestApplication;
import com.streamlined.restapp.dao.CountryRepository;
import com.streamlined.restapp.data.Continent;
import com.streamlined.restapp.data.Country;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = RestApplication.class)
@AutoConfigureMockMvc
@Transactional
class CountryControllerTest {

	private static final List<Country> COUNTRY_LIST = List.of(
			Country.builder().id(1L).name("USA").continent(Continent.NORTH_AMERICA).capital("Washington")
					.population(334914895).square(8080470).build(),
			Country.builder().id(2L).name("United Kingdom").continent(Continent.EUROPE).capital("London")
					.population(67596281).square(244376).build(),
			Country.builder().id(3L).name("Canada").continent(Continent.NORTH_AMERICA).capital("Ottawa")
					.population(40769890).square(9984670).build(),
			Country.builder().id(4L).name("Netherlands").continent(Continent.EUROPE).capital("Amsterdam")
					.population(18072300).square(42531).build(),
			Country.builder().id(5L).name("Germany").continent(Continent.EUROPE).capital("Berlin").population(84607016)
					.square(357600).build(),
			Country.builder().id(6L).name("France").continent(Continent.EUROPE).capital("Paris").population(68373433)
					.square(643801).build(),
			Country.builder().id(7L).name("Spain").continent(Continent.EUROPE).capital("Madrid").population(48592909)
					.square(505994).build(),
			Country.builder().id(8L).name("Portugal").continent(Continent.EUROPE).capital("Lisbon").population(10467366)
					.square(92230).build(),
			Country.builder().id(9L).name("Belgium").continent(Continent.EUROPE).capital("Brussels")
					.population(11697557).square(30689).build(),
			Country.builder().id(10L).name("Poland").continent(Continent.EUROPE).capital("Warsaw").population(38036118)
					.square(312696).build(),
			Country.builder().id(11L).name("Romania").continent(Continent.EUROPE).capital("Bucharest")
					.population(19051562).square(238398).build(),
			Country.builder().id(12L).name("Bulgaria").continent(Continent.EUROPE).capital("Sofia").population(6447710)
					.square(110994).build(),
			Country.builder().id(13L).name("Czech Republic").continent(Continent.EUROPE).capital("Prague")
					.population(10900555).square(78871).build(),
			Country.builder().id(14L).name("Slovakia").continent(Continent.EUROPE).capital("Bratislava")
					.population(5460185).square(49035).build(),
			Country.builder().id(15L).name("Hungary").continent(Continent.EUROPE).capital("Budapest")
					.population(9597085).square(93030).build(),
			Country.builder().id(16L).name("Greece").continent(Continent.EUROPE).capital("Athens").population(10413982)
					.square(131957).build(),
			Country.builder().id(17L).name("Italy").continent(Continent.EUROPE).capital("Rome").population(58853482)
					.square(301340).build(),
			Country.builder().id(18L).name("Ukraine").continent(Continent.EUROPE).capital("Kyiv").population(33365000)
					.square(603628).build());

	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper mapper;
	@Autowired
	private CountryRepository countryRepository;

	@Test
	void testGetAllCountriesSuccess() throws Exception {

		MvcResult mvcResult = mvc.perform(get("/api/country"))
				.andExpectAll(status().isOk(), content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		var content = mvcResult.getResponse().getContentAsString();
		var collectionType = mapper.getTypeFactory().constructCollectionType(List.class, Country.class);
		var value = mapper.readValue(content, collectionType);

		assertThat(value).isNotNull().asList().hasSize(COUNTRY_LIST.size())
				.containsExactlyInAnyOrderElementsOf(COUNTRY_LIST);
	}

	@Test
	void testGetCountryByIdSuccess() throws Exception {
		final int COUNTRY_INDEX = 0;
		final long COUNTRY_ID = 1L;

		MvcResult mvcResult = mvc.perform(get("/api/country/{id}", COUNTRY_ID))
				.andExpectAll(status().isOk(), content().contentType(MediaType.APPLICATION_JSON)).andReturn();
		var content = mvcResult.getResponse().getContentAsString();
		var value = mapper.readValue(content, Country.class);

		assertThat(value).isNotNull().usingRecursiveComparison().isEqualTo(COUNTRY_LIST.get(COUNTRY_INDEX));
	}

	@Test
	void testGetCountryByIdFailNotFound() throws Exception {
		final long NON_EXISTING_COUNTRY_ID = 1000L;

		mvc.perform(get("/api/country/{id}", NON_EXISTING_COUNTRY_ID)).andExpectAll(status().isNotFound());
	}

	@Test
	void testAddCountrySuccess() throws Exception {
		final Country newCountry = Country.builder().name("India").continent(Continent.ASIA).capital("New Delhi")
				.population(1428627663).square(3287263).build();
		String requestBody = mapper.writeValueAsString(newCountry);

		MvcResult mvcResult = mvc
				.perform(post("/api/country").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isCreated()).andReturn();

		var locationHeader = mvcResult.getResponse().getHeader("Location");
		assertThat(locationHeader).matchesSatisfying(Pattern.compile("http://localhost/api/country/(\\d+)"),
				matcher -> {
					var entityId = Long.valueOf(matcher.group(1));
					newCountry.setId(entityId);
					assertThat(countryRepository.findById(entityId)).contains(newCountry);
				});
	}

	@ParameterizedTest
	@ValueSource(strings = { "USA", "   ", "US" })
	void testAddCountryFailInvalidCountryName(String countryName) throws Exception {
		final Country newCountry = Country.builder().name(countryName).continent(Continent.ASIA).capital("New Delhi")
				.population(1428627663).square(3287263).build();
		String requestBody = mapper.writeValueAsString(newCountry);

		mvc.perform(post("/api/country").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isBadRequest());
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
		final long COUNTRY_ID = 1L;
		final Country newCountry = Country.builder().id(COUNTRY_ID).name("India").continent(Continent.ASIA)
				.capital("New Delhi").population(1428627663).square(3287263).build();
		String requestBody = mapper.writeValueAsString(newCountry);

		mvc.perform(put("/api/country/{id}", COUNTRY_ID).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isOk());

		var updatedEntity = countryRepository.findById(COUNTRY_ID);
		assertThat(updatedEntity).contains(newCountry);
	}

	void testUpdateCountryFailNonUniqueCountryName(String countryName) throws Exception {
		final long COUNTRY_ID = 2L;
		final Country newCountry = Country.builder().name("USA").continent(Continent.ASIA).capital("New Delhi")
				.population(1428627663).square(3287263).build();
		String requestBody = mapper.writeValueAsString(newCountry);

		mvc.perform(put("/api/country/{id}", COUNTRY_ID).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isBadRequest());
	}

	@ParameterizedTest
	@ValueSource(strings = { "   ", "US" })
	void testUpdateCountryFailInvalidCountryName(String countryName) throws Exception {
		final long COUNTRY_ID = 1L;
		final Country newCountry = Country.builder().name(countryName).continent(Continent.ASIA).capital("New Delhi")
				.population(1428627663).square(3287263).build();
		String requestBody = mapper.writeValueAsString(newCountry);

		mvc.perform(put("/api/country/{id}", COUNTRY_ID).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isBadRequest());
	}

	@Test
	void testUpdateCountryFailNullContinent() throws Exception {
		final long COUNTRY_ID = 1L;
		final Country newCountry = Country.builder().name("India").continent(null).capital("New Delhi")
				.population(1428627663).square(3287263).build();
		String requestBody = mapper.writeValueAsString(newCountry);

		mvc.perform(put("/api/country/{id}", COUNTRY_ID).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isBadRequest());
	}

	@ParameterizedTest
	@ValueSource(strings = { "   ", "AB" })
	void testUpdateCountryFailInvalidCapital(String capital) throws Exception {
		final long COUNTRY_ID = 1L;
		final Country newCountry = Country.builder().name("India").continent(Continent.ASIA).capital(capital)
				.population(1428627663).square(3287263).build();
		String requestBody = mapper.writeValueAsString(newCountry);

		mvc.perform(put("/api/country/{id}", COUNTRY_ID).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isBadRequest());
	}

	@ParameterizedTest
	@ValueSource(ints = { -100, 0 })
	void testUpdateCountryFailNegativeZeroPopulation(int population) throws Exception {
		final long COUNTRY_ID = 1L;
		final Country newCountry = Country.builder().name("India").continent(Continent.ASIA).capital("New Delhi")
				.population(population).square(3287263).build();
		String requestBody = mapper.writeValueAsString(newCountry);

		mvc.perform(put("/api/country/{id}", COUNTRY_ID).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isBadRequest());
	}

	@ParameterizedTest
	@ValueSource(doubles = { -10_000D, 0D })
	void testUpdateCountryFailNegativeZeroArea(double area) throws Exception {
		final long COUNTRY_ID = 1L;
		final Country newCountry = Country.builder().name("India").continent(Continent.ASIA).capital("New Delhi")
				.population(1428627663).square(area).build();
		String requestBody = mapper.writeValueAsString(newCountry);

		mvc.perform(put("/api/country/{id}", COUNTRY_ID).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isBadRequest());
	}

	@Test
	void testDeleteCountrySuccess() throws Exception {
		final long COUNTRY_ID = 1L;

		mvc.perform(delete("/api/country/{id}", COUNTRY_ID)).andExpect(status().isOk());

		var deletedEntity = countryRepository.findById(COUNTRY_ID);
		assertThat(deletedEntity).isEmpty();
	}

	@Test
	void testDeleteCountryFailNotFound() throws Exception {
		final long NON_EXISTING_COUNTRY_ID = 1000L;

		mvc.perform(delete("/api/country/{id}", NON_EXISTING_COUNTRY_ID)).andExpect(status().isOk());

		var nonExistingEntity = countryRepository.findById(NON_EXISTING_COUNTRY_ID);
		assertThat(nonExistingEntity).isEmpty();
	}

}
