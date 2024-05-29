package com.streamlined.restapp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.streamlined.restapp.RestApplication;
import com.streamlined.restapp.dao.PersonRepository;
import com.streamlined.restapp.data.Color;
import com.streamlined.restapp.data.Continent;
import com.streamlined.restapp.data.Country;
import com.streamlined.restapp.data.Person;
import com.streamlined.restapp.data.Sex;
import com.streamlined.restapp.dto.EssentialPersonDto;
import com.streamlined.restapp.dto.PersonListDto;
import com.streamlined.restapp.dto.UploadResponse;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = RestApplication.class)
@AutoConfigureMockMvc
@Transactional
class PersonControllerTest {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final String MULTIPART_FILE_NAME = "file";

	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper mapper;
	@Autowired
	private PersonRepository personRepository;

	@Test
	void testGetAllPersonsSuccess() throws Exception {
		final Country country = Country.builder().id(1L).name("India").continent(Continent.ASIA).capital("New Delhi")
				.population(1428627663).square(3287263).build();
		final List<Person> personList = List.of(
				Person.builder().name("John Smith").birthday(LocalDate.of(1990, 1, 1)).sex(Sex.MALE)
						.eyeColor(Color.GREEN).hairColor(Color.BLACK).weight(BigDecimal.valueOf(80))
						.height(BigDecimal.valueOf(190)).countryOfOrigin(country).citizenship(country)
						.favoriteMeals("apple,pear,banana").build(),
				Person.builder().name("Jacky Blacksmith").birthday(LocalDate.of(1980, 1, 1)).sex(Sex.FEMALE)
						.eyeColor(Color.BLUE).hairColor(Color.YELLOW).weight(BigDecimal.valueOf(70))
						.height(BigDecimal.valueOf(160)).countryOfOrigin(country).citizenship(country)
						.favoriteMeals("banana,apple,pear").build(),
				Person.builder().name("Ruth Glanshow").birthday(LocalDate.of(2000, 1, 1)).sex(Sex.FEMALE)
						.eyeColor(Color.RED).hairColor(Color.GREEN).weight(BigDecimal.valueOf(50))
						.height(BigDecimal.valueOf(120)).countryOfOrigin(country).citizenship(country)
						.favoriteMeals("pear,apple,banana").build());
		personRepository.deleteAll();
		personRepository.saveAll(personList);

		MvcResult mvcResult = mvc.perform(get("/api/person"))
				.andExpectAll(status().isOk(), content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		String content = mvcResult.getResponse().getContentAsString();
		CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, Person.class);
		Object value = mapper.readValue(content, collectionType);

		assertThat(value).isNotNull().asList().usingRecursiveComparison().isEqualTo(personList);
	}

	@Test
	void testGetPersonByIdSuccess() throws Exception {
		Country newCountry = Country.builder().id(1L).name("India").continent(Continent.ASIA).capital("New Delhi")
				.population(1428627663).square(3287263).build();
		Person newPerson = Person.builder().name("John Smith").birthday(LocalDate.of(1990, 1, 1)).sex(Sex.MALE)
				.eyeColor(Color.GREEN).hairColor(Color.BLACK).weight(BigDecimal.valueOf(80))
				.height(BigDecimal.valueOf(190)).countryOfOrigin(newCountry).citizenship(newCountry)
				.favoriteMeals("apple,pear,banana").build();

		newPerson = personRepository.save(newPerson);

		MvcResult mvcResult = mvc.perform(get("/api/person/{id}", newPerson.getId()))
				.andExpectAll(status().isOk(), content().contentType(MediaType.APPLICATION_JSON)).andReturn();
		String content = mvcResult.getResponse().getContentAsString();
		Person person = mapper.readValue(content, Person.class);

		assertThat(person).isNotNull().usingRecursiveComparison().isEqualTo(newPerson);
	}

	@Test
	void testGetPersonByIdFailNotFound() throws Exception {
		final Long PERSON_ID = 1000L;

		personRepository.deleteById(PERSON_ID);

		mvc.perform(get("/api/person/{id}", PERSON_ID)).andExpectAll(status().isNotFound());
	}

	@Test
	void testAddPersonSuccess() throws Exception {
		Country newCountry = Country.builder().id(1L).name("India").continent(Continent.ASIA).capital("New Delhi")
				.population(1428627663).square(3287263).build();
		Person newPerson = Person.builder().name("John Smith").birthday(LocalDate.of(1990, 1, 1)).sex(Sex.MALE)
				.eyeColor(Color.GREEN).hairColor(Color.BLACK).weight(BigDecimal.valueOf(80))
				.height(BigDecimal.valueOf(190)).countryOfOrigin(newCountry).citizenship(newCountry)
				.favoriteMeals("apple,pear,banana").build();
		String requestBody = mapper.writeValueAsString(newPerson);

		MvcResult mvcResult = mvc
				.perform(post("/api/person").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isCreated()).andReturn();

		String locationHeader = mvcResult.getResponse().getHeader("Location");
		assertThat(locationHeader).matchesSatisfying(Pattern.compile("http://localhost/api/person/(\\d+)"), matcher -> {
			Long entityId = Long.valueOf(matcher.group(1));
			newPerson.setId(entityId);
			assertThat(personRepository.findById(entityId)).contains(newPerson);
		});
	}

	@ParameterizedTest
	@ValueSource(strings = { "   ", "AB" })
	void testAddPersonFailInvalidPersonName(String personName) throws Exception {
		Country newCountry = Country.builder().id(1L).name("India").continent(Continent.ASIA).capital("New Delhi")
				.population(1428627663).square(3287263).build();
		Person newPerson = Person.builder().name(personName).birthday(LocalDate.of(1990, 1, 1)).sex(Sex.MALE)
				.eyeColor(Color.GREEN).hairColor(Color.BLACK).weight(BigDecimal.valueOf(80))
				.height(BigDecimal.valueOf(190)).countryOfOrigin(newCountry).citizenship(newCountry)
				.favoriteMeals("apple,pear,banana").build();
		String requestBody = mapper.writeValueAsString(newPerson);

		mvc.perform(post("/api/person").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isBadRequest());
	}

	@Test
	void testAddPersonFailInvalidBirthday() throws Exception {
		Country newCountry = Country.builder().id(1L).name("India").continent(Continent.ASIA).capital("New Delhi")
				.population(1428627663).square(3287263).build();
		LocalDate birthDay = LocalDate.now().plusYears(10);
		Person newPerson = Person.builder().name("Nicholas Green").birthday(birthDay).sex(Sex.MALE)
				.eyeColor(Color.GREEN).hairColor(Color.BLACK).weight(BigDecimal.valueOf(80))
				.height(BigDecimal.valueOf(190)).countryOfOrigin(newCountry).citizenship(newCountry)
				.favoriteMeals("apple,pear,banana").build();
		String requestBody = mapper.writeValueAsString(newPerson);

		mvc.perform(post("/api/person").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isBadRequest());
	}

	@Test
	void testAddPersonFailNullBirthday() throws Exception {
		Country newCountry = Country.builder().id(1L).name("India").continent(Continent.ASIA).capital("New Delhi")
				.population(1428627663).square(3287263).build();
		Person newPerson = Person.builder().name("Nicholas Green").birthday(null).sex(Sex.MALE).eyeColor(Color.GREEN)
				.hairColor(Color.BLACK).weight(BigDecimal.valueOf(80)).height(BigDecimal.valueOf(190))
				.countryOfOrigin(newCountry).citizenship(newCountry).favoriteMeals("apple,pear,banana").build();
		String requestBody = mapper.writeValueAsString(newPerson);

		mvc.perform(post("/api/person").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isBadRequest());
	}

	@Test
	void testAddPersonFailNonUniquePersonNameAndBirthday() throws Exception {
		Country newCountry = Country.builder().id(1L).name("India").continent(Continent.ASIA).capital("New Delhi")
				.population(1428627663).square(3287263).build();
		Person newPerson = Person.builder().name("Ronald Smith").birthday(LocalDate.of(1990, 1, 1)).sex(Sex.MALE)
				.eyeColor(Color.GREEN).hairColor(Color.BLACK).weight(BigDecimal.valueOf(80))
				.height(BigDecimal.valueOf(190)).countryOfOrigin(newCountry).citizenship(newCountry)
				.favoriteMeals("apple,pear,banana").build();
		String requestBody = mapper.writeValueAsString(newPerson);
		personRepository.save(newPerson);

		mvc.perform(post("/api/person").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isBadRequest());
	}

	@Test
	void testAddPersonFailNullSex() throws Exception {
		Country newCountry = Country.builder().id(1L).name("India").continent(Continent.ASIA).capital("New Delhi")
				.population(1428627663).square(3287263).build();
		Person newPerson = Person.builder().name("Nicholas Green").birthday(LocalDate.of(2000, 1, 1)).sex(null)
				.eyeColor(Color.GREEN).hairColor(Color.BLACK).weight(BigDecimal.valueOf(80))
				.height(BigDecimal.valueOf(190)).countryOfOrigin(newCountry).citizenship(newCountry)
				.favoriteMeals("apple,pear,banana").build();
		String requestBody = mapper.writeValueAsString(newPerson);

		mvc.perform(post("/api/person").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isBadRequest());
	}

	@Test
	void testAddPersonFailNullEyeColor() throws Exception {
		Country newCountry = Country.builder().id(1L).name("India").continent(Continent.ASIA).capital("New Delhi")
				.population(1428627663).square(3287263).build();
		Person newPerson = Person.builder().name("Nicholas Green").birthday(LocalDate.of(2000, 1, 1)).sex(Sex.MALE)
				.eyeColor(null).hairColor(Color.BLACK).weight(BigDecimal.valueOf(80)).height(BigDecimal.valueOf(190))
				.countryOfOrigin(newCountry).citizenship(newCountry).favoriteMeals("apple,pear,banana").build();
		String requestBody = mapper.writeValueAsString(newPerson);

		mvc.perform(post("/api/person").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isBadRequest());
	}

	@Test
	void testAddPersonFailNullHairColor() throws Exception {
		Country newCountry = Country.builder().id(1L).name("India").continent(Continent.ASIA).capital("New Delhi")
				.population(1428627663).square(3287263).build();
		Person newPerson = Person.builder().name("Nicholas Green").birthday(LocalDate.of(2000, 1, 1)).sex(Sex.MALE)
				.eyeColor(Color.GREEN).hairColor(null).weight(BigDecimal.valueOf(80)).height(BigDecimal.valueOf(190))
				.countryOfOrigin(newCountry).citizenship(newCountry).favoriteMeals("apple,pear,banana").build();
		String requestBody = mapper.writeValueAsString(newPerson);

		mvc.perform(post("/api/person").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isBadRequest());
	}

	@Test
	void testAddPersonFailNullCountryOfOrigin() throws Exception {
		Country newCountry = Country.builder().id(1L).name("India").continent(Continent.ASIA).capital("New Delhi")
				.population(1428627663).square(3287263).build();
		Person newPerson = Person.builder().name("Nicholas Green").birthday(LocalDate.of(2000, 1, 1)).sex(Sex.MALE)
				.eyeColor(Color.GREEN).hairColor(Color.BLACK).weight(BigDecimal.valueOf(80))
				.height(BigDecimal.valueOf(190)).countryOfOrigin(null).citizenship(newCountry)
				.favoriteMeals("apple,pear,banana").build();
		String requestBody = mapper.writeValueAsString(newPerson);

		mvc.perform(post("/api/person").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isBadRequest());
	}

	@Test
	void testAddPersonFailNullCitizenship() throws Exception {
		Country newCountry = Country.builder().id(1L).name("India").continent(Continent.ASIA).capital("New Delhi")
				.population(1428627663).square(3287263).build();
		Person newPerson = Person.builder().name("Nicholas Green").birthday(LocalDate.of(2000, 1, 1)).sex(Sex.MALE)
				.eyeColor(Color.GREEN).hairColor(Color.BLACK).weight(BigDecimal.valueOf(80))
				.height(BigDecimal.valueOf(190)).countryOfOrigin(newCountry).citizenship(null)
				.favoriteMeals("apple,pear,banana").build();
		String requestBody = mapper.writeValueAsString(newPerson);

		mvc.perform(post("/api/person").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isBadRequest());
	}

	@ParameterizedTest
	@ValueSource(doubles = { -100, 49, 151, 300 })
	void testAddPersonFailWrongWeight(double weight) throws Exception {
		Country newCountry = Country.builder().id(1L).name("India").continent(Continent.ASIA).capital("New Delhi")
				.population(1428627663).square(3287263).build();
		Person newPerson = Person.builder().name("Nicholas Green").birthday(LocalDate.of(2000, 1, 1)).sex(Sex.MALE)
				.eyeColor(Color.GREEN).hairColor(Color.BLACK).weight(BigDecimal.valueOf(weight))
				.height(BigDecimal.valueOf(190)).countryOfOrigin(newCountry).citizenship(newCountry)
				.favoriteMeals("apple,pear,banana").build();
		String requestBody = mapper.writeValueAsString(newPerson);

		mvc.perform(post("/api/person").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isBadRequest());
	}

	@ParameterizedTest
	@ValueSource(doubles = { -100, 59, 221, 300 })
	void testAddPersonFailWrongHeight(double height) throws Exception {
		Country newCountry = Country.builder().id(1L).name("India").continent(Continent.ASIA).capital("New Delhi")
				.population(1428627663).square(3287263).build();
		Person newPerson = Person.builder().name("Nicholas Green").birthday(LocalDate.of(2000, 1, 1)).sex(Sex.MALE)
				.eyeColor(Color.GREEN).hairColor(Color.BLACK).weight(BigDecimal.valueOf(80))
				.height(BigDecimal.valueOf(height)).countryOfOrigin(newCountry).citizenship(newCountry)
				.favoriteMeals("apple,pear,banana").build();
		String requestBody = mapper.writeValueAsString(newPerson);

		mvc.perform(post("/api/person").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isBadRequest());
	}

	@ParameterizedTest
	@ValueSource(strings = { ",apple", "apple,", "apple;pear", "apple=pear" })
	void testAddPersonFailWrongFavoriteMeals(String favoriteMeals) throws Exception {
		Country newCountry = Country.builder().id(1L).name("India").continent(Continent.ASIA).capital("New Delhi")
				.population(1428627663).square(3287263).build();
		Person newPerson = Person.builder().name("Nicholas Green").birthday(LocalDate.of(2000, 1, 1)).sex(Sex.MALE)
				.eyeColor(Color.GREEN).hairColor(Color.BLACK).weight(BigDecimal.valueOf(80))
				.height(BigDecimal.valueOf(180)).countryOfOrigin(newCountry).citizenship(newCountry)
				.favoriteMeals(favoriteMeals).build();
		String requestBody = mapper.writeValueAsString(newPerson);

		mvc.perform(post("/api/person").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isBadRequest());
	}

	@Test
	void testUpdatePersonSuccess() throws Exception {
		Country newCountry = Country.builder().id(1L).name("India").continent(Continent.ASIA).capital("New Delhi")
				.population(1428627663).square(3287263).build();
		Person newPerson = Person.builder().name("Nicholas Green").birthday(LocalDate.of(2000, 1, 1)).sex(Sex.MALE)
				.eyeColor(Color.GREEN).hairColor(Color.BLACK).weight(BigDecimal.valueOf(80))
				.height(BigDecimal.valueOf(180)).countryOfOrigin(newCountry).citizenship(newCountry)
				.favoriteMeals("apple,pear,banana").build();
		Person updatedPerson = Person.builder().name("Nicholas Green").birthday(LocalDate.of(2000, 1, 1)).sex(Sex.MALE)
				.eyeColor(Color.YELLOW).hairColor(Color.RED).weight(BigDecimal.valueOf(80))
				.height(BigDecimal.valueOf(180)).countryOfOrigin(newCountry).citizenship(newCountry)
				.favoriteMeals("apple,pear,banana").build();

		newPerson = personRepository.save(newPerson);
		final Long PERSON_ID = newPerson.getId();
		updatedPerson.setId(PERSON_ID);

		String requestBody = mapper.writeValueAsString(updatedPerson);
		mvc.perform(put("/api/person/{id}", PERSON_ID).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isOk());

		Optional<Person> fetchedPerson = personRepository.findById(PERSON_ID);
		assertThat(fetchedPerson).contains(updatedPerson);
	}

	@Test
	void testDeletePersonSuccess() throws Exception {
		Country newCountry = Country.builder().id(1L).name("India").continent(Continent.ASIA).capital("New Delhi")
				.population(1428627663).square(3287263).build();
		Person newPerson = Person.builder().name("Nicholas Green").birthday(LocalDate.of(2000, 1, 1)).sex(Sex.MALE)
				.eyeColor(Color.GREEN).hairColor(Color.BLACK).weight(BigDecimal.valueOf(80))
				.height(BigDecimal.valueOf(180)).countryOfOrigin(newCountry).citizenship(newCountry)
				.favoriteMeals("apple,pear,banana").build();

		newPerson = personRepository.save(newPerson);
		final Long PERSON_ID = newPerson.getId();

		mvc.perform(delete("/api/person/{id}", PERSON_ID)).andExpect(status().isOk());

		Optional<Person> deletedEntity = personRepository.findById(PERSON_ID);
		assertThat(deletedEntity).isEmpty();
	}

	@Test
	void testDeletePersonFailNotFound() throws Exception {
		final long NON_EXISTING_PERSON_ID = 1000L;
		personRepository.deleteById(NON_EXISTING_PERSON_ID);

		mvc.perform(delete("/api/person/{id}", NON_EXISTING_PERSON_ID)).andExpect(status().isOk());

		Optional<Person> nonExistingEntity = personRepository.findById(NON_EXISTING_PERSON_ID);
		assertThat(nonExistingEntity).isEmpty();
	}

	@Test
	void testDeleteAllSuccess() throws Exception {
		Country newCountry = Country.builder().id(1L).name("India").continent(Continent.ASIA).capital("New Delhi")
				.population(1428627663).square(3287263).build();
		Person newPerson = Person.builder().name("Nicholas Green").birthday(LocalDate.of(2000, 1, 1)).sex(Sex.MALE)
				.eyeColor(Color.GREEN).hairColor(Color.BLACK).weight(BigDecimal.valueOf(80))
				.height(BigDecimal.valueOf(180)).countryOfOrigin(newCountry).citizenship(newCountry)
				.favoriteMeals("apple,pear,banana").build();

		newPerson = personRepository.save(newPerson);

		mvc.perform(delete("/api/person")).andExpect(status().isOk());

		long existingEntitiesCount = personRepository.count();
		assertThat(existingEntitiesCount).isZero();
	}

	@Test
	void testGetPersonListFilterByPropertyValueSuccess() throws Exception {
		final Country country = Country.builder().id(1L).name("India").continent(Continent.ASIA).capital("New Delhi")
				.population(1428627663).square(3287263).build();
		final List<Person> personList = List.of(
				Person.builder().name("John Smith").birthday(LocalDate.of(1990, 1, 1)).sex(Sex.MALE)
						.eyeColor(Color.GREEN).hairColor(Color.BLACK).weight(BigDecimal.valueOf(80))
						.height(BigDecimal.valueOf(190)).countryOfOrigin(country).citizenship(country)
						.favoriteMeals("apple,pear,banana").build(),
				Person.builder().name("Jacky Blacksmith").birthday(LocalDate.of(1980, 1, 1)).sex(Sex.FEMALE)
						.eyeColor(Color.RED).hairColor(Color.YELLOW).weight(BigDecimal.valueOf(70))
						.height(BigDecimal.valueOf(160)).countryOfOrigin(country).citizenship(country)
						.favoriteMeals("banana,apple,pear").build(),
				Person.builder().name("Ruth Glanshow").birthday(LocalDate.of(2000, 1, 1)).sex(Sex.FEMALE)
						.eyeColor(Color.RED).hairColor(Color.YELLOW).weight(BigDecimal.valueOf(50))
						.height(BigDecimal.valueOf(120)).countryOfOrigin(country).citizenship(country)
						.favoriteMeals("pear,apple,banana").build(),
				Person.builder().name("Richard Galsworthy").birthday(LocalDate.of(1990, 1, 1)).sex(Sex.MALE)
						.eyeColor(Color.GREEN).hairColor(Color.BLACK).weight(BigDecimal.valueOf(80))
						.height(BigDecimal.valueOf(190)).countryOfOrigin(country).citizenship(country)
						.favoriteMeals("apple,pear,banana").build(),
				Person.builder().name("Sheila Winslow").birthday(LocalDate.of(1980, 1, 1)).sex(Sex.FEMALE)
						.eyeColor(Color.RED).hairColor(Color.YELLOW).weight(BigDecimal.valueOf(70))
						.height(BigDecimal.valueOf(160)).countryOfOrigin(country).citizenship(country)
						.favoriteMeals("banana,apple,pear").build(),
				Person.builder().name("Becky Steep").birthday(LocalDate.of(2000, 1, 1)).sex(Sex.FEMALE)
						.eyeColor(Color.RED).hairColor(Color.YELLOW).weight(BigDecimal.valueOf(50))
						.height(BigDecimal.valueOf(120)).countryOfOrigin(country).citizenship(country)
						.favoriteMeals("pear,apple,banana").build());
		final List<EssentialPersonDto> result = List.of(
				EssentialPersonDto.builder().name("Sheila Winslow").birthday(LocalDate.of(1980, 1, 1)).sex(Sex.FEMALE)
						.eyeColor(Color.RED).height(BigDecimal.valueOf(160)).build(),
				EssentialPersonDto.builder().name("Becky Steep").birthday(LocalDate.of(2000, 1, 1)).sex(Sex.FEMALE)
						.eyeColor(Color.RED).height(BigDecimal.valueOf(120)).build());
		personRepository.deleteAll();
		personRepository.saveAll(personList);

		String requestBody = """
				{
				        "sex":"FEMALE",
				        "eyeColor":"RED",
				        "hairColor":"YELLOW",
				        "page":1,
				        "size":2
				}
				""";
		MvcResult mvcResult = mvc
				.perform(post("/api/person/_list").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isOk(), content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		String content = mvcResult.getResponse().getContentAsString();
		PersonListDto response = mapper.readValue(content, PersonListDto.class);

		assertThat(response).isNotNull();
		assertThat(response.totalPages()).isEqualTo(2);
		assertThat(response.list()).hasSize(2).usingRecursiveComparison(RecursiveComparisonConfiguration.builder()
				.withComparedFields("name", "birthday", "sex", "eyeColor", "height").build()).isEqualTo(result);
	}

	@Test
	void testGetPersonListFilterByCountryReferenceSuccess() throws Exception {
		final Country usa = Country.builder().id(1L).name("USA").continent(Continent.NORTH_AMERICA)
				.capital("Washington").population(1428627663).square(3287263).build();
		final Country uk = Country.builder().id(2L).name("United Kingdom").continent(Continent.EUROPE).capital("London")
				.population(1428627663).square(3287263).build();
		final Country france = Country.builder().id(3L).name("France").continent(Continent.EUROPE).capital("Paris")
				.population(1428627663).square(3287263).build();
		final List<Person> personList = List.of(
				Person.builder().name("John Smith").birthday(LocalDate.of(1990, 1, 1)).sex(Sex.MALE)
						.eyeColor(Color.GREEN).hairColor(Color.BLACK).weight(BigDecimal.valueOf(80))
						.height(BigDecimal.valueOf(190)).countryOfOrigin(usa).citizenship(usa)
						.favoriteMeals("apple,pear,banana").build(),
				Person.builder().name("Jacky Blacksmith").birthday(LocalDate.of(1980, 1, 1)).sex(Sex.FEMALE)
						.eyeColor(Color.RED).hairColor(Color.YELLOW).weight(BigDecimal.valueOf(70))
						.height(BigDecimal.valueOf(160)).countryOfOrigin(uk).citizenship(uk)
						.favoriteMeals("banana,apple,pear").build(),
				Person.builder().name("Ruth Glanshow").birthday(LocalDate.of(2000, 1, 1)).sex(Sex.FEMALE)
						.eyeColor(Color.RED).hairColor(Color.YELLOW).weight(BigDecimal.valueOf(50))
						.height(BigDecimal.valueOf(120)).countryOfOrigin(france).citizenship(france)
						.favoriteMeals("pear,apple,banana").build(),
				Person.builder().name("Richard Galsworthy").birthday(LocalDate.of(1990, 1, 1)).sex(Sex.MALE)
						.eyeColor(Color.GREEN).hairColor(Color.BLACK).weight(BigDecimal.valueOf(80))
						.height(BigDecimal.valueOf(190)).countryOfOrigin(usa).citizenship(usa)
						.favoriteMeals("apple,pear,banana").build(),
				Person.builder().name("Sheila Winslow").birthday(LocalDate.of(1980, 1, 1)).sex(Sex.FEMALE)
						.eyeColor(Color.RED).hairColor(Color.YELLOW).weight(BigDecimal.valueOf(70))
						.height(BigDecimal.valueOf(160)).countryOfOrigin(uk).citizenship(uk)
						.favoriteMeals("banana,apple,pear").build(),
				Person.builder().name("Becky Steep").birthday(LocalDate.of(2000, 1, 1)).sex(Sex.FEMALE)
						.eyeColor(Color.RED).hairColor(Color.YELLOW).weight(BigDecimal.valueOf(50))
						.height(BigDecimal.valueOf(120)).countryOfOrigin(france).citizenship(france)
						.favoriteMeals("pear,apple,banana").build());
		final List<EssentialPersonDto> result = List.of(
				EssentialPersonDto.builder().name("John Smith").birthday(LocalDate.of(1990, 1, 1)).sex(Sex.MALE)
						.eyeColor(Color.GREEN).height(BigDecimal.valueOf(190)).build(),
				EssentialPersonDto.builder().name("Richard Galsworthy").birthday(LocalDate.of(1990, 1, 1)).sex(Sex.MALE)
						.eyeColor(Color.GREEN).height(BigDecimal.valueOf(190)).build());
		personRepository.deleteAll();
		personRepository.saveAll(personList);

		String requestBody = """
				{
				           "countryOfOrigin":{
				               "name": "USA"
				           },
				           "citizenship":{
				           		"capital": "Washington"
				           },
					       "page":0,
					       "size":2
				}
				""";
		MvcResult mvcResult = mvc
				.perform(post("/api/person/_list").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isOk(), content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		String content = mvcResult.getResponse().getContentAsString();
		PersonListDto response = mapper.readValue(content, PersonListDto.class);

		assertThat(response).isNotNull();
		assertThat(response.totalPages()).isEqualTo(1);
		assertThat(response.list()).hasSize(2).usingRecursiveComparison(RecursiveComparisonConfiguration.builder()
				.withComparedFields("name", "birthday", "sex", "eyeColor", "height").build()).isEqualTo(result);
	}

	@Test
	void testGetPersonListFilterByCountryIdSuccess() throws Exception {
		final Country usa = Country.builder().id(1L).name("USA").continent(Continent.NORTH_AMERICA)
				.capital("Washington").population(1428627663).square(3287263).build();
		final Country uk = Country.builder().id(2L).name("United Kingdom").continent(Continent.EUROPE).capital("London")
				.population(1428627663).square(3287263).build();
		final Country france = Country.builder().id(3L).name("France").continent(Continent.EUROPE).capital("Paris")
				.population(1428627663).square(3287263).build();
		final List<Person> personList = List.of(
				Person.builder().name("John Smith").birthday(LocalDate.of(1990, 1, 1)).sex(Sex.MALE)
						.eyeColor(Color.GREEN).hairColor(Color.BLACK).weight(BigDecimal.valueOf(80))
						.height(BigDecimal.valueOf(190)).countryOfOrigin(usa).citizenship(usa)
						.favoriteMeals("apple,pear,banana").build(),
				Person.builder().name("Jacky Blacksmith").birthday(LocalDate.of(1980, 1, 1)).sex(Sex.FEMALE)
						.eyeColor(Color.RED).hairColor(Color.YELLOW).weight(BigDecimal.valueOf(70))
						.height(BigDecimal.valueOf(160)).countryOfOrigin(uk).citizenship(uk)
						.favoriteMeals("banana,apple,pear").build(),
				Person.builder().name("Ruth Glanshow").birthday(LocalDate.of(2000, 1, 1)).sex(Sex.FEMALE)
						.eyeColor(Color.RED).hairColor(Color.YELLOW).weight(BigDecimal.valueOf(50))
						.height(BigDecimal.valueOf(120)).countryOfOrigin(france).citizenship(france)
						.favoriteMeals("pear,apple,banana").build(),
				Person.builder().name("Richard Galsworthy").birthday(LocalDate.of(1990, 1, 1)).sex(Sex.MALE)
						.eyeColor(Color.GREEN).hairColor(Color.BLACK).weight(BigDecimal.valueOf(80))
						.height(BigDecimal.valueOf(190)).countryOfOrigin(usa).citizenship(usa)
						.favoriteMeals("apple,pear,banana").build(),
				Person.builder().name("Sheila Winslow").birthday(LocalDate.of(1980, 1, 1)).sex(Sex.FEMALE)
						.eyeColor(Color.RED).hairColor(Color.YELLOW).weight(BigDecimal.valueOf(70))
						.height(BigDecimal.valueOf(160)).countryOfOrigin(uk).citizenship(uk)
						.favoriteMeals("banana,apple,pear").build(),
				Person.builder().name("Becky Steep").birthday(LocalDate.of(2000, 1, 1)).sex(Sex.FEMALE)
						.eyeColor(Color.RED).hairColor(Color.YELLOW).weight(BigDecimal.valueOf(50))
						.height(BigDecimal.valueOf(120)).countryOfOrigin(france).citizenship(france)
						.favoriteMeals("pear,apple,banana").build());
		final List<EssentialPersonDto> result = List.of(
				EssentialPersonDto.builder().name("John Smith").birthday(LocalDate.of(1990, 1, 1)).sex(Sex.MALE)
						.eyeColor(Color.GREEN).height(BigDecimal.valueOf(190)).build(),
				EssentialPersonDto.builder().name("Richard Galsworthy").birthday(LocalDate.of(1990, 1, 1)).sex(Sex.MALE)
						.eyeColor(Color.GREEN).height(BigDecimal.valueOf(190)).build());
		personRepository.deleteAll();
		personRepository.saveAll(personList);

		String requestBody = """
				{
				           "countryOfOrigin":{
				           		"id":1
				           },
					       "page":0,
					       "size":2
				}
				""";
		MvcResult mvcResult = mvc
				.perform(post("/api/person/_list").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isOk(), content().contentType(MediaType.APPLICATION_JSON)).andReturn();

		String content = mvcResult.getResponse().getContentAsString();
		PersonListDto response = mapper.readValue(content, PersonListDto.class);

		assertThat(response).isNotNull();
		assertThat(response.totalPages()).isEqualTo(1);
		assertThat(response.list()).hasSize(2).usingRecursiveComparison(RecursiveComparisonConfiguration.builder()
				.withComparedFields("name", "birthday", "sex", "eyeColor", "height").build()).isEqualTo(result);
	}

	@Test
	void testGetPersonListAsFileSuccess() throws Exception {
		final Country usa = Country.builder().id(1L).name("USA").continent(Continent.NORTH_AMERICA)
				.capital("Washington").population(1428627663).square(3287263).build();
		final Country france = Country.builder().id(2L).name("France").continent(Continent.EUROPE).capital("Paris")
				.population(1428627663).square(3287263).build();
		final List<Person> personList = List.of(
				Person.builder().name("John Smith").birthday(LocalDate.of(1990, 1, 1)).sex(Sex.MALE)
						.eyeColor(Color.GREEN).hairColor(Color.BLACK).weight(BigDecimal.valueOf(80))
						.height(BigDecimal.valueOf(190)).countryOfOrigin(usa).citizenship(usa)
						.favoriteMeals("apple,pear,banana").build(),
				Person.builder().name("Jacky Blacksmith").birthday(LocalDate.of(1980, 1, 1)).sex(Sex.FEMALE)
						.eyeColor(Color.BLUE).hairColor(Color.BLACK).weight(BigDecimal.valueOf(70))
						.height(BigDecimal.valueOf(160)).countryOfOrigin(usa).citizenship(usa)
						.favoriteMeals("banana,apple,pear").build(),
				Person.builder().name("Ruth Glanshow").birthday(LocalDate.of(2000, 1, 1)).sex(Sex.FEMALE)
						.eyeColor(Color.RED).hairColor(Color.YELLOW).weight(BigDecimal.valueOf(50))
						.height(BigDecimal.valueOf(120)).countryOfOrigin(france).citizenship(france)
						.favoriteMeals("pear,apple,banana").build(),
				Person.builder().name("Richard Galsworthy").birthday(LocalDate.of(1990, 1, 1)).sex(Sex.MALE)
						.eyeColor(Color.GREEN).hairColor(Color.BLACK).weight(BigDecimal.valueOf(80))
						.height(BigDecimal.valueOf(190)).countryOfOrigin(usa).citizenship(usa)
						.favoriteMeals("apple,pear,banana").build(),
				Person.builder().name("Sheila Winslow").birthday(LocalDate.of(1980, 1, 1)).sex(Sex.FEMALE)
						.eyeColor(Color.BROWN).hairColor(Color.GRAY).weight(BigDecimal.valueOf(70))
						.height(BigDecimal.valueOf(160)).countryOfOrigin(usa).citizenship(usa)
						.favoriteMeals("banana,apple,pear").build(),
				Person.builder().name("Becky Steep").birthday(LocalDate.of(2000, 1, 1)).sex(Sex.FEMALE)
						.eyeColor(Color.RED).hairColor(Color.YELLOW).weight(BigDecimal.valueOf(50))
						.height(BigDecimal.valueOf(120)).countryOfOrigin(france).citizenship(france)
						.favoriteMeals("pear,apple,banana").build());
		personRepository.deleteAll();
		personRepository.saveAll(personList);

		String requestBody = """
				{
				       "sex":"FEMALE",
				       "eyeColor":"RED",
				       "hairColor":"YELLOW"
				}
				""";
		String reply = "Ruth Glanshow;2000-01-01;FEMALE;RED;120" + LINE_SEPARATOR + "Becky Steep;2000-01-01;FEMALE;RED;120"
				+ LINE_SEPARATOR;
		MvcResult mvcResult = mvc
				.perform(post("/api/person/_report").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpectAll(status().isOk()).andReturn();

		String contentDispositionHeader = mvcResult.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION);
		String contentType = mvcResult.getResponse().getContentType();
		byte[] content = mvcResult.getResponse().getContentAsByteArray();

		assertThat(contentDispositionHeader).matches("attachment; filename=\"\\w+.csv\"");
		assertThat(contentType).isEqualTo("text/csv");
		assertThat(content).isEqualTo(reply.getBytes());
	}

	@Test
	void testUploadFileSuccess() throws Exception {
		final Country usa = Country.builder().id(1L).name("USA").continent(Continent.NORTH_AMERICA)
				.capital("Washington").population(1428627663).square(3287263).build();
		final List<Person> personList = List.of(
				Person.builder().name("John Smith").birthday(LocalDate.of(1990, 1, 1)).sex(Sex.MALE)
						.eyeColor(Color.GREEN).hairColor(Color.BLACK).weight(BigDecimal.valueOf(80))
						.height(BigDecimal.valueOf(190)).countryOfOrigin(usa).citizenship(usa)
						.favoriteMeals("apple,pear,banana").build(),
				Person.builder().name("Jacky Blacksmith").birthday(LocalDate.of(1980, 1, 1)).sex(Sex.FEMALE)
						.eyeColor(Color.BLUE).hairColor(Color.BLACK).weight(BigDecimal.valueOf(70))
						.height(BigDecimal.valueOf(160)).countryOfOrigin(usa).citizenship(usa)
						.favoriteMeals("banana,apple,pear").build(),
				Person.builder().name("Ruth Glanshow").birthday(LocalDate.of(2000, 1, 1)).sex(Sex.FEMALE)
						.eyeColor(Color.RED).hairColor(Color.YELLOW).weight(BigDecimal.valueOf(50))
						.height(BigDecimal.valueOf(120)).countryOfOrigin(usa).citizenship(usa)
						.favoriteMeals("pear,apple,banana").build());
		byte[] multipartFileContent = mapper.writeValueAsBytes(personList);

		personRepository.deleteAll();

		MockMultipartFile multipartFile = new MockMultipartFile(MULTIPART_FILE_NAME, multipartFileContent);
		MvcResult mvcResult = mvc.perform(multipart("/api/person/upload").file(multipartFile))
				.andExpectAll(status().isOk()).andReturn();

		UploadResponse response = mapper.readValue(mvcResult.getResponse().getContentAsString(), UploadResponse.class);
		assertThat(response.succeededEntries()).isEqualTo(personList.size());
		assertThat(response.failedEntries()).isZero();

		assertThat(personRepository.findAll()).asList().usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
				.containsExactlyInAnyOrderElementsOf(personList);
	}

}
