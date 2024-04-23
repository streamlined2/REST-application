package com.streamlined.restapp.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.streamlined.restapp.model.Country;

@Repository
public interface CountryRepository extends CrudRepository<Country, Long> {
}
