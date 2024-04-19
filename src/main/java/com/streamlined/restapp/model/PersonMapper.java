package com.streamlined.restapp.model;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PersonMapper {

	private final CountryMapper countryMapper;

	public PersonDto toDto(Person person) {
		return PersonDto.builder().id(person.getId()).name(person.getName()).birthday(person.getBirthday())
				.sex(person.getSex()).eyeColor(person.getEyeColor()).hairColor(person.getHairColor())
				.weight(person.getWeight()).height(person.getHeight())
				.countryOfOrigin(countryMapper.toDto(person.getCountryOfOrigin()))
				.citizenship(countryMapper.toDto(person.getCitizenship())).favoriteMeals(person.getFavoriteMeals())
				.build();
	}

	public EssentialPersonDto toListDto(Person person) {
		return EssentialPersonDto.builder().id(person.getId()).name(person.getName()).birthday(person.getBirthday())
				.sex(person.getSex()).eyeColor(person.getEyeColor()).height(person.getHeight()).build();
	}

	public Person toEntity(PersonDto person) {
		return Person.builder().id(person.id()).name(person.name()).birthday(person.birthday()).sex(person.sex())
				.eyeColor(person.eyeColor()).hairColor(person.hairColor()).weight(person.weight())
				.height(person.height()).countryOfOrigin(countryMapper.toEntity(person.countryOfOrigin()))
				.citizenship(countryMapper.toEntity(person.citizenship())).favoriteMeals(person.favoriteMeals())
				.build();
	}

}
