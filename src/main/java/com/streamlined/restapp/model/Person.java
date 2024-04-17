package com.streamlined.restapp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Person {

	@EqualsAndHashCode.Include
	@NotBlank(message = "Person name should not be blank")
	@Size(min = 3, message = "Person name should be of length 3 or greater")
	private String name;

	@EqualsAndHashCode.Include
	@JsonFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "Person birthday should not be null")
	@Past(message = "Person birthday should belong to past")
	private LocalDate birthday;

	@NotNull(message = "Person sex should not be null")
	private Sex sex;

	@NotNull(message = "Person eye color should not be null")
	private Color eyeColor;

	@NotNull(message = "Person hair color should not be null")
	private Color hairColor;

	@DecimalMin(value = "50", message = "Person weight should not be less than 50 kg")
	@DecimalMax(value = "150", message = "Person weight should not be greater than 150 kg")
	private BigDecimal weight;

	@DecimalMin(value = "60", message = "Person height should not be less than 60 cm")
	@DecimalMax(value = "220", message = "Person height should not be greater than 220 cm")
	private BigDecimal height;

	@NotNull(message = "Country of origin should not be null")
	private Country countryOfOrigin;

	@NotNull(message = "Country of citizenship should not be null")
	private Country citizenship;

	@NotBlank(message = "List of favorite meals should not be blank")
	@Pattern(regexp = "\\w{3,}(,\\w{3,})*", message = "Meals should be of length 3 or greater and separated by commas")
	private String favoriteMeals;

}
