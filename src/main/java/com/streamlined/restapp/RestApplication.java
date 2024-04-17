package com.streamlined.restapp;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.streamlined.restapp.model.Color;
import com.streamlined.restapp.model.Continent;
import com.streamlined.restapp.model.CountryDto;
import com.streamlined.restapp.model.PersonDto;
import com.streamlined.restapp.model.Sex;
import com.streamlined.restapp.service.CountryService;
import com.streamlined.restapp.service.PersonService;

import lombok.RequiredArgsConstructor;

@SpringBootApplication
@RequiredArgsConstructor
public class RestApplication implements CommandLineRunner {

	private final PersonService personService;
	private final CountryService countryService;

	public static void main(String[] args) {
		SpringApplication.run(RestApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		var person = personService.getPersonById(1L);
		System.out.printf("Person: %s%n", person.toString());
		var country = countryService.getCountryById(1L);
		System.out.printf("Country: %s%n", country);

		personService.save(1L,
				PersonDto.builder().id(1L).name("Nick James").birthday(LocalDate.of(1975, 1, 1)).sex(Sex.MALE)
						.eyeColor(Color.GRAY).hairColor(Color.YELLOW).weight(BigDecimal.valueOf(67))
						.height(BigDecimal.valueOf(180)).countryOfOrigin(country.orElseThrow())
						.citizenship(country.orElseThrow()).favoriteMeals("apple,watermelon").build());
		countryService.save(1L, CountryDto.builder().id(1L).name("United Kingdom").continent(Continent.EUROPE)
				.capital("London").population(60000000).square(1745813.01).build());

		countryService.getAllCountries().forEach(System.out::println);
		personService.getAllPersons().forEach(System.out::println);

		personService.removeById(1L);
		countryService.removeById(1L);

		countryService.getAllCountries().forEach(System.out::println);
		personService.getAllPersons().forEach(System.out::println);
	}

}
