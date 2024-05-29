package com.streamlined.restapp.dto;

import java.util.Objects;

import com.streamlined.restapp.data.Continent;

import lombok.Builder;

@Builder
public record CountryDto(Long id, String name, Continent continent, String capital, int population, double square) {

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CountryDto dto) {
			return Objects.equals(id, dto.id);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}
