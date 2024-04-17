package com.streamlined.restapp.model;

import org.springframework.stereotype.Component;

@Component
public class PersonMapper {

	public PersonDto toDto(Person person) {
		return PersonDto.builder().name(person.getName()).birthday(person.getBirthday()).sex(person.getSex())
				.eyeColor(person.getEyeColor()).hairColor(person.getHairColor()).weight(person.getWeight())
				.height(person.getHeight()).countryOfOrigin(person.getCountryOfOrigin())
				.citizenship(person.getCitizenship()).favoriteMeals(person.getFavoriteMeals()).build();
	}

	public Person toEntity(PersonDto person) {
		return Person.builder().name(person.name()).birthday(person.birthday()).sex(person.sex())
				.eyeColor(person.eyeColor()).hairColor(person.hairColor()).weight(person.weight())
				.height(person.height()).countryOfOrigin(person.countryOfOrigin()).citizenship(person.citizenship())
				.favoriteMeals(person.favoriteMeals()).build();
	}

}
