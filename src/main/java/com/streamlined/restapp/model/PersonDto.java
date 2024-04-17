package com.streamlined.restapp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import lombok.Builder;

@Builder
public record PersonDto(String name, LocalDate birthday, Sex sex, Color eyeColor, Color hairColor, BigDecimal weight,
		BigDecimal height, Country countryOfOrigin, Country citizenship, String favoriteMeals) {

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PersonDto dto) {
			return Objects.equals(name, dto.name) && Objects.equals(birthday, dto.birthday);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, birthday);
	}

}
