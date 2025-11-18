//Assignment 2 COMP303-002
//Maria Angelina Guerta Ligo 301428777
// Ayushi Parmar 301444746
//10-30-2025

package com.spring.boot.assignment2.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.spring.boot.assignment2.models.Flight;

public interface FlightRepository extends JpaRepository<Flight, Integer> {
	
	//get the flight
	 Optional<Flight> findByOriginAndDestinationAndAirlineNameAndDepartureTime(
		        String origin, String destination, String airlineName, LocalDateTime departureTime
		    );
	 
	 //get the dates and times
	 List<Flight> findByOriginAndDestinationAndAirlineName(
	            String origin, String destination, String airlineName
	    );
}