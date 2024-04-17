package com.streamlined.restapp.model;

import java.util.Objects;

import com.streamlined.restapp.model.Country.Continent;

import lombok.Builder;

@Builder
public record CountryDto(String name, Continent continent, String capital, int population, double square) {

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CountryDto dto) {
			return Objects.equals(name, dto.name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

}
