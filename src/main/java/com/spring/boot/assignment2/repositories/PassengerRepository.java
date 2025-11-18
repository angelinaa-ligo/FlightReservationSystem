//Assignment 2 COMP303-002
//Maria Angelina Guerta Ligo 301428777
// Ayushi Parmar 301444746
//10-30-2025

package com.spring.boot.assignment2.repositories;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import com.spring.boot.assignment2.models.Passenger;

public interface PassengerRepository extends CrudRepository<Passenger, Integer> {

	Optional<Passenger> findByUsername(String username);
}
