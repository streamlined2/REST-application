package com.streamlined.restapp.model;

import org.hibernate.annotations.NaturalId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "country")
public class Country {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@NotBlank(message = "Country name should not be blank")
	@Size(min = 3, message = "Country name must be of length 3 or greater")
	@Column(name = "name", nullable = false, unique = true)
	@NaturalId
	private String name;

	@NotNull(message = "Country continent should not be null")
	@Enumerated(EnumType.STRING)
	@Column(name = "continent", nullable = false)
	private Continent continent;

	@NotBlank(message = "Country capital should not be blank")
	@Size(min = 3, message = "Country capital must be of length 3 or greater")
	@Column(name = "capital", nullable = false)
	private String capital;

	@Positive(message = "Country population should be positive value")
	@Column(name = "population")
	private int population;

	@Positive(message = "Country square should be positive value")
	@Column(name = "square")
	private double square;

}
