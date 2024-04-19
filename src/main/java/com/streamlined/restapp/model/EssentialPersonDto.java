package com.streamlined.restapp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import lombok.Builder;

@Builder
public record EssentialPersonDto(Long id, String name, LocalDate birthday, Sex sex, Color eyeColor, BigDecimal height) {

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EssentialPersonDto dto) {
			return Objects.equals(id, dto.id);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}
