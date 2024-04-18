package com.streamlined.restapp.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.streamlined.restapp.exception.EntityNotFoundException;
import com.streamlined.restapp.exception.IncorrectDataException;
import com.streamlined.restapp.model.Color;
import com.streamlined.restapp.model.Person;
import com.streamlined.restapp.model.Sex;

@Repository
public class PersonRepository {

	private final CountryRepository countryRepository;
	private final Map<Long, Person> persons;

	public PersonRepository(CountryRepository countryRepository) {
		this.countryRepository = countryRepository;
		persons = new HashMap<>();
		persons.putAll(Map.ofEntries(
				Map.entry(1L, Person.builder().id(1L).name("Charley Thomas").birthday(LocalDate.of(1970, 12, 10))
						.sex(Sex.MALE).eyeColor(Color.GRAY).hairColor(Color.YELLOW).weight(BigDecimal.valueOf(67))
						.height(BigDecimal.valueOf(180)).countryOfOrigin(countryRepository.findById(1L).orElseThrow())
						.citizenship(countryRepository.findById(1L).orElseThrow()).favoriteMeals("apple,watermelon")
						.build()),
				Map.entry(2L, Person.builder().id(2L).name("Jess Newton").birthday(LocalDate.of(1985, 2, 15))
						.sex(Sex.FEMALE).eyeColor(Color.GREEN).hairColor(Color.RED).weight(BigDecimal.valueOf(89))
						.height(BigDecimal.valueOf(165)).countryOfOrigin(countryRepository.findById(2L).orElseThrow())
						.citizenship(countryRepository.findById(2L).orElseThrow()).favoriteMeals("banana,pear,apple")
						.build()),
				Map.entry(3L, Person.builder().id(3L).name("Tom Kent").birthday(LocalDate.of(1987, 6, 4))
						.sex(Sex.FEMALE).eyeColor(Color.BLUE).hairColor(Color.BROWN).weight(BigDecimal.valueOf(90))
						.height(BigDecimal.valueOf(186)).countryOfOrigin(countryRepository.findById(3L).orElseThrow())
						.citizenship(countryRepository.findById(3L).orElseThrow())
						.favoriteMeals("pear,grape,watermelon,apple").build()),
				Map.entry(4L, Person.builder().id(4L).name("Hiram Horton").birthday(LocalDate.of(1990, 5, 1))
						.sex(Sex.MALE).eyeColor(Color.GRAY).hairColor(Color.YELLOW).weight(BigDecimal.valueOf(100))
						.height(BigDecimal.valueOf(165)).countryOfOrigin(countryRepository.findById(4L).orElseThrow())
						.citizenship(countryRepository.findById(4L).orElseThrow())
						.favoriteMeals("apple,watermelon,pear").build()),
				Map.entry(5L, Person.builder().id(5L).name("Jess Burgess").birthday(LocalDate.of(1995, 3, 2))
						.sex(Sex.FEMALE).eyeColor(Color.GREEN).hairColor(Color.BLACK).weight(BigDecimal.valueOf(96))
						.height(BigDecimal.valueOf(150)).countryOfOrigin(countryRepository.findById(5L).orElseThrow())
						.citizenship(countryRepository.findById(5L).orElseThrow())
						.favoriteMeals("banana,apple,pear,grape").build()),
				Map.entry(6L, Person.builder().id(6L).name("Perry Gleason").birthday(LocalDate.of(1965, 9, 4))
						.sex(Sex.MALE).eyeColor(Color.BLACK).hairColor(Color.RED).weight(BigDecimal.valueOf(78))
						.height(BigDecimal.valueOf(195)).countryOfOrigin(countryRepository.findById(4L).orElseThrow())
						.citizenship(countryRepository.findById(4L).orElseThrow())
						.favoriteMeals("pear,watermelon,grape").build()),
				Map.entry(7L, Person.builder().id(7L).name("Nathan Hahn").birthday(LocalDate.of(1987, 4, 9))
						.sex(Sex.MALE).eyeColor(Color.BLUE).hairColor(Color.BROWN).weight(BigDecimal.valueOf(65))
						.height(BigDecimal.valueOf(145)).countryOfOrigin(countryRepository.findById(3L).orElseThrow())
						.citizenship(countryRepository.findById(3L).orElseThrow())
						.favoriteMeals("apple,banana,pear,grape").build()),
				Map.entry(8L, Person.builder().id(8L).name("Claude Sorensen").birthday(LocalDate.of(1989, 10, 5))
						.sex(Sex.MALE).eyeColor(Color.BLACK).hairColor(Color.BLUE).weight(BigDecimal.valueOf(87))
						.height(BigDecimal.valueOf(120)).countryOfOrigin(countryRepository.findById(2L).orElseThrow())
						.citizenship(countryRepository.findById(2L).orElseThrow())
						.favoriteMeals("watermelon,apple,grape").build()),
				Map.entry(9L, Person.builder().id(9L).name("Oliver Elliott").birthday(LocalDate.of(1978, 8, 5))
						.sex(Sex.MALE).eyeColor(Color.BROWN).hairColor(Color.BLACK).weight(BigDecimal.valueOf(99))
						.height(BigDecimal.valueOf(130)).countryOfOrigin(countryRepository.findById(1L).orElseThrow())
						.citizenship(countryRepository.findById(1L).orElseThrow())
						.favoriteMeals("pear,watermelon,apple").build()),
				Map.entry(10L, Person.builder().id(10L).name("Eli Summers").birthday(LocalDate.of(1999, 1, 9))
						.sex(Sex.FEMALE).eyeColor(Color.YELLOW).hairColor(Color.GREEN).weight(BigDecimal.valueOf(86))
						.height(BigDecimal.valueOf(156)).countryOfOrigin(countryRepository.findById(2L).orElseThrow())
						.citizenship(countryRepository.findById(2L).orElseThrow()).favoriteMeals("grape,pear,banana")
						.build()),
				Map.entry(11L, Person.builder().id(11L).name("Amos Webb").birthday(LocalDate.of(1996, 8, 5))
						.sex(Sex.MALE).eyeColor(Color.RED).hairColor(Color.BLUE).weight(BigDecimal.valueOf(96))
						.height(BigDecimal.valueOf(180)).countryOfOrigin(countryRepository.findById(3L).orElseThrow())
						.citizenship(countryRepository.findById(3L).orElseThrow())
						.favoriteMeals("watermelon,apple,pear,banana").build())));
	}

	public Iterable<Person> findAll() {
		return persons.values();
	}

	public Optional<Person> findById(Long id) {
		return Optional.ofNullable(persons.get(id));
	}

	public Person save(Long id, Person person) {
		if (isDuplicatePersonNameAndBirthday(id, person)) {
			throw new IncorrectDataException("Person list already contains name and birthday: %s %s"
					.formatted(person.getName(), person.getBirthday()));
		}
		person.setId(id);
		persons.put(id, person);
		return person;
	}

	private boolean isDuplicatePersonNameAndBirthday(Long id, Person person) {
		return !persons.values().stream()
				.filter(c -> !Objects.equals(c.getId(), id) && Objects.equals(c.getName(), person.getName())
						&& Objects.equals(c.getBirthday(), person.getBirthday()))
				.findAny().isEmpty();
	}

	public void deleteById(Long id) {
		if (findById(id).isEmpty()) {
			throw new EntityNotFoundException("Person with id %d not found".formatted(id));
		}
		persons.remove(id);
	}

}
