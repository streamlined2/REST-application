package com.streamlined.restapp.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.streamlined.restapp.model.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
}
