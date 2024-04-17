package com.streamlined.restapp.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Country {

	public enum Continent {
		AFRICA, ASIA, EUROPE, NORTH_AMERICA, SOUTH_AMERICA, ANTARCTICA, AUSTRALIA
	}

	@EqualsAndHashCode.Include
	@NotBlank(message = "Country name should not be blank")
	@Size(min = 3, message = "Country name must be of length 3 or greater")
	private String name;
	
	@NotNull(message = "Country continent should not be null")
	private Continent continent;
	
	@NotBlank(message = "Country capital should not be blank")
	@Size(min = 3, message = "Country capital must be of length 3 or greater")
	private String capital;
	
	@Positive(message = "Country population should be positive value")
	private int population;
	
	@Positive(message = "Country square should be positive value")
	private double square;

}
