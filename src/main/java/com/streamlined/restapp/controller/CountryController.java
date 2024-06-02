package com.streamlined.restapp.controller;

import java.util.stream.Stream;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.streamlined.restapp.Utilities;
import com.streamlined.restapp.dto.CountryDto;
import com.streamlined.restapp.exception.EntityNotFoundException;
import com.streamlined.restapp.service.country.CountryService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * Controller class for country entity
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/country")
public class CountryController {

	private final CountryService countryService;

	@GetMapping
	public Stream<CountryDto> getAllCountries() {
		return countryService.getAllCountries();
	}

	@GetMapping("/{id}")
	public CountryDto getCountryById(@PathVariable Long id) {
		return countryService.getCountryById(id)
				.orElseThrow(() -> new EntityNotFoundException("Country with id %d not found".formatted(id)));
	}

	@PostMapping
	public ResponseEntity<Void> addCountry(@RequestBody CountryDto country, HttpServletRequest servletRequest) {
		CountryDto savedCountry = countryService.save(country);
		return ResponseEntity.created(Utilities.getResourceURI(servletRequest, savedCountry.id())).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> updateCountry(@PathVariable Long id, @RequestBody CountryDto country) {
		countryService.save(id, country);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCountry(@PathVariable Long id) {
		countryService.removeById(id);
		return ResponseEntity.ok().build();
	}

}
