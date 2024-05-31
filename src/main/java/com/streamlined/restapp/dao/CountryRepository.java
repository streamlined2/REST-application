package com.streamlined.restapp.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.streamlined.restapp.data.Country;

@Repository
public interface CountryRepository extends CrudRepository<Country, Long> {

	Optional<Country> findByName(String name);
	
	void deleteByName(String name);

	@Query("select c from Country c where c.name in :names")
	Iterable<Country> getAllByName(@Param("names") List<String> names);

}
