package com.streamlined.restapp.data;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.annotations.NaturalId;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Entity
@Table(name = "person", uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "birthday" }) })
public class Person {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@NotBlank(message = "Person name should not be blank")
	@Size(min = 3, message = "Person name should be of length 3 or greater")
	@Column(name = "name", nullable = false)
	@NaturalId
	private String name;

	@JsonFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "Person birthday should not be null")
	@Past(message = "Person birthday should belong to past")
	@Temporal(TemporalType.DATE)
	@Column(name = "birthday", nullable = false)
	@NaturalId
	private LocalDate birthday;

	@NotNull(message = "Person sex should not be null")
	@Enumerated(EnumType.STRING)
	@Column(name = "sex", nullable = false)
	private Sex sex;

	@NotNull(message = "Person eye color should not be null")
	@Enumerated(EnumType.STRING)
	@Column(name = "eye_color")
	private Color eyeColor;

	@NotNull(message = "Person hair color should not be null")
	@Enumerated(EnumType.STRING)
	@Column(name = "hair_color")
	private Color hairColor;

	@DecimalMin(value = "50", message = "Person weight should not be less than 50 kg")
	@DecimalMax(value = "150", message = "Person weight should not be greater than 150 kg")
	@Column(name = "weight")
	private BigDecimal weight;

	@DecimalMin(value = "60", message = "Person height should not be less than 60 cm")
	@DecimalMax(value = "220", message = "Person height should not be greater than 220 cm")
	@Column(name = "height")
	private BigDecimal height;

	@NotNull(message = "Country of origin should not be null")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(nullable = false, name = "origin")
	private Country countryOfOrigin;

	@NotNull(message = "Country of citizenship should not be null")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(nullable = false, name = "citizenship")
	private Country citizenship;

	@NotBlank(message = "List of favorite meals should not be blank")
	@Pattern(regexp = "\\w{3,}(,\\w{3,})*", message = "Meals should be of length 3 or greater and separated by commas")
	@Column(name = "meals")
	private String favoriteMeals;

}
