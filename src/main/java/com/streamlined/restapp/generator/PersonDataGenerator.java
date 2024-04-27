package com.streamlined.restapp.generator;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.streamlined.restapp.model.Color;
import com.streamlined.restapp.model.Continent;
import com.streamlined.restapp.model.Country;
import com.streamlined.restapp.model.Person;
import com.streamlined.restapp.model.Sex;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PersonDataGenerator {

	private static final Path RESULT_FILE_DIRECTORY = Path.of("src", "main", "resources", "data");
	private static final String FILE_NAME = "data";
	private static final String FILE_EXTENSION = ".json";
	private static final int PERSON_COUNT = 1_000;
	private static final int FILE_COUNT = 1;

	private static final double MIN_WEIGHT = 45;
	private static final double MAX_WEIGHT = 155;
	private static final double MIN_HEIGHT = 55;
	private static final double MAX_HEIGHT = 225;
	private static final int MIN_MEALS_COUNT = 3;
	private static final LocalDate BIRTHDAY_START = LocalDate.of(1980, 1, 1);
	private static final long BIRTHDAY_RANGE = 45 * ChronoUnit.YEARS.getDuration().toDays();

	private final List<Country> countries = List.of(
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
					.square(603628).build(),
			Country.builder().id(19L).name("Philippines").continent(Continent.ASIA).capital("Manila")
					.population(114163719).square(343448).build(),
			Country.builder().id(20L).name("Japan").continent(Continent.ASIA).capital("Tokyo").population(123970000)
					.square(377975).build(),
			Country.builder().id(21L).name("Peru").continent(Continent.SOUTH_AMERICA).capital("Lima")
					.population(34352720).square(1285216).build());

	private final List<String> meals = List.of("apple", "pear", "grape", "banana", "watermelon");

	private final List<String> firstNames = List.of("Charley", "Jess", "Tom", "Hiram", "Perry", "Nathan", "Claude",
			"Oliver", "Eli", "Amos", "Cecil", "Guy", "Milton", "Vernon", "Alexander", "Clarence", "Howard", "Jasper",
			"Walter", "Mack", "Hubert", "Alfred", "Martin", "Oliver", "Joseph", "Wallace", "Eugene", "Hugh", "Earl");
	private final List<String> lastNames = List.of("Thomas", "Newton", "Kent", "Horton", "Burgess", "Gleason", "Hahn",
			"Sorensen", "Elliott", "Summers", "Webb", "Cash", "Nielsen", "Jensen", "Brady", "Adams", "Griffith",
			"Nichols", "Steiner", "Denton", "Jennings", "Emery", "Ellis", "Ackerman", "Crowley", "Justice", "Helton",
			"Waller", "Wallace");

	private final ObjectMapper mapper;
	private final Random random;

	public PersonDataGenerator() {
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		random = new SecureRandom();
	}

	public void createData(Path resultFileDirectory, int totalNumberOfPersons, int numberOfFiles) {
		prepareResultFileDirectory(resultFileDirectory);
		final int personsPerFile = (int) Math.ceil((double) totalNumberOfPersons / numberOfFiles);
		int numberOfPersons = totalNumberOfPersons;
		for (int fileNumber = 0; fileNumber < numberOfFiles && numberOfPersons > 0; fileNumber++) {
			final int restPersons = Math.min(personsPerFile, numberOfPersons);
			createDataForOneFile(resultFileDirectory, restPersons, fileNumber);
			numberOfPersons -= restPersons;
		}
	}

	private void createDataForOneFile(Path resultFileDirectory, int numberOfPersons, int fileNumber) {
		try (var writer = Files.newBufferedWriter(getResultFile(resultFileDirectory, fileNumber),
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
			List<Person> persons = new ArrayList<>(numberOfPersons);
			for (int k = 0; k < numberOfPersons; k++) {
				persons.add(createPerson());
			}
			mapper.writeValue(writer, persons);
		} catch (IOException e) {
			log.error("Can't write generated data");
			throw new GeneratorException("Can't write generated data", e);
		}
	}

	private void prepareResultFileDirectory(Path resultFileDirectory) {
		try {
			Files.createDirectories(resultFileDirectory);
		} catch (IOException e) {
			log.error("Can't create directory for data to generate");
			throw new GeneratorException("Can't create directory for data to generate", e);
		}
	}

	private Path getResultFile(Path resultFileDirectory, int fileNumber) {
		return new File(resultFileDirectory.toFile(), FILE_NAME + fileNumber + FILE_EXTENSION).toPath();
	}

	private Person createPerson() {
		return Person.builder().name(getName()).birthday(getBirthday()).sex(getSex()).eyeColor(getColor())
				.hairColor(getColor()).weight(getWeight()).height(getHeight()).countryOfOrigin(getCountry())
				.citizenship(getCountry()).favoriteMeals(getFavoriteMeals()).build();
	}

	private Country getCountry() {
		return countries.get(random.nextInt(countries.size()));
	}

	private Color getColor() {
		return Color.values()[random.nextInt(Color.values().length)];
	}

	private Sex getSex() {
		return Sex.values()[random.nextInt(Sex.values().length)];
	}

	private BigDecimal getWeight() {
		return BigDecimal.valueOf(MIN_WEIGHT + random.nextDouble(MAX_WEIGHT - MIN_WEIGHT));
	}

	private BigDecimal getHeight() {
		return BigDecimal.valueOf(MIN_HEIGHT + random.nextDouble(MAX_HEIGHT - MIN_HEIGHT));
	}

	private String getName() {
		return firstNames.get(random.nextInt(firstNames.size())) + " "
				+ lastNames.get(random.nextInt(lastNames.size()));
	}

	private LocalDate getBirthday() {
		return BIRTHDAY_START.plus(random.nextLong(BIRTHDAY_RANGE), ChronoUnit.DAYS);
	}

	private String getFavoriteMeals() {
		var mealSet = new ArrayList<>(meals);
		Collections.shuffle(mealSet, random);
		int count = MIN_MEALS_COUNT + random.nextInt(meals.size() - MIN_MEALS_COUNT);
		var mealIterator = mealSet.iterator();
		StringBuilder b = new StringBuilder();
		if (count > 0) {
			b.append(mealIterator.next());
			count--;
			while (count > 0) {
				b.append(",").append(mealIterator.next());
				count--;
			}
		}
		return b.toString();
	}

	public static void main(String... args) {
		new PersonDataGenerator().createData(RESULT_FILE_DIRECTORY, PERSON_COUNT, FILE_COUNT);
	}

}
